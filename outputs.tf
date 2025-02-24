output "resource_ids" {
  value = {
    organization_id    = data.confluent_organization.main.id
    environment_id     = confluent_environment.demo.id
    kafka_cluster_id   = confluent_kafka_cluster.demo.id
    bootstrap_endpoint = confluent_kafka_cluster.demo.bootstrap_endpoint

    app-manager = {
      display_name = confluent_service_account.app-manager.display_name
      id           = confluent_service_account.app-manager.id
      api_key      = confluent_api_key.app-manager-kafka-api-key.id
      api_secret   = confluent_api_key.app-manager-kafka-api-key.secret
    }

    app-consumer = {
      display_name = confluent_service_account.app-consumer.display_name
      id           = confluent_service_account.app-consumer.id
      api_key      = confluent_api_key.app-consumer-kafka-api-key.id
      api_secret   = confluent_api_key.app-consumer-kafka-api-key.secret
    }

    schema_registry = {
      rest_endpoint = data.confluent_schema_registry_cluster.demo.rest_endpoint
      api_key       = confluent_api_key.infrastructure-manager-schema-registry-api-key.id
      api_secret    = confluent_api_key.infrastructure-manager-schema-registry-api-key.secret
    }
  }
  sensitive = true
}
