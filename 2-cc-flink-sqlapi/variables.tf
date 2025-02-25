variable "confluent_cloud_api_key" {
  description = "Confluent Cloud - API Key"
  type        = string
}

variable "confluent_cloud_api_secret" {
  description = "Confluent Cloud - API Secret"
  type        = string
  sensitive   = true
}

variable "confluent_cloud_environment_id" {
  description = "Confluent Cloud - Environment ID"
  type        = string
}

variable "confluent_cloud_kafka_cluster_id" {
  description = "Confluent Cloud - Kafka Cluster ID"
  type        = string
}

resource "random_id" "id" {
  byte_length = 4
}

variable "cc_prefix" {
  type        = string
  default     = "demo"
}

# # ----------------------------------------
# # Confluent Cloud - Kafka Specific
# # ----------------------------------------
variable "cc_cloud_provider" {
  type    = string
  default = "AWS"
}

variable "cc_cloud_region" {
  type    = string
  default = "eu-central-1"
}

variable "cc_cluster_name" {
  type    = string
  default = "demo-cluster"
}

# # --------------------------------------------
# # Confluent Cloud - Topics
# # --------------------------------------------
# variable "cc_topic" {
#   type = map(string)
#   default = {
#     fleet_mgmt_description = "fleet_mgmt_description"
#     fleet_mgmt_location = "fleet_mgmt_location"
#     fleet_mgmt_sensors = "fleet_mgmt_sensors"
#   }
# }

# # --------------------------------------------
# # Confluent Cloud - Connectors
# # --------------------------------------------
# variable "cc_connector" {
#   type = map(string)
#   default = {
#     fleet_mgmt_description = "fleet_mgmt_description"
#     fleet_mgmt_location = "fleet_mgmt_location"
#     fleet_mgmt_sensors = "fleet_mgmt_sensors"
#   }
# }
