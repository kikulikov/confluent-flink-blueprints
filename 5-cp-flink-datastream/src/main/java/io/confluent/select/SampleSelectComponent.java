package io.confluent.select;

import fleet_mgmt.fleet_mgmt_location;
import java.time.Duration;
import java.util.Map;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleSelectComponent {

    private static final String JAAS_STRING =
            "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSelectComponent.class);

    public void exec(String[] args) throws Exception {
        LOGGER.info("Starting...");

        // Parse command-line arguments
        final var parameters = ParameterTool.fromArgs(args);

        // Set up the Flink execution environment
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parameters.getInt("parallelism", 1)); // Set parallelism, defaulting to 1 if not provided

        // Construct JAAS authentication string for Kafka security
        final var jaasIn =
                String.format(JAAS_STRING, parameters.get("consumer.key"), parameters.get("consumer.secret"));

        // Configure Avro deserialization schema for consuming messages from Kafka
        final var gen = ConfluentRegistryAvroDeserializationSchema.forSpecific(
                fleet_mgmt_location.class,
                parameters.get("schema.registry.url"),
                Map.of(
                        "basic.auth.credentials.source",
                        "USER_INFO",
                        "basic.auth.user.info",
                        parameters.get("basic.auth.user.info")));

        // Build the Kafka source for consuming fleet management location data
        final KafkaSource<fleet_mgmt_location> source = KafkaSource.<fleet_mgmt_location>builder()
                .setBootstrapServers(parameters.get("brokers")) // Set Kafka brokers
                .setGroupId(parameters.get("consumer.group")) // Set consumer group ID
                .setProperty("security.protocol", "SASL_SSL") // Enable SASL over SSL for authentication
                .setProperty("sasl.jaas.config", jaasIn) // Provide JAAS configuration for authentication
                .setProperty("sasl.mechanism", "PLAIN") // Use PLAIN authentication mechanism
                .setTopics(parameters.get("in.topic")) // Subscribe to the input topic
                .setStartingOffsets(
                        OffsetsInitializer.earliest()) // Start consuming from the earliest available messages
                .setValueOnlyDeserializer(gen) // Deserialize values using the Avro schema
                .build();

        // Define source with watermarks
        final DataStreamSource<fleet_mgmt_location> data = env.fromSource(
                source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10)), "Kafka Source");

        // Print the incoming data stream to the console
        data.print();

        // Execute program, beginning computation.
        env.execute("Kafka Sensors");
    }
}
