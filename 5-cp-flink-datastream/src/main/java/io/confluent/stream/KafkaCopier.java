package io.confluent.stream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCopier {

    private static final String JAAS_STRING =
            "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";
    private static final Logger log = LoggerFactory.getLogger(KafkaCopier.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Kafka Copier...");

        final var parameters = ParameterTool.fromArgs(args);
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parameters.getInt("parallelism", 2));
        env.enableCheckpointing(1000);
        env.disableOperatorChaining();

        final var jaasIn =
                String.format(JAAS_STRING, parameters.get("consumer-key"), parameters.get("consumer-secret"));

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(parameters.get("brokers"))
                .setGroupId(parameters.get("consumer-group"))
                .setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.jaas.config", jaasIn)
                .setProperty("sasl.mechanism", "PLAIN")
                .setTopics(parameters.get("in-topic"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        log.info("Reading from Kafka topic: " + parameters.get("in-topic"));

        final DataStreamSource<String> data = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        data.keyBy((KeySelector<String, String>) value -> {
                    log.info("Keying by: " + value);
                    return value;
                })
                .process(new Stateful());

        final var jaasOut =
                String.format(JAAS_STRING, parameters.get("producer-key"), parameters.get("producer-secret"));

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(parameters.get("brokers"))
                .setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.jaas.config", jaasOut)
                .setProperty("sasl.mechanism", "PLAIN")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(parameters.get("out-topic"))
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        data.sinkTo(sink);

        // Execute program, beginning computation.
        env.execute("Kafka Copier");
    }
}
