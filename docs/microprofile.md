# MicroProfile Integration

The `hiero-enterprise-microprofile` module provides CDI-based integration for Eclipse MicroProfile runtimes such as Quarkus.

## Dependency

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-microprofile</artifactId>
    <version>VERSION</version>
</dependency>
```

## Configuration

The MicroProfile integration reads configuration from the `hiero.*` namespace.

Example:

```properties
hiero.accountId=0.0.53854625
hiero.privateKey=YOUR_DER_ENCODED_PRIVATE_KEY
hiero.network.name=hedera-testnet
```

Additional network-related settings include:

- `hiero.network.nodes`
- `hiero.network.mirrornode`
- `hiero.network.requestTimeoutInMs`

When `hiero.network.nodes` is provided, the named network setting is not used.

## Managed services

The module uses CDI producers to expose the same service interfaces defined in the base module.

Examples include:

- `AccountClient`
- `FileClient`
- `FungibleTokenClient`
- `NftClient`
- `SmartContractClient`
- `TopicClient`
- `MirrorNodeClient`
- repository interfaces such as `AccountRepository`, `TokenRepository`, and `NftRepository`

## Injection example

```java
@ApplicationScoped
public class HieroAccountService {

    @Inject
    AccountClient accountClient;
}
```

## What the module provides

The MicroProfile module wires:

- configuration objects for operator and network settings
- a shared `HieroConfig` and `HieroContext`
- CDI-produced protocol-layer clients
- CDI-produced mirror-node clients and repositories
- MicroProfile-specific REST and JSON converter implementations

## Sample application

See the MicroProfile sample module in the repository for a minimal example using this integration.
