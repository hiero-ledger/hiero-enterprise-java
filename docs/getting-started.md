# Getting Started

**Hiero Enterprise Java** is an enterprise-grade framework for building Java applications on the [Hiero Network](https://hiero.org).

It provides a framework friendly layer on top of the Hiero SDK, with built-in dependency injection, centralized configuration, and reusable services. By integrating with Spring Boot and MicroProfile, Hiero Enterprise helps developers focus on business logic rather than SDK setup and infrastructure concerns.

---
## Choose an Integration Module

Hiero Enterprise Java provides framework-specific integrations for modern Java applications.

Choose the module that matches your application framework:

- `hiero-enterprise-spring` for Spring Boot applications
- `hiero-enterprise-microprofile` for MicroProfile and Quarkus applications

Both modules are built on top of the Base module, which contains the shared APIs, clients, repositories, and service contracts used across the project.

---
## Add a Dependency

Add the dependency for your chosen framework.

### Spring Boot

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

### MicroProfile

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-microprofile</artifactId>
    <version>VERSION</version>
</dependency>
```

---
## Configure Your Application

To connect to a Hiero network, you must configure an operator account and network settings.

The following configuration is required:

- **Operator Account ID** — the account used to submit transactions and pay transaction fees.
- **Operator Private Key** — the private key used to sign transactions on behalf of the operator account.
- **Network Configuration** — the target network (`testnet`, `previewnet`, or `mainnet`) or a custom node configuration.

Don't have an account yet? Create a free Testnet account using the Hedera Portal.

[Create a Testnet Account](https://portal.hedera.com){ .md-button }

After creating your account, keep your Account ID and private key available. You will need them when configuring Hiero Enterprise.

!!! warning

    Testnet provides 1000 HBAR for testing, which refreshes every 24 hours for development purposes.

---
## Next Steps

Continue with one of the following guides:

- [Spring Boot](spring-boot.md) - Configure and use Hiero Enterprise in Spring applications.
- [MicroProfile](microprofile.md) - Configure and use Hiero Enterprise in MicroProfile and Quarkus applications.
- [Clients & Repositories](client-and-repository.md) - Explore the available clients and repositories services.