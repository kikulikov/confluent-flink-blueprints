package io.confluent.base;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Base test class with common setup for Kafka and Schema Registry
 */
@Testcontainers
public abstract class BaseInfrastructureTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseInfrastructureTest.class);

  static {
    LOGGER.info("Initializing test infrastructure");
  }

  // Kafka configuration
  public static final DockerImageName CP_KAFKA_IMAGE =
      DockerImageName.parse("confluentinc/cp-kafka:7.8.1");

  // Schema Registry configuration
  protected static final DockerImageName SCHEMA_REGISTRY_IMAGE =
      DockerImageName.parse("confluentinc/cp-schema-registry:7.8.1");

  // Shared network for containers
  protected static final Network NETWORK = Network.newNetwork();

  @Container
  protected static final KafkaContainer KAFKA_BROKER = new KafkaContainer(CP_KAFKA_IMAGE)
      .withLogConsumer(new Slf4jLogConsumer(LOGGER))
      .withKraft()
      .withNetwork(NETWORK)
      .withNetworkAliases("kafka")
      .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
      .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
      .withReuse(false)  // Ensure fresh container each time
      .waitingFor(Wait.forListeningPort())
      .withStartupTimeout(Duration.ofMinutes(2));

  @Container
  protected static final GenericContainer<?> SCHEMA_REGISTRY =
      new GenericContainer<>(SCHEMA_REGISTRY_IMAGE)
          .withLogConsumer(new Slf4jLogConsumer(LOGGER))
          .withNetwork(NETWORK)
          .withExposedPorts(8081)
          .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
          .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "kafka:9092")
          .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
          // Disable Schema Registry compatibility check due to
          // https://issues.apache.org/jira/browse/FLINK-33045 and
          // https://issues.apache.org/jira/browse/FLINK-36650
          // Appears to be because the namespace + name doesn't match with what Flink would use
          .withEnv("SCHEMA_REGISTRY_AVRO_COMPATIBILITY_LEVEL", "NONE")
          .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))
          .dependsOn(KAFKA_BROKER);

  // Flink MiniCluster
  protected static MiniClusterWithClientResource flinkCluster;

  @BeforeAll
  public static void setup() throws Exception {
    System.out.println("Starting test infrastructure...");
//    Startables.deepStart(Arrays.asList(KAFKA_BROKER, SCHEMA_REGISTRY)).join();

    // Start Flink MiniCluster
    final var configuration = new Configuration();
    configuration.set(RestOptions.BIND_PORT, "8081-8089");

    flinkCluster =
        new MiniClusterWithClientResource(
            new MiniClusterResourceConfiguration.Builder()
                .setConfiguration(configuration)
                .setNumberSlotsPerTaskManager(4)
                .setNumberTaskManagers(2)
                .build());

    flinkCluster.before();

    LOGGER.info("Test environment setup completed");
  }

  @AfterAll
  public static void teardown() {
    if (flinkCluster != null) {
      flinkCluster.after();
    }
  }

  protected void createKafkaTopic(String topicName) throws Exception {
    // Create Kafka topics using AdminClient
    final var adminProps = new Properties();
    adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER.getBootstrapServers());

    try (AdminClient adminClient = AdminClient.create(adminProps)) {
      // Configure the topic with 1 partition for testing
      final var newTopic = new NewTopic(topicName, 1, (short) 1);
      // Create the topic and wait for it to complete
      adminClient.createTopics(Collections.singleton(newTopic)).all().get();
      LOGGER.info("Created Kafka topic: {}", topicName);
    } catch (Exception e) {
      LOGGER.error("Failed to create Kafka topic: {}", topicName, e);
      throw e;
    }
  }

  protected <T extends SpecificRecord> void produceRecords(String topicName, List<T> records)
      throws InterruptedException, ExecutionException {
    try (KafkaProducer<String, T> producer = createAvroProducer()) {

      for (T t : records) {
        ProducerRecord<String, T> record = new ProducerRecord<>(topicName, t.get(0).toString(), t);
        producer.send(record).get();
      }

      producer.flush();
      LOGGER.info("Finished sending {} transactions to Kafka", records.size());
    }
  }

  private <T extends SpecificRecord> KafkaProducer<String, T> createAvroProducer() {
    final var props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER.getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

    final var schemaRegistryUrl = String.format("http://%s:%d",
        SCHEMA_REGISTRY.getHost(), SCHEMA_REGISTRY.getMappedPort(8081));

    props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

    return new KafkaProducer<>(props);
  }

  protected <T extends SpecificRecord> List<T> consumeRecords(String topicName) {

    try (KafkaConsumer<String, T> consumer = createAvroConsumer()) {
      consumer.subscribe(Collections.singletonList(topicName));
      LOGGER.info("Subscribed to topic: {}", topicName);

      final List<T> result = new ArrayList<>();
      int emptyPolls = 0, maxEmptyPolls = 10;

      // Poll for records with a timeout
      while (emptyPolls < maxEmptyPolls) {
        LOGGER.info("Polling for records (empty polls so far: {}/{})", emptyPolls, maxEmptyPolls);
        final ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(500));

        if (records.isEmpty()) {
          emptyPolls++;
          LOGGER.info("Empty poll #{}/{}", emptyPolls, maxEmptyPolls);
        } else {
          emptyPolls = 0;
          LOGGER.info("Received {} records", records.count());
          for (ConsumerRecord<String, T> record : records) {
            result.add(record.value());
          }
        }
      }

      LOGGER.info("Finished consuming. Retrieved {} approved transactions", result.size());
      return result;
    }
  }

  private <T extends SpecificRecord> KafkaConsumer<String, T> createAvroConsumer() {
    final var props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());

    final var schemaRegistryUrl = String.format("http://%s:%d",
        SCHEMA_REGISTRY.getHost(), SCHEMA_REGISTRY.getMappedPort(8081));

    props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

    // Ensure specific Avro reader is enabled
    props.put("specific.avro.reader", "true");

    // Set the subject naming strategy
    props.put("value.subject.name.strategy", "io.confluent.kafka.serializers.subject.TopicNameStrategy");

    return new KafkaConsumer<>(props);
  }
}
