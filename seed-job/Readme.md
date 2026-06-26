# Kafka Middleware Deployment

## Overview

This directory contains all the configuration files required to deploy and manage the Kafka middleware using Jenkins and Kubernetes.

The deployment supports secure Kafka communication using SASL authentication and uses Kubernetes ConfigMaps for managing configuration files.

---

# Directory Structure

```
kafka/
├── configmap-kafka-config.yaml
├── configmap-kafka-jmx.yaml
├── configmap-kafka-log4j.yaml
├── kafka-sasl.yaml
├── kafka.jenkinsfile
├── middleware.groovy
└── README.md
```

---

# File Description

| File | Purpose |
|------|---------|
| `configmap-kafka-config.yaml` | Kafka server configuration stored as a Kubernetes ConfigMap. |
| `configmap-kafka-jmx.yaml` | JMX monitoring configuration for Kafka metrics collection. |
| `configmap-kafka-log4j.yaml` | Log4j logging configuration for Kafka brokers. |
| `kafka-sasl.yaml` | Kafka SASL authentication configuration and Kubernetes deployment settings. |
| `kafka.jenkinsfile` | Jenkins pipeline responsible for deploying or upgrading Kafka. |
| `middleware.groovy` | Shared Jenkins Groovy library used during middleware deployment. |

---

# Deployment Workflow

```
Developer

     │

Commit Changes

     │

Git Repository

     │

Jenkins Pipeline

     │

Load middleware.groovy

     │

Load kafka.jenkinsfile

     │

Create / Update ConfigMaps

     │

Deploy Kafka

     │

Verify Deployment

     │

Deployment Successful
```

---

# Configuration Files

## configmap-kafka-config.yaml

Contains the primary Kafka broker configuration.

Typical settings include:

- Broker ID
- Listeners
- Advertised Listeners
- Log Directories
- Retention Policy
- Replication Settings
- KRaft Configuration
- Topic Defaults

---

## configmap-kafka-jmx.yaml

Contains JMX exporter configuration used for monitoring Kafka.

Typical settings include:

- JMX Port
- Exporter Rules
- Metrics Collection
- Prometheus Integration

---

## configmap-kafka-log4j.yaml

Contains Kafka logging configuration.

Typical settings include:

- Log Level
- Console Appender
- File Appender
- Rolling Log Policy
- Log Format

---

## kafka-sasl.yaml

Contains Kafka deployment configuration using SASL authentication.

Typically includes:

- Kubernetes Deployment
- StatefulSet
- Secrets
- SASL Credentials
- Volume Mounts
- Environment Variables
- Service Configuration

---

# Jenkins Pipeline

The `kafka.jenkinsfile` automates the complete deployment process.

Pipeline stages typically include:

1. Checkout Source Code
2. Validate Parameters
3. Load Shared Library
4. Create ConfigMaps
5. Deploy Kafka
6. Wait for Pods
7. Verify Deployment
8. Publish Deployment Status

---

# middleware.groovy

This shared Groovy script provides reusable deployment functions used across multiple middleware components.

Common responsibilities include:

- Namespace Validation
- Kubernetes Authentication
- ConfigMap Management
- Secret Management
- Deployment Execution
- Rollout Status Check
- Error Handling
- Logging

---

# Deployment Prerequisites

Before deploying Kafka, ensure the following are available:

- Kubernetes Cluster
- kubectl Access
- Jenkins Agent
- Required Namespace
- Storage Class
- Persistent Volumes
- Kafka Secrets
- SASL Credentials

---

# Deployment Steps

1. Update the required configuration files.
2. Commit the changes to Git.
3. Trigger the Jenkins Pipeline.
4. Select the target environment.
5. Monitor the deployment progress.
6. Verify that all Kafka pods are in the **Running** state.

---

# Verification

After deployment, verify the following:

- ConfigMaps are created successfully.
- Kafka pods are running.
- StatefulSet is healthy.
- Services are available.
- SASL authentication is functioning correctly.
- Logs contain no startup errors.

Example:

```bash
kubectl get pods

kubectl get configmap

kubectl get svc

kubectl logs <kafka-pod>
```

---

# Best Practices

- Store sensitive credentials in Kubernetes Secrets.
- Do not hardcode passwords in YAML files.
- Validate configuration before deployment.
- Keep ConfigMaps version controlled.
- Test configuration changes in lower environments before production.
- Maintain consistent naming conventions.

---

# Related Files

```
configmap-kafka-config.yaml
configmap-kafka-jmx.yaml
configmap-kafka-log4j.yaml
kafka-sasl.yaml
kafka.jenkinsfile
middleware.groovy
```

---

# Summary

This directory contains all files required to automate Kafka deployment using Jenkins and Kubernetes. Configuration is managed through ConfigMaps, authentication is handled using SASL, and deployment is fully automated through reusable Jenkins pipelines and shared Groovy libraries.
