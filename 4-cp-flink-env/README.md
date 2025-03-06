# Confluent Manager for Apache Flink

TODO flink modes explained
TODO autoscaler

https://docs.confluent.io/platform/current/flink/get-started.html#

## Prerequisites 

```sh
brew update && brew upgrade
brew install helm kind kubectl krew kubectx
```

## K8S Cluster Setup

```sh
# Create the K8S cluster
kind create cluster --name confluent --config kind.yaml
kubectl cluster-info --context kind-confluent

# Switch the context
kubectl config get-contexts
kubectl config use-context kind-confluent
kubectl config current-context

alias k='kubectl' 
alias kgp='kubectl get pods -o wide' 
```

## Confluent CLI

If you have not installed Confluent Platform 7.8, you must install the latest version of the Confluent CLI and set the CONFLUENT_HOME environment variable.

```sh
# Download Confluent packages and unzip
mkdir -p "$HOME/confluent"
wget -P "$HOME/confluent" https://packages.confluent.io/archive/7.9/confluent-7.9.0.zip
unzip "$HOME/confluent/confluent-7.9.0.zip"

# Export CONFLUENT_HOME
export CONFLUENT_HOME="$HOME/confluent/confluent-7.9.0"
```

```sh
# Add the Confluent Platform Helm repository to your local Helm configuration.
helm repo add confluentinc https://packages.confluent.io/helm

# Update the local Helm repository cache to ensure you have the latest chart versions.
helm repo update

# Install the cert-manager into your Kubernetes cluster.
kubectl create -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.16/cert-manager.yaml

# Create separate Kubernetes namespaces for better resource isolation and organization.
# kubectl create namespace flink-manager
# kubectl create namespace flink-operator
kubectl create namespace flink

# Install or upgrade the Confluent Manager for Apache Flink.
helm upgrade --namespace flink-manager --create-namespace --install \
confluent-manager-for-flink confluentinc/confluent-manager-for-apache-flink 

# Install or upgrade the Flink Kubernetes Operator using Helm.
helm upgrade --namespace flink-operator --create-namespace --install \
cp-flink-kubernetes-operator confluentinc/flink-kubernetes-operator --set watchNamespaces={flink}

# Verify the Helm installations across all namespaces.
# This command lists all Helm releases, confirming that the Flink operator and Confluent Manager are correctly deployed.
helm list --all-namespaces

# Switch the namespace
kubectl get namespaces
kubectl create namespace demo
kubectl config set-context --current --namespace=demo
```

## Run Example Flink Application / No Kafka

https://docs.confluent.io/platform/current/flink/get-started.html#step-2-deploy-af-jobs

```sh
# Open port forwarding to CMF.
kubectl port-forward -n flink-manager svc/cmf-service 8080:80

# Create Environment
confluent flink environment create development --kubernetes-namespace flink --url http://localhost:8080

# Deploy example Flink jobs
confluent flink application create --environment development --url http://localhost:8080 example-deployment.json

# Access Web UI
confluent flink application web-ui-forward --environment development basic-example --url http://localhost:8080

# Delete the application
confluent flink application delete --environment development basic-example --url http://localhost:8080
```

## Optional: Kubernetes Dashboard

```sh
helm upgrade --namespace kubernetes-dashboard --create-namespace \
--install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard 

kubectl create serviceaccount cluster-admin &
kubectl create clusterrolebinding serviceaccounts-cluster-admin \
--clusterrole=cluster-admin --group=system:serviceaccounts

kubectl create token cluster-admin
kubectl -n kubernetes-dashboard port-forward svc/kubernetes-dashboard-kong-proxy 8443:443

```