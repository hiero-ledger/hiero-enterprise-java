# Base / Managed Services

The base module defines the main service interfaces used by both the Spring Boot and MicroProfile integrations.

## Core configuration objects

- `HieroConfig` creates framework-independent configuration
- `HieroContext` holds the active network and operator account information

These objects are used internally by the framework integrations and shared implementations.

## Transaction-oriented clients

The following managed clients are used for write or transaction-based operations:

- `AccountClient`
- `FileClient`
- `FungibleTokenClient`
- `NftClient`
- `SmartContractClient`
- `TopicClient`
- `HookClient`

These clients build on the protocol-layer support in the base module.

## Contract verification

- `ContractVerificationClient` provides smart-contract verification support

## Mirror-node access

The base module also exposes a lower-level `MirrorNodeClient` plus repository-style abstractions for query use cases.

Available repository interfaces include:

- `AccountRepository`
- `BlockRepository`
- `ContractRepository`
- `NetworkRepository`
- `NftRepository`
- `TokenRepository`
- `TopicRepository`
- `TransactionRepository`

These repositories are intended for read-only access to data exposed through the mirror node.

## Protocol-layer access

- `ProtocolLayerClient` provides lower-level access for transaction submission and related protocol operations

Most applications should prefer the higher-level managed clients unless they need a lower-level integration point.

## Framework availability

The services in this module are exposed through:

- Spring beans in `hiero-enterprise-spring`
- CDI producers in `hiero-enterprise-microprofile`

That means application code can usually depend on the shared interfaces while letting the framework module provide the implementation.
