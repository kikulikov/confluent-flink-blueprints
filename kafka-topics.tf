# --------------------------------------------
# Confluent Cloud - Infra Manager
# --------------------------------------------
resource "confluent_service_account" "infrastructure-manager" {
  display_name = "infrastructure-manager-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Service Account for setting up Schemas and Topics"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_role_binding" "infrastructure-manager-environment-admin" {
  principal   = "User:${confluent_service_account.infrastructure-manager.id}"
  crn_pattern = confluent_environment.demo.resource_name
  role_name   = "EnvironmentAdmin"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_api_key" "infrastructure-manager-kafka-api-key" {
  display_name = "infrastructure-manager-kafka-api-key"
  description  = "Kafka API Key that is owned by 'infrastructure-manager' service account"
  owner {
    id          = confluent_service_account.infrastructure-manager.id
    api_version = confluent_service_account.infrastructure-manager.api_version
    kind        = confluent_service_account.infrastructure-manager.kind
  }
  managed_resource {
    id          = confluent_kafka_cluster.demo.id
    api_version = confluent_kafka_cluster.demo.api_version
    kind        = confluent_kafka_cluster.demo.kind

    environment {
      id = confluent_environment.demo.id
    }
  }
  depends_on = [
    confluent_role_binding.infrastructure-manager-environment-admin
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_api_key" "infrastructure-manager-schema-registry-api-key" {
  display_name = "infrastructure-manager-schema-registry-api-key"
  description  = "Schema Registry API Key that is owned by 'infrastructure-manager' service account"
  owner {
    id          = confluent_service_account.infrastructure-manager.id
    api_version = confluent_service_account.infrastructure-manager.api_version
    kind        = confluent_service_account.infrastructure-manager.kind
  }
  managed_resource {
    id          = data.confluent_schema_registry_cluster.demo.id
    api_version = data.confluent_schema_registry_cluster.demo.api_version
    kind        = data.confluent_schema_registry_cluster.demo.kind

    environment {
      id = confluent_environment.demo.id
    }
  }
  depends_on = [
    confluent_role_binding.infrastructure-manager-environment-admin
  ]
  lifecycle {
    prevent_destroy = false
  }
}

# --------------------------------------------
# Confluent Cloud - Kafka Topics
# --------------------------------------------
resource "confluent_kafka_topic" "fleet_mgmt_description" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  topic_name    = "${var.cc_prefix}_${var.cc_topic.fleet_mgmt_description}"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.infrastructure-manager-kafka-api-key.id
    secret = confluent_api_key.infrastructure-manager-kafka-api-key.secret
  }
  partitions_count = 1
  config = {
    "cleanup.policy"  = "delete"
    "retention.bytes" = "-1"
    "retention.ms"    = "259200000"
  }
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_kafka_topic" "fleet_mgmt_location" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  topic_name    = "${var.cc_prefix}_${var.cc_topic.fleet_mgmt_location}"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.infrastructure-manager-kafka-api-key.id
    secret = confluent_api_key.infrastructure-manager-kafka-api-key.secret
  }
  partitions_count = 1
  config = {
    "cleanup.policy"  = "delete"
    "retention.bytes" = "-1"
    "retention.ms"    = "259200000"
  }
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_kafka_topic" "fleet_mgmt_sensors" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  topic_name    = "${var.cc_prefix}_${var.cc_topic.fleet_mgmt_sensors}"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.infrastructure-manager-kafka-api-key.id
    secret = confluent_api_key.infrastructure-manager-kafka-api-key.secret
  }
  partitions_count = 1
  config = {
    "cleanup.policy"  = "delete"
    "retention.bytes" = "-1"
    "retention.ms"    = "259200000"
  }
  lifecycle {
    prevent_destroy = false
  }
}
