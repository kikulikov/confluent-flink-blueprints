# -------------------------------------------------------
# Confluent Cloud - Connectors
# -------------------------------------------------------
resource "confluent_connector" "fleet_mgmt_description" {
  environment {
    id = confluent_environment.demo.id
  }
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  config_sensitive = {}
  config_nonsensitive = {
    "connector.class"          = "DatagenSource"
    "name"                     = "${var.cc_prefix}_${var.cc_connector.fleet_mgmt_description}"
    "kafka.auth.mode"          = "SERVICE_ACCOUNT"
    "kafka.service.account.id" = confluent_service_account.infrastructure-manager.id
    "kafka.topic"              = confluent_kafka_topic.fleet_mgmt_description.topic_name
    "output.data.format"       = "AVRO"
    "quickstart"               = "FLEET_MGMT_DESCRIPTION"
    "tasks.max"                = "1"
    "max.interval"             = "5000"
  }
  depends_on = [
    confluent_service_account.infrastructure-manager,
    confluent_kafka_topic.fleet_mgmt_description
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_connector" "fleet_mgmt_location" {
  environment {
    id = confluent_environment.demo.id
  }
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  config_sensitive = {}
  config_nonsensitive = {
    "connector.class"          = "DatagenSource"
    "name"                     = "${var.cc_prefix}_${var.cc_connector.fleet_mgmt_location}"
    "kafka.auth.mode"          = "SERVICE_ACCOUNT"
    "kafka.service.account.id" = confluent_service_account.infrastructure-manager.id
    "kafka.topic"              = confluent_kafka_topic.fleet_mgmt_location.topic_name
    "output.data.format"       = "AVRO"
    "quickstart"               = "FLEET_MGMT_LOCATION"
    "tasks.max"                = "1"
    "max.interval"             = "2000"
  }
  depends_on = [
    confluent_service_account.infrastructure-manager,
    confluent_kafka_topic.fleet_mgmt_location
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_connector" "fleet_mgmt_sensors" {
  environment {
    id = confluent_environment.demo.id
  }
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  config_sensitive = {}
  config_nonsensitive = {
    "connector.class"          = "DatagenSource"
    "name"                     = "${var.cc_prefix}_${var.cc_connector.fleet_mgmt_sensors}"
    "kafka.auth.mode"          = "SERVICE_ACCOUNT"
    "kafka.service.account.id" = confluent_service_account.infrastructure-manager.id
    "kafka.topic"              = confluent_kafka_topic.fleet_mgmt_sensors.topic_name
    "output.data.format"       = "AVRO"
    "quickstart"               = "FLEET_MGMT_SENSORS"
    "tasks.max"                = "1"
    "max.interval"             = "1000"
  }
  depends_on = [
    confluent_service_account.infrastructure-manager,
    confluent_kafka_topic.fleet_mgmt_sensors
  ]
  lifecycle {
    prevent_destroy = false
  }
}
