package io.confluent.stream;

import static org.apache.flink.table.api.Expressions.*;

import io.confluent.flink.plugin.ConfluentTableDescriptor;
import io.confluent.flink.plugin.ConfluentTools;
import java.time.Duration;
import java.util.Arrays;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.Tumble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleStreamComponent implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleStreamComponent.class);
    private static final String DEMO_FLEET_MGMT_SENSORS = "demo_fleet_mgmt_sensors";
    private static final String DEMO_FLEET_MGMT_SENSORS_ALERTS = "demo_fleet_mgmt_sensors_alerts";
    private static final String TS_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private static final Schema TABLE_SCHEMA =
            Schema.newBuilder()
                    .column("vehicle_id", DataTypes.INT().notNull())
                    .column("engine_temperature", DataTypes.INT())
                    .column("average_rpm", DataTypes.INT())
                    .column("window_start", DataTypes.STRING())
                    .column("window_end", DataTypes.STRING())
                    .build();

    private static final ConfluentTableDescriptor TABLE_DESCRIPTOR =
            ConfluentTableDescriptor.forManaged()
                    .schema(TABLE_SCHEMA)
                    .distributedBy(4, "vehicle_id")
                    .option("kafka.retention.time", "2 d")
                    .build();

    @Autowired private TableEnvironment tableEnvironment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Running...");

        ensureTableExists();

        final var tableResult =
                tableEnvironment
                        .from(DEMO_FLEET_MGMT_SENSORS)
                        .window(Tumble.over(lit(10).minutes()).on($("$rowtime")).as("w"))
                        .groupBy($("w"), $("vehicle_id"))
                        .select(
                                $("vehicle_id"),
                                $("engine_temperature").avg().as("engine_temperature"),
                                $("average_rpm").avg().as("average_rpm"),
                                dateFormat($("w").start(), TS_FORMAT).as("window_start"),
                                dateFormat($("w").end(), TS_FORMAT).as("window_end"))
                        .where(
                                $("engine_temperature")
                                        .isGreater(lit(240))
                                        .and($("average_rpm").isGreater(lit(4900))))
                        .insertInto(DEMO_FLEET_MGMT_SENSORS_ALERTS)
                        .execute();

        LOGGER.info("Statement: {}", ConfluentTools.getStatementName(tableResult));
    }

    public void ensureTableExists() {
        if (Arrays.stream(tableEnvironment.listTables())
                .noneMatch(name -> name.equals(DEMO_FLEET_MGMT_SENSORS_ALERTS))) {

            tableEnvironment.createTable(DEMO_FLEET_MGMT_SENSORS_ALERTS, TABLE_DESCRIPTOR);

            try {
                Thread.sleep(Duration.ofSeconds(30).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
