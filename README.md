# Confluent Flink Blueprints: Exploring Apache Flink with Kafka and Confluent Cloud

This project serves as a hands-on demonstration of Apache Flink's APIs 
and their seamless integration with Apache Kafka and Confluent Cloud.

## Modules

### [1-cc-flink-env](1-cc-flink-env) 
Sets up the foundational Confluent Cloud infrastructure required for 
running Apache Flink workloads. This module provisions Kafka cluster, 
topics, and other essential resources in Confluent Cloud.

### [2-cc-flink-sqlapi](2-cc-flink-sqlapi)
Demonstrates how to provision and configure Flink SQL resources on 
Confluent Cloud, enabling SQL-based stream processing over Kafka data. 
Includes examples of defining SQL queries for real-time analytics.

### [3-cc-flink-tableapi](3-cc-flink-tableapi)
Showcases the Flink Table API by building and deploying a sample 
application that processes data using a relational programming model. 
Includes steps for deployment to Confluent Cloud.

### [4-cp-flink-env](4-cp-flink-env)
Provisions a local Kubernetes environment and deploys the Confluent 
Flink Manager, setting the stage for running Flink workloads on-premises.

### [5-cp-flink-datastream](5-cp-flink-datastream)
Provides a sample application implementation using the Flink 
DataStream API, deployed on a local Kubernetes cluster.

## TODO

