# Getting Started

This guide shows the basic setup needed to use Hiero Enterprise Java in an application.

## Choose an integration module

Use one of the framework modules depending on your application:

- `hiero-enterprise-spring` for Spring Boot
- `hiero-enterprise-microprofile` for MicroProfile or Quarkus

The base module provides the shared APIs and implementations, but most applications should start with one of the framework integrations.

## Spring Boot dependency

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

## MicroProfile dependency

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-microprofile</artifactId>
    <version>VERSION</version>
</dependency>
```

## Required configuration

In both integrations, you need to configure:

- an operator account ID
- the private key for that operator account
- the target network or custom node settings

The project currently supports DER-encoded ECDSA private keys for the operator configuration.

## Common setup steps

1. Add the dependency for your framework
2. Configure the operator account and network
3. Enable or produce the Hiero services
4. Inject a managed client such as `AccountClient`, `FileClient`, or `SmartContractClient`

## Network choices

You can use one of the predefined Hedera-based networks or provide custom nodes and a mirror node endpoint. This is useful for local or custom environments such as Solo-based setups.

## Running the build

The project uses Maven and includes the Maven wrapper:

```bash
./mvnw verify
```

Some tests require real network credentials. If no account is configured, those tests will fail.

## Next steps

- See [Spring Boot](spring-boot.md) for Spring-specific setup.
- See [MicroProfile](microprofile.md) for MicroProfile-specific setup.
- See [Base / Managed Services](managed-services.md) for the available clients and repositories.
