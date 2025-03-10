package io.confluent.vehicles;

import fleet_mgmt.fleet_mgmt_sensors;
import io.confluent.model.VehicleStats;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write a solution to calculate the number of vehicles for each engine usage category: HIGH, NORMAL, LOW
 * https://leetcode.com/problems/count-salary-categories/description/
 */
public class VehicleStatsComponent {

    private static final String JAAS_STRING =
            "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleStatsComponent.class);

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public void exec(String[] args) throws Exception {
        LOGGER.info("Starting...");

        final var parameters = ParameterTool.fromArgs(args);
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parameters.getInt("parallelism", 2));
        env.enableCheckpointing(60_000);
        env.disableOperatorChaining();
        // env.getConfig().disableGenericTypes();
        // env.getConfig().enableForceAvro();

        // Set file-based checkpoint storage to avoid memory limits
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        final var checkpointStorage = new FileSystemCheckpointStorage("file:///tmp/flink-checkpoints");
        env.getCheckpointConfig().setCheckpointStorage(checkpointStorage);

        final var jaasIn =
                String.format(JAAS_STRING, parameters.get("consumer.key"), parameters.get("consumer.secret"));

        final var jaasOut =
                String.format(JAAS_STRING, parameters.get("producer.key"), parameters.get("producer.secret"));

        final var deserializationSchema = ConfluentRegistryAvroDeserializationSchema.forGeneric(
                fleet_mgmt_sensors.getClassSchema(),
                parameters.get("schema.registry.url"),
                Map.of(
                        "basic.auth.credentials.source",
                        "USER_INFO",
                        "basic.auth.user.info",
                        parameters.get("basic.auth.user.info")));

        final var serializationSchema = KafkaRecordSerializationSchema.builder()
                .setTopic(parameters.get("out.topic"))
                // .setKeySerializationSchema(new SimpleStringSchema())
                .setValueSerializationSchema(ConfluentRegistryAvroSerializationSchema.forSpecific(
                        VehicleStats.class,
                        parameters.get("out.topic") + "-value",
                        parameters.get("schema.registry.url"),
                        Map.of(
                                "basic.auth.credentials.source",
                                "USER_INFO",
                                "basic.auth.user.info",
                                parameters.get("basic.auth.user.info"))))
                .build();

        final KafkaSource<GenericRecord> source = KafkaSource.<GenericRecord>builder()
                .setBootstrapServers(parameters.get("brokers"))
                .setGroupId(parameters.get("consumer.group"))
                .setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.jaas.config", jaasIn)
                .setProperty("sasl.mechanism", "PLAIN")
                .setTopics(parameters.get("in.topic"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(deserializationSchema)
                .build();

        final Sink<VehicleStats> sink = KafkaSink.<VehicleStats>builder()
                .setBootstrapServers(parameters.get("brokers"))
                .setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.jaas.config", jaasOut)
                .setProperty("sasl.mechanism", "PLAIN")
                .setRecordSerializer(serializationSchema)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        // Define source with watermarks
        final DataStreamSource<GenericRecord> data = env.fromSource(
                source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10)), "Kafka Source");

        // higher than 5% threshold
        data.filter(record -> getAverageRpm(record) >= 4750 && getEngineTemp(record) >= 240)
                .windowAll(TumblingEventTimeWindows.of(Duration.ofMinutes(10)))
                .process(getUsage("HIGH"))
                .keyBy(VehicleStats::getUsageCategory)
                .sinkTo(sink);

        // between 5% to 20% threshold
        data.filter(record -> (getAverageRpm(record) > 4000 && getAverageRpm(record) <= 4750)
                        || (getEngineTemp(record) > 200 && getEngineTemp(record) <= 240))
                .windowAll(TumblingEventTimeWindows.of(Duration.ofMinutes(10)))
                .process(getUsage("NORMAL"))
                .keyBy(VehicleStats::getUsageCategory)
                .sinkTo(sink);

        // lower than 20% threshold
        data.filter(record -> getAverageRpm(record) <= 4000 && getEngineTemp(record) <= 200)
                .windowAll(TumblingEventTimeWindows.of(Duration.ofMinutes(10)))
                .process(getUsage("LOW"))
                .keyBy(VehicleStats::getUsageCategory)
                .sinkTo(sink);

        // Execute program, beginning computation.
        env.execute("Kafka Sensors");
    }

    private static ProcessAllWindowFunction<GenericRecord, VehicleStats, TimeWindow> getUsage(String usageCategory) {
        return new ProcessAllWindowFunction<>() {
            @Override
            public void process(Context context, Iterable<GenericRecord> elements, Collector<VehicleStats> out) {
                final TimeWindow window = context.window();

                final var windowStart = formatter.format(Instant.ofEpochMilli(window.getStart()));
                final var windowEnd = formatter.format(Instant.ofEpochMilli(window.getEnd()));
                final var vehicleCount =
                        StreamSupport.stream(elements.spliterator(), false).count();

                out.collect(new VehicleStats(usageCategory, vehicleCount, windowStart, windowEnd));
            }
        };
    }

    private static Integer getAverageRpm(GenericRecord value) {
        return (Integer) value.get("average_rpm");
    }

    private static Integer getEngineTemp(GenericRecord value) {
        return (Integer) value.get("engine_temperature");
    }
}
