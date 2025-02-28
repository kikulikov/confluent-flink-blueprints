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

    private static final Logger log = LoggerFactory.getLogger(KafkaCopier.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting Kafka Copier...");
        ParameterTool parameters = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.disableOperatorChaining();
        env.setParallelism(parameters.getInt("parallelism", 2));
        env.enableCheckpointing(1000);

        KafkaSource<String> source =
                KafkaSource.<String>builder()
                        .setBootstrapServers(parameters.get("brokers"))
                        .setTopics(parameters.get("in-topic"))
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new SimpleStringSchema())
                        .build();

        log.info("Reading from Kafka topic: " + parameters.get("in-topic"));

        DataStreamSource<String> data =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        data.keyBy(
                        (KeySelector<String, String>)
                                value -> {
                                    log.info("Keying by: " + value);
                                    return value;
                                })
                .process(new Stateful());

        KafkaSink<String> sink =
                KafkaSink.<String>builder()
                        .setBootstrapServers(parameters.get("brokers"))
                        .setRecordSerializer(
                                KafkaRecordSerializationSchema.builder()
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
