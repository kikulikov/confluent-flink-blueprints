terraform {
  required_providers {
    confluent = {
      source  = "confluentinc/confluent"
      version = "2.17.0"
    }
  }
}

provider "confluent" {
  cloud_api_key    = var.confluent_cloud_api_key
  cloud_api_secret = var.confluent_cloud_api_secret
}

# -------------------------------------------------------
# Confluent Cloud - Organization
# -------------------------------------------------------
data "confluent_organization" "main" {
}

# -------------------------------------------------------
# Confluent Cloud - Environment
# -------------------------------------------------------
data "confluent_environment" "demo" {
  id = var.confluent_cloud_environment_id
}

# --------------------------------------------------------
# Confluent Cloud - Kafka Cluster
# --------------------------------------------------------
data "confluent_kafka_cluster" "demo" {
  id = var.confluent_cloud_kafka_cluster_id
  environment {
    id = var.confluent_cloud_environment_id
  }
}
