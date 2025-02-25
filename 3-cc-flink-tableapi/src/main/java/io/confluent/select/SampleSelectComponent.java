package io.confluent.select;

import static org.apache.flink.table.api.Expressions.$;

import io.confluent.flink.plugin.ConfluentTools;
import org.apache.flink.table.api.TableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleSelectComponent implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSelectComponent.class);

    @Autowired private TableEnvironment tableEnvironment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Running...");

        final var tableResult =
                tableEnvironment
                        .from("demo_fleet_mgmt_sensors")
                        .select($("*"), $("$rowtime"))
                        .execute();

        ConfluentTools.printMaterialized(tableResult, 100);
    }
}
