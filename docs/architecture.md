# Architecture

Hiero Enterprise Java is split into three main modules:

- `hiero-enterprise-base` contains the shared core APIs and implementations.
- `hiero-enterprise-spring` integrates the base module with Spring Boot.
- `hiero-enterprise-microprofile` integrates the base module with Eclipse MicroProfile implementations such as Quarkus.

## Module roles

### Base module

The base module contains the core service contracts and their shared implementations. This includes:

- protocol-layer clients for submitting transactions
- mirror-node clients and repositories for query use cases
- shared data models such as accounts, tokens, NFTs, topics, contracts, and pages
- configuration abstractions such as `HieroConfig` and `HieroContext`

This module is the foundation used by both framework integrations.

### Spring Boot module

The Spring Boot module provides:

- the `@EnableHiero` annotation
- Spring Boot configuration properties under `spring.hiero.*`
- Spring-managed beans for the managed services defined in the base module
- Spring-specific mirror-node REST and JSON converter implementations

Applications using Spring Boot depend on this module instead of wiring the base services manually.

### MicroProfile module

The MicroProfile module provides:

- CDI producers for the managed services
- MicroProfile configuration classes under `hiero.*`
- MicroProfile-specific mirror-node REST and JSON converter implementations

Applications using MicroProfile or Quarkus can inject the same service interfaces that are available in the base module.

## Main layers

The project is organized around two main interaction paths:

1. Protocol-layer access for transactions and network operations
2. Mirror-node access for read-only queries

In practice, applications typically work with high-level clients and repositories instead of using the lower-level SDK directly.

## Interaction flow

At a high level, the flow looks like this:

1. Framework-specific configuration creates a `HieroConfig`
2. `HieroConfig` creates a `HieroContext`
3. The framework module exposes managed clients and repositories
4. Transaction use cases go through the protocol-layer client
5. Query use cases go through the mirror-node client and repositories

## External dependencies

The project currently builds on top of the Hedera Java SDK and is being migrated to the Hiero namespace over time. Mirror-node queries are handled through REST-based clients exposed by the framework modules.

## Where to continue

- See [Getting Started](getting-started.md) for basic setup.
- See [Spring Boot](spring-boot.md) for Spring configuration and usage.
- See [MicroProfile](microprofile.md) for CDI and MicroProfile configuration.
- See [Base / Managed Services](managed-services.md) for the main services exposed to applications.
