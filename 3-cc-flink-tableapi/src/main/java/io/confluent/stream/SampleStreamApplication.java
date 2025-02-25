package io.confluent.stream;

import io.confluent.flink.plugin.ConfluentSettings;
import java.util.UUID;
import org.apache.flink.table.api.TableEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleStreamApplication {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${confluent.flink.catalog.name}")
    private String catalogName;

    @Value("${confluent.flink.database.name}")
    private String databaseName;

    public static void main(String[] args) {
        SpringApplication.run(SampleStreamApplication.class, args);
    }

    @Bean
    public TableEnvironment tableEnvironment() {
        final var settings = ConfluentSettings.fromGlobalVariables();
        final var environment = TableEnvironment.create(settings);

        environment.useCatalog(catalogName); // Use the injected catalog name
        environment.useDatabase(databaseName); // Use the injected database name

        // Assign a custom name to the Flink job for easier identification
        final var statementName = applicationName + "-" + UUID.randomUUID();
        environment.getConfig().set("client.statement-name", statementName);

        return environment;
    }
}
