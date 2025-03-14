package io.confluent.vehicles;

import fleet_mgmt.fleet_mgmt_sensors;
import io.confluent.base.BaseInfrastructureTest;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VehicleStatsComponentTest extends BaseInfrastructureTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(VehicleStatsComponentTest.class);

  @Test
  public void testProduceConsume() throws Exception {

    final var topicName = "test";
    super.createKafkaTopic(topicName);

    super.produceRecords(topicName,
        Collections.singletonList(new fleet_mgmt_sensors(1, 300, 4000)));

    final List<fleet_mgmt_sensors> result = super.consumeRecords(topicName);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1, result.getFirst().getVehicleId());
    Assertions.assertEquals(300, result.getFirst().getEngineTemperature());
    Assertions.assertEquals(4000, result.getFirst().getAverageRpm());

    LOGGER.info("Successfully produced/consumed {} record to Kafka topic '{}'", result.size(), topicName);
  }

  @Test
  public void testFlinkProcessing() throws Exception {
    super.createKafkaTopic(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS);
    super.createKafkaTopic(VehicleStatsComponent.DEMO_FLEET_VEHICLE_STATS);

    produceRecords();

    final var settings = EnvironmentSettings.inStreamingMode();
    final var environment = TableEnvironment.create(settings);

    final var component = new VehicleStatsComponent();
    component.tableEnvironment = environment;

    component.run(null);

    Thread.sleep(1000);

    final List<fleet_mgmt_sensors> result = super.consumeRecords(VehicleStatsComponent.DEMO_FLEET_VEHICLE_STATS);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1, result.getFirst().getVehicleId());
    Assertions.assertEquals(300, result.getFirst().getEngineTemperature());
    Assertions.assertEquals(4000, result.getFirst().getAverageRpm());

    LOGGER.info("Successfully produced/consumed {} record to Kafka topic '{}'", result.size(), VehicleStatsComponent.DEMO_FLEET_VEHICLE_STATS);
  }

  private void produceRecords() throws InterruptedException, ExecutionException {
    super.produceRecords(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS,
        Collections.singletonList(new fleet_mgmt_sensors(1, 300, 5000)));

    super.produceRecords(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS,
        Collections.singletonList(new fleet_mgmt_sensors(2, 150, 3500)));

    super.produceRecords(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS,
        Collections.singletonList(new fleet_mgmt_sensors(1, 310, 5010)));

    super.produceRecords(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS,
        Collections.singletonList(new fleet_mgmt_sensors(2, 155, 3505)));

    super.produceRecords(VehicleStatsComponent.DEMO_FLEET_MGMT_SENSORS,
        Collections.singletonList(new fleet_mgmt_sensors(2, 320, 5020)));
  }
}