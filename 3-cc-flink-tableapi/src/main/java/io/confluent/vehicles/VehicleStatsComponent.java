package io.confluent.vehicles;

import static org.apache.flink.table.api.Expressions.*;

import io.confluent.flink.plugin.ConfluentTableDescriptor;
import io.confluent.flink.plugin.ConfluentTools;
import java.time.Duration;
import java.util.Arrays;
import org.apache.flink.table.api.*;
import org.apache.flink.table.expressions.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Write a solution to calculate the number of vehicles for each engine usage category: HIGH, NORMAL, LOW
 * https://leetcode.com/problems/count-salary-categories/description/
 */
@Component
public class VehicleStatsComponent implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleStatsComponent.class);
    protected static final String DEMO_FLEET_MGMT_SENSORS = "demo_fleet_mgmt_sensors";
    protected static final String DEMO_FLEET_VEHICLE_STATS = "demo_fleet_vehicle_stats_tableapi";

    private static final ApiExpression TUMBLE_SIZE = lit(10).minutes();
    private static final String TS_FORMAT = "yyyy_MM_dd_hh_mm_ss";
    private static final int NUMBER_OF_BUCKETS = 1;

    static final Schema STATS_SCHEMA = Schema.newBuilder()
            .column("usage_category", DataTypes.STRING().notNull())
            .column("vehicle_count", DataTypes.BIGINT())
            .column("window_start", DataTypes.STRING())
            .column("window_end", DataTypes.STRING())
            .primaryKey("usage_category")
            .build();

    private static final ConfluentTableDescriptor STATS_DESCRIPTOR = ConfluentTableDescriptor.forManaged()
            .distributedInto(NUMBER_OF_BUCKETS)
            .option("kafka.retention.time", "2 d")
            .option("value.fields-include", "ALL")
            .schema(STATS_SCHEMA)
            .build();

    @Autowired
    protected TableEnvironment tableEnvironment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Running...");

        ensureTableExists();

        final Table processHigh = tableEnvironment
                .from(DEMO_FLEET_MGMT_SENSORS)
                .filter($("average_rpm")
                        .isGreaterOrEqual(lit(4750))
                        .and($("engine_temperature").isGreaterOrEqual(lit(240))))
                .window(Tumble.over(TUMBLE_SIZE).on($("$rowtime")).as("w"))
                .groupBy($("w"))
                .select(getOutputFields("HIGH"));

        final Table processNormal = tableEnvironment
                .from(DEMO_FLEET_MGMT_SENSORS)
                .filter(($("average_rpm")
                                .isGreater(lit(4000))
                                .and($("average_rpm").isLess(lit(4750))))
                        .or($("engine_temperature")
                                .isGreater(lit(200))
                                .and($("engine_temperature").isLess(lit(240)))))
                .window(Tumble.over(TUMBLE_SIZE).on($("$rowtime")).as("w"))
                .groupBy($("w"))
                .select(getOutputFields("NORMAL"));

        final Table processLow = tableEnvironment
                .from(DEMO_FLEET_MGMT_SENSORS)
                .filter($("average_rpm")
                        .isLessOrEqual(lit(4000))
                        .and($("engine_temperature").isLessOrEqual(lit(200))))
                .window(Tumble.over(TUMBLE_SIZE).on($("$rowtime")).as("w"))
                .groupBy($("w"))
                .select(getOutputFields("LOW"));

        final var tableResult = processHigh
                .unionAll(processNormal)
                .unionAll(processLow)
                .insertInto(DEMO_FLEET_VEHICLE_STATS)
                .execute();

        LOGGER.info("Statement ID: {}", ConfluentTools.getStatementName(tableResult));
    }

    private static Expression[] getOutputFields(String category) {
        return new Expression[] {
            lit(category).as("usage_category"),
            $("vehicle_id").count().as("vehicle_count"),
            dateFormat($("w").start(), TS_FORMAT).as("window_start"),
            dateFormat($("w").end(), TS_FORMAT).as("window_end")
        };
    }

    public void ensureTableExists() {
        if (Arrays.stream(tableEnvironment.listTables())
                .noneMatch(name -> name.equals(DEMO_FLEET_VEHICLE_STATS))) {

            tableEnvironment.createTable(DEMO_FLEET_VEHICLE_STATS, STATS_DESCRIPTOR);

            try {
                Thread.sleep(Duration.ofSeconds(30).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
