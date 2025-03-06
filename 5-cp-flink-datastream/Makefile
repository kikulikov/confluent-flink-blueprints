# Makefile for Flink application deployment
# Variables
ENVIRONMENT = development
FLINK_URL = http://localhost:8080
APP_NAME = kafka-reader-writer-example
JAR_NAME = demo-0.0.1-SNAPSHOT-all.jar
MINIO_BUCKET = dev-minio/flink
DEPLOYMENT_FILE = kafka-deployment.json

# Default target
.PHONY: all
all: build upload create

# Delete existing application
.PHONY: delete
delete:
	confluent flink application delete --force \
	--environment $(ENVIRONMENT) --url $(FLINK_URL) $(APP_NAME) && echo

# Build the JAR (clean, format, and package)
.PHONY: build
build:
	java -jar avro-tools-1.12.0.jar compile schema \
    src/main/avro/schema-demo_fleet_mgmt_sensors-value-v1.avsc src/main/java/

	java -jar avro-tools-1.12.0.jar compile schema \
    src/main/avro/schema-demo_fleet_mgmt_location-value-v1.avsc src/main/java/

	./gradlew clean spotlessApply shadowJar && echo

# Upload JAR to MinIO
.PHONY: upload
upload: build
	mc cp build/libs/$(JAR_NAME) $(MINIO_BUCKET)/$(JAR_NAME) && echo

# Create the application
.PHONY: create
create: upload
	confluent flink application create -v \
	--environment $(ENVIRONMENT) --url $(FLINK_URL) $(DEPLOYMENT_FILE) && echo

# Full redeployment (delete -> build -> upload -> create)
.PHONY: redeploy
redeploy: delete build upload create
