# Table API

## Prerequisites

```shell
confluent login

confluent environment list
confluent environment use env-05rzn6

confluent kafka cluster list
confluent kafka cluster use lkc-77owpp

confluent kafka topic create demo_fleet_mgmt_vehicle_stats_datastream --partitions 1 --if-not-exists
```

## Gradle

```bash
gradle wrapper

./gradlew clean
./gradlew spotlessApply
./gradlew build
```

https://github.com/confluentinc/cp-flink-e2e

### Install minio CLI
https://min.io/docs/minio/linux/reference/minio-mc.html#quickstart
brew install minio/stable/mc


### Add the MinIO Operator Repo to Helm
https://min.io/docs/minio/kubernetes/upstream/operations/install-deploy-manage/deploy-operator-helm.html#install-the-minio-operator-using-helm-charts

helm repo add minio-operator https://operator.min.io

helm install --namespace minio-operator --create-namespace \
operator minio-operator/operator

mc cp build/libs/cp-flink-datastream-0.0.2-SNAPSHOT-all.jar dev-minio/flink/cp-flink-datastream-0.0.2-SNAPSHOT-all.jar

```sh
# Open port forwarding to CMF.
kubectl port-forward -n flink-manager svc/cmf-service 8080:80

# Create Environment
confluent flink environment create development --kubernetes-namespace flink --url http://localhost:8080

# Deploy example Flink jobs
confluent flink application create --environment development --url http://localhost:8080 deploy-sample-select.json

# Access Web UI
confluent flink application web-ui-forward --environment development kafka-reader-writer-example --url http://localhost:8080

# Delete the application
confluent flink application delete --environment development basic-example --url http://localhost:8080
```

## TODO

- Unit Tests
- IT Tests
- Run scripts / profiles

```shell
java -jar avro-tools-1.12.0.jar compile schema src/main/avro/schema-demo_fleet_mgmt_sensors-value-v1.avsc src/main/java/
java -jar avro-tools-1.12.0.jar compile schema src/main/avro/schema-demo_fleet_mgmt_location-value-v1.avsc src/main/java/
java -jar avro-tools-1.12.0.jar compile schema src/main/avro/schema-vehicle-stats-value.avsc src/main/java/

confluent flink application delete --environment development --url http://localhost:8080 kafka-reader-writer-example --force
./gradlew clean spotlessApply build
mc cp build/libs/cp-flink-datastream-0.0.2-SNAPSHOT-all.jar dev-minio/flink/cp-flink-datastream-0.0.2-SNAPSHOT-all.jar
confluent flink application create --environment development --url http://localhost:8080 deploy-sample-select.json
```

## Resources

https://docs.confluent.io/cloud/current/flink/reference/table-api.html
https://docs.confluent.io/cloud/current/flink/reference/functions/table-api-functions.html#
https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html
https://developer.confluent.io/courses/flink-table-api-java/overview/
https://github.com/confluentinc/flink-table-api-java-examples
