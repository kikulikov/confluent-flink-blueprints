# Flink Table API Showcase

## Gradle

```bash
# Generate or update the Gradle Wrapper scripts  
gradle wrapper  

# Clean previous build outputs
./gradlew clean

# Run Spotless to automatically format and fix code style issues  
./gradlew spotlessApply

# Build the project, compiling and packaging the code  
./gradlew build

# Clean previous builds, format code, and create a fat JAR
./gradlew clean spotlessApply build
```

## Command Line

```shell
# Submit the application to Confluent Cloud
java -cp build/libs/cc-flink-tableapi-0.0.2-SNAPSHOT-all.jar io.confluent.vehicles.VehicleStatsApplication
```

## Flink Shell

```shell
# Start a Confluent Flink shell session  
confluent flink shell --compute-pool lfcp-jg7jom --environment env-05rzn6  

# Then query all records from the table  
> SELECT * FROM `demo_fleet_mgmt_vehicle_stats_table_api`;

```

## IntelliJ IDEA Environment

```properties
ORG_ID=4c60b3e5-72c0-4c78-9677-0cc97ff37d11
ENV_ID=env-05rzn6
CLOUD_PROVIDER=AWS
CLOUD_REGION=eu-central-1
COMPUTE_POOL_ID=lfcp-jg7jom
FLINK_API_KEY=XXX
FLINK_API_SECRET=YYY
```

## TODO

- Unit Tests
- IT Tests
- Run scripts
- Spring profiles

## Resources

https://docs.confluent.io/cloud/current/flink/reference/table-api.html
https://docs.confluent.io/cloud/current/flink/reference/functions/table-api-functions.html#
https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html
https://developer.confluent.io/courses/flink-table-api-java/overview/
https://github.com/confluentinc/flink-table-api-java-examples
