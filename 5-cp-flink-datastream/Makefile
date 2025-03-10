# Makefile for Flink application deployment

# Variables
ENVIRONMENT = development
FLINK_URL = http://localhost:8080
JAR_NAME = cp-flink-datastream-0.0.1-SNAPSHOT-all.jar
MINIO_BUCKET = dev-minio/flink
DEPLOYMENT_FILE ?= deploy-sample-select.json
APP_NAME = kafka-example

# Default target
.PHONY: all
all: build

# Delete existing application
.PHONY: delete
delete:
	confluent flink application delete --force \
	--environment $(ENVIRONMENT) --url $(FLINK_URL) $(APP_NAME) && echo

# Build the JAR (clean, format, and package)
.PHONY: build
build:
	java -jar avro-tools-1.12.0.jar compile schema \
    src/main/avro/schema-demo_fleet_mgmt_sensors-value-v1.avsc src/main/java/ && echo

	java -jar avro-tools-1.12.0.jar compile schema \
    src/main/avro/schema-demo_fleet_mgmt_location-value-v1.avsc src/main/java/ && echo

	./gradlew clean spotlessApply build

# Upload JAR to MinIO
.PHONY: upload
upload:
	mc cp build/libs/$(JAR_NAME) $(MINIO_BUCKET)/$(JAR_NAME) && echo

# Create the application
.PHONY: submit
submit:
	confluent flink application create -v \
	--environment $(ENVIRONMENT) --url $(FLINK_URL) $(DEPLOYMENT_FILE) && echo

# Full deployment (build -> upload -> submit)
.PHONY: create
create: build upload submit

# Full redeployment (delete -> build -> upload -> submit)
.PHONY: update
update: delete build upload submit
