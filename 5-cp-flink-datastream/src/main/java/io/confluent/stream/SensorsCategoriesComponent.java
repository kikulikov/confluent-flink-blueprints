package io.confluent.stream;

import fleet_mgmt.fleet_mgmt_sensors;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * https://leetcode.com/problems/count-salary-categories/description/
 */
public class SensorsCategoriesComponent {

    private static final String JAAS_STRING =
            "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";
    // private static final Logger LOGGER = LoggerFactory.getLogger(SensorsCategoriesComponent.class);

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public void exec(String[] args) throws Exception {

        final var parameters = ParameterTool.fromArgs(args);
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parameters.getInt("parallelism", 2));
        env.enableCheckpointing(60_000);
        env.disableOperatorChaining();
        // Set file-based checkpoint storage to avoid memory limits
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        env.getCheckpointConfig()
                .setCheckpointStorage(new FileSystemCheckpointStorage("file:///tmp/flink-checkpoints"));

        final var jaasIn =
                String.format(JAAS_STRING, parameters.get("consumer.key"), parameters.get("consumer.secret"));

        final var gen = ConfluentRegistryAvroDeserializationSchema.forGeneric(
                fleet_mgmt_sensors.getClassSchema(),
                parameters.get("schema.registry.url"),
                Map.of(
                        "basic.auth.credentials.source",
                        "USER_INFO",
                        "basic.auth.user.info",
                        parameters.get("basic.auth.user.info")));

        final KafkaSource<GenericRecord> source = KafkaSource.<GenericRecord>builder()
                .setBootstrapServers(parameters.get("brokers"))
                .setGroupId(parameters.get("consumer.group"))
                .setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.jaas.config", jaasIn)
                .setProperty("sasl.mechanism", "PLAIN")
                .setTopics(parameters.get("in.topic"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(gen)
                .build();

        // Define source with watermarks
        final DataStreamSource<GenericRecord> data = env.fromSource(
                source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10)), "Kafka Source");

        data.filter(record -> getAverageRpm(record) > 4900 && getEngineTemp(record) > 240)
                .windowAll(TumblingEventTimeWindows.of(Duration.ofMinutes(10)))
                .process(new ProcessAllWindowFunction<GenericRecord, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<GenericRecord> elements, Collector<String> out) {
                        final TimeWindow window = context.window();

                        final var windowStart = formatter.format(Instant.ofEpochMilli(window.getStart()));
                        final var windowEnd = formatter.format(Instant.ofEpochMilli(window.getEnd()));
                        final var count = StreamSupport.stream(elements.spliterator(), false)
                                .count();
                        final var result = String.format("%s, %s, %s, %d", windowStart, windowEnd, "HIGH USAGE", count);

                        out.collect(result);
                    }
                })
                .print();

        // Execute program, beginning computation.
        env.execute("Kafka Sensors");
    }

    private static Integer getAverageRpm(GenericRecord value) {
        return (Integer) value.get("average_rpm");
    }

    private static Integer getEngineTemp(GenericRecord value) {
        return (Integer) value.get("engine_temperature");
    }
}
