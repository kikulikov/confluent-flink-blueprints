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
resource "confluent_environment" "demo" {
  display_name = "${var.cc_env_name}-${random_id.id.hex}"

  stream_governance {
    package = "ESSENTIALS"
  }

  lifecycle {
    prevent_destroy = false
  }
}

# --------------------------------------------------------
# Confluent Cloud - Kafka Cluster
# --------------------------------------------------------
resource "confluent_kafka_cluster" "demo" {
  display_name = var.cc_cluster_name
  availability = var.cc_availability
  cloud        = var.cc_cloud_provider
  region       = var.cc_cloud_region
  basic {}
  environment {
    id = confluent_environment.demo.id
  }
  lifecycle {
    prevent_destroy = false
  }
}

# --------------------------------------------------------
# Confluent Cloud - Schema Registry
# --------------------------------------------------------
data "confluent_schema_registry_cluster" "demo" {
  environment {
    id = confluent_environment.demo.id
  }
  depends_on = [
    confluent_kafka_cluster.demo
  ]
}


