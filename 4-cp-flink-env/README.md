# Confluent Manager for Apache Flink

[Get Started with Confluent Platform for Apache Flink](https://docs.confluent.io/platform/current/flink/get-started.html#)

## Prerequisites 

```sh
brew install helm kind kubectl kubectx
brew install confluentinc/tap/cli

# Define aliases for convenience
alias k='kubectl'
```

## Kubernetes Cluster Setup

```sh
# Create a Kubernetes cluster using Kind with a custom configuration
kind create cluster --name confluent --config kind-config.yaml

# Display cluster information for the 'kind-confluent' context
kubectl cluster-info --context kind-confluent

# Switch the active kubectl context to 'kind-confluent'
kubectl config use-context kind-confluent

# Delete the Kind cluster after use
kind delete cluster --name confluent
```

## Confluent Flink Manager - Setup Using `helmfile`

[Declaratively deploy your Kubernetes manifests with Helmfile](https://github.com/helmfile/helmfile)

```shell
# Install the cert-manager into your Kubernetes cluster.
kubectl create -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.16/cert-manager.yaml

# Apply Helm charts defined in helmfile.yaml configuration.
helmfile apply

# Verify the Helm installations across all namespaces.
helm list --all-namespaces
```

## Confluent Flink Manager - Manual Setup

```sh
# Install the cert-manager into your Kubernetes cluster.
kubectl create -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.16/cert-manager.yaml

# Add the Confluent Platform Helm repository to your local Helm configuration.
helm repo add confluentinc https://packages.confluent.io/helm

# Update the local Helm repository cache to ensure you have the latest chart versions.
helm repo update

# Install or upgrade the Confluent Manager for Apache Flink.
helm upgrade --namespace flink-manager --create-namespace --install \
confluent-manager-for-flink confluentinc/confluent-manager-for-apache-flink 

# Install or upgrade the Flink Kubernetes Operator using Helm.
helm upgrade --namespace flink-operator --create-namespace --install \
cp-flink-kubernetes-operator confluentinc/flink-kubernetes-operator --set watchNamespaces={flink}

# Install or upgrade the Kubernetes Dashboard.
helm upgrade --namespace kubernetes-dashboard --create-namespace \
--install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard

# Verify the Helm installations across all namespaces.
helm list --all-namespaces
```

## Run Example Flink Application / No Kafka

[Deploy Flink jobs](https://docs.confluent.io/platform/current/flink/get-started.html#step-2-deploy-af-jobs)

```sh
# Forward local port 8080 to the CMF service on port 80 in the 'flink-manager' namespace.
# This allows local access to the CMF service at http://localhost:8080.
kubectl port-forward -n flink-manager svc/cmf-service 8080:80

# Create a separate Kubernetes namespace named 'flink' for better resource isolation.
kubectl create namespace flink

# Create a new Confluent Flink environment named 'development' in the 'flink' namespace.
# The environment will connect to the CMF service through the forwarded URL.
confluent flink environment create development --kubernetes-namespace flink --url http://localhost:8080

# Deploy an example Flink job using the configuration from 'example-deployment.json'.
confluent flink application create --environment development --url http://localhost:8080 example-deployment.json

# Forward the Flink Web UI for the 'basic-example' application.
# This allows local access to the Flink Web UI at http://localhost:8080.
confluent flink application web-ui-forward --environment development basic-example --url http://localhost:8080

# Delete the 'basic-example' Flink application from the 'development' environment.
confluent flink application delete --environment development basic-example --url http://localhost:8080
```

## Optional: Kubernetes Dashboard

```sh
# Install or upgrade the Kubernetes Dashboard using Helm 
helm upgrade --namespace kubernetes-dashboard --create-namespace \
--install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard 

# Create a service account named 'cluster-admin'
kubectl create serviceaccount cluster-admin

# Create a cluster role binding to give the 'cluster-admin' required permissions
kubectl create clusterrolebinding serviceaccounts-cluster-admin \
--clusterrole=cluster-admin --group=system:serviceaccounts

# Create a token for the 'cluster-admin' service account
kubectl create token cluster-admin

# Forward local port 8443 to the Kubernetes Dashboard service port to access Web UI
kubectl -n kubernetes-dashboard port-forward svc/kubernetes-dashboard-kong-proxy 8443:443
```