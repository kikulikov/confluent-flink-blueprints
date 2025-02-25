# -------------------------------------------------------
# Confluent Flink - Compute Pool
# -------------------------------------------------------
data "confluent_flink_region" "demo" {
  cloud  = var.cc_cloud_provider
  region = var.cc_cloud_region
}

resource "confluent_flink_compute_pool" "demo" {
  display_name = "${var.cc_prefix}-compute-pool-${random_id.id.hex}"
  cloud        = data.confluent_flink_region.demo.cloud
  region       = data.confluent_flink_region.demo.region
  max_cfu      = 10
  environment {
    id = data.confluent_environment.demo.id
  }
}

# -------------------------------------------------------
# Confluent Flink - Statements Runner
# -------------------------------------------------------
resource "confluent_service_account" "statements-runner" {
  display_name = "statements-runner-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Service account for running Flink Statements in 'inventory' Kafka cluster"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_role_binding" "statements-runner-environment-admin" {
  principal   = "User:${confluent_service_account.statements-runner.id}"
  crn_pattern = data.confluent_environment.demo.resource_name
  role_name   = "EnvironmentAdmin"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_api_key" "statements-runner-flink-api-key" {
  display_name = "statements-runner-key-${var.cc_cluster_name}-${random_id.id.hex}"
  description  = "Flink API Key that is owned by 'statements-runner' service account"
  owner {
    id          = confluent_service_account.statements-runner.id
    api_version = confluent_service_account.statements-runner.api_version
    kind        = confluent_service_account.statements-runner.kind
  }
  managed_resource {
    id          = data.confluent_flink_region.demo.id
    api_version = data.confluent_flink_region.demo.api_version
    kind        = data.confluent_flink_region.demo.kind
    environment {
      id = data.confluent_environment.demo.id
    }
  }
  lifecycle {
    prevent_destroy = false
  }
}

# -------------------------------------------------------
# Confluent Flink - Statements Declarations
# -------------------------------------------------------
resource "confluent_flink_statement" "create-table-location-detailed" {
  organization {
    id = data.confluent_organization.main.id
  }
  environment {
    id = data.confluent_environment.demo.id
  }
  compute_pool {
    id = confluent_flink_compute_pool.demo.id
  }
  principal {
    id = confluent_service_account.statements-runner.id
  }
  statement = file("flink-statements/create-table-location-detailed.sql")
  properties = {
    "sql.current-catalog"  = data.confluent_environment.demo.display_name
    "sql.current-database" = data.confluent_kafka_cluster.demo.display_name
  }
  rest_endpoint = data.confluent_flink_region.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.statements-runner-flink-api-key.id
    secret = confluent_api_key.statements-runner-flink-api-key.secret
  }
  depends_on = [
    confluent_flink_compute_pool.demo
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_flink_statement" "create-table-location-latest" {
  organization {
    id = data.confluent_organization.main.id
  }
  environment {
    id = data.confluent_environment.demo.id
  }
  compute_pool {
    id = confluent_flink_compute_pool.demo.id
  }
  principal {
    id = confluent_service_account.statements-runner.id
  }
  statement = file("flink-statements/create-table-location-latest.sql")
  properties = {
    "sql.current-catalog"  = data.confluent_environment.demo.display_name
    "sql.current-database" = data.confluent_kafka_cluster.demo.display_name
  }
  rest_endpoint = data.confluent_flink_region.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.statements-runner-flink-api-key.id
    secret = confluent_api_key.statements-runner-flink-api-key.secret
  }
  depends_on = [
    confluent_flink_compute_pool.demo
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_flink_statement" "insert-into-location-detailed" {
  organization {
    id = data.confluent_organization.main.id
  }
  environment {
    id = data.confluent_environment.demo.id
  }
  compute_pool {
    id = confluent_flink_compute_pool.demo.id
  }
  principal {
    id = confluent_service_account.statements-runner.id
  }
  statement = file("flink-statements/insert-into-location-detailed.sql")
  properties = {
    "sql.current-catalog"  = data.confluent_environment.demo.display_name
    "sql.current-database" = data.confluent_kafka_cluster.demo.display_name
  }
  rest_endpoint = data.confluent_flink_region.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.statements-runner-flink-api-key.id
    secret = confluent_api_key.statements-runner-flink-api-key.secret
  }
  depends_on = [
    confluent_flink_compute_pool.demo
  ]
  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_flink_statement" "insert-into-location-latest" {
  organization {
    id = data.confluent_organization.main.id
  }
  environment {
    id = data.confluent_environment.demo.id
  }
  compute_pool {
    id = confluent_flink_compute_pool.demo.id
  }
  principal {
    id = confluent_service_account.statements-runner.id
  }
  statement = file("flink-statements/insert-into-location-latest.sql")
  properties = {
    "sql.current-catalog"  = data.confluent_environment.demo.display_name
    "sql.current-database" = data.confluent_kafka_cluster.demo.display_name
  }
  rest_endpoint = data.confluent_flink_region.demo.rest_endpoint
  credentials {
    key    = confluent_api_key.statements-runner-flink-api-key.id
    secret = confluent_api_key.statements-runner-flink-api-key.secret
  }
  depends_on = [
    confluent_flink_compute_pool.demo
  ]
  lifecycle {
    prevent_destroy = false
  }
}

# -------------------------------------------------------
# Confluent Flink - Artifacts Declarations
# -------------------------------------------------------

resource "confluent_flink_artifact" "main" {
  cloud            = var.cc_cloud_provider
  region           = var.cc_cloud_region
  display_name     = "engine_status_artifact"
  content_format   = "JAR"
  artifact_file    = "custom-functions/target/custom-functions-1.0.jar"
  environment {
    id = data.confluent_environment.demo.id
  }
  lifecycle {
    prevent_destroy = false
  }
}

# CREATE FUNCTION EngineStatus AS 'io.confluent.functions.EngineStatus'
# USING JAR 'confluent-artifact://cfa-0x3yn9';

# SELECT vehicle_id, engine_temperature, EngineStatus(engine_temperature) AS engine_status 
# FROM `demo_fleet_mgmt_sensors` LIMIT 20;
