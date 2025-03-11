CLUSTER_NAME ?= confluent
CONFIG_FILE ?= kind-config.yaml

# Create the Kind cluster
.PHONY: create-cluster
create-cluster:
	@echo "Creating Kind cluster '$(CLUSTER_NAME)' with config '$(CONFIG_FILE)'..."
	@kind create cluster --name $(CLUSTER_NAME) --config $(CONFIG_FILE)
	@echo "Cluster '$(CLUSTER_NAME)' created."

# Switch kubectl context to the cluster
.PHONY: switch-context
switch-context: check-cluster
	@echo "Switching kubectl context to 'kind-$(CLUSTER_NAME)'..."
	@kubectl config use-context kind-$(CLUSTER_NAME)
	@echo "Context switched to 'kind-$(CLUSTER_NAME)'."

# Check if the cluster is ready
.PHONY: check-cluster
check-cluster:
	@echo "Waiting for cluster '$(CLUSTER_NAME)' to be ready..."
	@kubectl wait --for=condition=Ready node -l kubernetes.io/hostname=$(CLUSTER_NAME)-control-plane --timeout=120s
	@echo "Cluster '$(CLUSTER_NAME)' is ready."

# Delete the Kind cluster
.PHONY: delete-cluster
delete-cluster:
	@echo "Deleting Kind cluster '$(CLUSTER_NAME)'..."
	@kind delete cluster --name $(CLUSTER_NAME)
	@echo "Cluster '$(CLUSTER_NAME)' deleted."

# Ensure commands are not cached
.PHONY: create-cluster switch-context check-cluster delete-cluster