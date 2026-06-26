
# Middleware Deployment Automation

## Overview

This repository automates the deployment, upgrade, downgrade, and rollback of middleware components running on Kubernetes.

The automation is designed to provide a consistent and repeatable deployment process while allowing operators to select different middleware versions based on project requirements.
<img width="1416" height="737" alt="image" src="https://github.com/user-attachments/assets/4fcc5902-ece0-42f7-9099-25edd0aa43dd" />

---

# Objectives

- Automate middleware deployment
- Support multiple middleware versions
- Standardize deployments across environments
- Simplify upgrades and rollbacks
- Reduce manual deployment effort
- Ensure reproducible infrastructure

---

# Middleware Components

The automation currently supports the following middleware.

| Middleware | Description |
|------------|-------------|
| AWS Load Balancer Controller | AWS EKS Ingress Controller |
| Kafka | Event Streaming Platform |
| Keycloak | Identity & Access Management |
| MongoDB | NoSQL Database |
| PostgreSQL | Relational Database |
| Redis Master | Primary Redis Node |
| Redis Replicas | Redis Replication |
| Vault | Secrets Management |
| SFTPGo | Secure File Transfer |
| IPS | Internal Processing Service |
| ICM | Internal Communication Module |
| LM | License Management |

---

# Version Strategy

Each middleware maintains three supported deployment versions.

| Version Type | Purpose |
|--------------|---------|
| Old Version | Previous production release used for rollback |
| Stable Version | Recommended version for production deployment |
| Latest Version | Most recent release available for testing or new features |

Example:

| Middleware | Old | Stable | Latest |
|------------|-----|---------|--------|
| Kafka | 3.8.0 | 4.0.0 | 4.1.0 |
| Redis | 7.2 | 7.4 | 8.0 |
| MongoDB | 7.0 | 8.0 | 8.1 |
| PostgreSQL | 15 | 16 | 17 |
| Keycloak | 25 | 26 | 27 |

> **Note:** Version numbers above are examples. Actual versions are maintained in this repository.

---

# Deployment Options

Users can deploy any supported version.

```
Old Version
      │
      ▼
Stable Version
      │
      ▼
Latest Version
```

Typical use cases

### Old Version

- Rollback
- Compatibility testing
- Emergency recovery

---

### Stable Version

Recommended for:

- Production
- UAT
- Pre-Production

Benefits

- Fully validated
- Performance tested
- Security reviewed
- Recommended by DevOps

---

### Latest Version

Recommended for

- Development
- Feature validation
- POC
- Testing new capabilities

---

# Jenkins Deployment Flow

```
User

   │

Select Middleware

   │

Select Version

   ├── Old
   ├── Stable
   └── Latest

   │

Select Environment

   ├── Dev
   ├── QA
   ├── UAT
   └── Production

   │

Run Jenkins Pipeline

   │

Validate Inputs

   │

Deploy Helm Chart

   │

Verify Deployment

   │

Deployment Complete
```

---

# Deployment Process

## Step 1

Choose Middleware

Example:

- Kafka
- Redis
- Vault
- PostgreSQL

---

## Step 2

Choose Version

- Old
- Stable
- Latest

---

## Step 3

Choose Namespace

Example

```
middleware-dev
middleware-qa
middleware-uat
middleware-prod
```

---

## Step 4

Deploy

Pipeline performs

- Pull Helm Chart
- Validate Values
- Deploy/Upgrade Release
- Wait for Pods
- Verify Readiness
- Report Status

---

# Rollback Process

Rollback can be performed by simply deploying the **Old Version**.

```
Latest

      │

Rollback

      ▼

Stable

      │

Rollback

      ▼

Old
```

No manual Helm rollback is required if version deployment is used.

---

# Repository Structure

```
middleware-deployment/

├── charts/
│   ├── kafka/
│   ├── redis/
│   ├── mongodb/
│   ├── postgresql/
│   ├── vault/
│   ├── keycloak/
│   └── ...
│
├── versions/
│   ├── kafka.yaml
│   ├── redis.yaml
│   ├── mongodb.yaml
│   └── ...
│
├── values/
│   ├── dev/
│   ├── qa/
│   ├── uat/
│   └── prod/
│
├── Jenkinsfile
└── README.md
```

---

# Version Configuration Example

```yaml
kafka:
  old: 3.8.0
  stable: 4.0.0
  latest: 4.1.0

redis:
  old: 7.2
  stable: 7.4
  latest: 8.0
```

---

# Benefits

- Standardized deployments
- Version-controlled releases
- Easy rollback
- Automated upgrades
- Minimal manual intervention
- Production-ready deployments
- GitOps-friendly workflow
- Reusable Helm charts

---

# Best Practices

✅ Deploy **Stable Version** in Production.

✅ Use **Latest Version** only for validation or development.

✅ Keep **Old Version** available for rollback.

✅ Test every new version before marking it as Stable.

✅ Maintain version history in Git.

---

# Future Enhancements

- Automatic version discovery
- Dependency validation
- Health check automation
- Canary deployment
- Blue-Green deployment
- Automated rollback on failure
- Slack/MS Teams notifications
- Deployment dashboard

---

# Example Deployment

```
Middleware : Kafka

Environment : UAT

Version : Stable

Action : Upgrade

Pipeline

✓ Validate Version
✓ Pull Helm Chart
✓ Upgrade Release
✓ Verify Pods
✓ Verify Services
✓ Deployment Successful
```

---

# Summary

This automation framework provides a reliable and standardized way to deploy middleware components across Kubernetes environments. By supporting **Old**, **Stable**, and **Latest** versions, teams can safely upgrade, test new releases, and quickly roll back when necessary while maintaining consistent deployment practices through Jenkins and Helm.
