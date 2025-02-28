# -------------------------------------------------------
# Confluent Cloud - App Manager
# -------------------------------------------------------
resource "confluent_service_account" "app-manager" {
  display_name = "app-manager-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Service account to manage 'inventory' Kafka cluster"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_role_binding" "app-manager-kafka-cluster-admin" {
  principal   = "User:${confluent_service_account.app-manager.id}"
  crn_pattern = confluent_kafka_cluster.demo.rbac_crn
  role_name   = "CloudClusterAdmin"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_api_key" "app-manager-kafka-api-key" {
  display_name = "app-manager-key-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Kafka API Key that is owned by 'app-manager' service account"
  owner {
    id          = confluent_service_account.app-manager.id
    api_version = confluent_service_account.app-manager.api_version
    kind        = confluent_service_account.app-manager.kind
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
    confluent_role_binding.app-manager-kafka-cluster-admin
  ]
  lifecycle {
    prevent_destroy = false
  }
}

# -------------------------------------------------------
# Confluent Cloud - App Producer
# -------------------------------------------------------
resource "confluent_service_account" "app-producer" {
  display_name = "app-producer-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Service account to produce to the topics of 'inventory' Kafka cluster"
}

resource "confluent_api_key" "app-producer-kafka-api-key" {
  display_name = "app-producer-key-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Kafka API Key that is owned by 'app-producer' service account"
  owner {
    id          = confluent_service_account.app-producer.id
    api_version = confluent_service_account.app-producer.api_version
    kind        = confluent_service_account.app-producer.kind
  }
  managed_resource {
    id          = confluent_kafka_cluster.demo.id
    api_version = confluent_kafka_cluster.demo.api_version
    kind        = confluent_kafka_cluster.demo.kind

    environment {
      id = confluent_environment.demo.id
    }
  }
  lifecycle {
    prevent_destroy = false
  }
}

# resource "confluent_role_binding" "app-producer-developer-write" {
#   principal   = "User:${confluent_service_account.app-producer.id}"
#   role_name   = "DeveloperWrite"
#   crn_pattern = "${confluent_kafka_cluster.demo.rbac_crn}/kafka=${confluent_kafka_cluster.demo.id}/topic=demo_*"
# }

resource "confluent_kafka_acl" "app-producer-write-on-topic" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  resource_type = "TOPIC"
  resource_name = "demo_"
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.app-producer.id}"
  host          = "*"
  operation     = "WRITE"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.app-manager-kafka-api-key.id
    secret = confluent_api_key.app-manager-kafka-api-key.secret
  }
  lifecycle {
    prevent_destroy = false
  }
}

# -------------------------------------------------------
# Confluent Cloud - App Consumer
# -------------------------------------------------------
resource "confluent_service_account" "app-consumer" {
  display_name = "app-consumer-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Service account to consume from the topics of 'inventory' Kafka cluster"
}

resource "confluent_api_key" "app-consumer-kafka-api-key" {
  display_name = "app-consumer-key-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Kafka API Key that is owned by 'app-consumer' service account"
  owner {
    id          = confluent_service_account.app-consumer.id
    api_version = confluent_service_account.app-consumer.api_version
    kind        = confluent_service_account.app-consumer.kind
  }
  managed_resource {
    id          = confluent_kafka_cluster.demo.id
    api_version = confluent_kafka_cluster.demo.api_version
    kind        = confluent_kafka_cluster.demo.kind

    environment {
      id = confluent_environment.demo.id
    }
  }
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_kafka_acl" "app-consumer-read-on-topic" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  resource_type = "TOPIC"
  resource_name = "demo_"
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.app-consumer.id}"
  host          = "*"
  operation     = "READ"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.app-manager-kafka-api-key.id
    secret = confluent_api_key.app-manager-kafka-api-key.secret
  }
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_kafka_acl" "app-consumer-read-on-group" {
  kafka_cluster {
    id = confluent_kafka_cluster.demo.id
  }
  resource_type = "GROUP"
  resource_name = "demo_"
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.app-consumer.id}"
  host          = "*"
  operation     = "READ"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.app-manager-kafka-api-key.id
    secret = confluent_api_key.app-manager-kafka-api-key.secret
  }
  lifecycle {
    prevent_destroy = false
  }
}
