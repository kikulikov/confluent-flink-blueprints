output "resource_ids" {
  value = {
    organization_id    = data.confluent_organization.main.id
    environment_id     = data.confluent_environment.demo.id
    kafka_cluster_id   = data.confluent_kafka_cluster.demo.id
    bootstrap_endpoint = data.confluent_kafka_cluster.demo.bootstrap_endpoint

    confluent_flink = {
      cloud  = data.confluent_flink_region.demo.cloud
      region = data.confluent_flink_region.demo.region
      id     = confluent_flink_compute_pool.demo.id
      key    = confluent_api_key.statements-runner-flink-api-key.id
      secret = confluent_api_key.statements-runner-flink-api-key.secret
    }
  }
  sensitive = true
}
