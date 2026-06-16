# MicroProfile Integration

The `hiero-enterprise-microprofile` module provides integration support for Eclipse MicroProfile-compatible applications such as Quarkus and Helidon.

---

## Dependency
Add the following dependency to your `pom.xml` build configuration:

```xml
<dependency>
  <groupId>org.hiero</groupId>
  <artifactId>hiero-enterprise-microprofile</artifactId>
  <version>VERSION</version>
</dependency>
```

---

## Configuration

The MicroProfile integration automatically loads configuration from the `hiero.*` property namespace.

Configuration can be provided through standard MicroProfile configuration sources, including:

- `microprofile-config.properties`
- `application.properties` (eg. Quarkus)
- Environment variables


Add these settings to your configuration file:

```properties
hiero.accountId=0.0.53854625
hiero.privateKey=YOUR_DER_ENCODED_PRIVATE_KEY
hiero.network.name=hedera-testnet
```

### Configuration Properties

| Property | Description |
|:---------|:------------|
| `hiero.accountId` | **Required.** Your Hiero operator account ID. |
| `hiero.privateKey` | **Required.** Your DER-encoded private key used to sign transactions. |
| `hiero.network.name` | Target network name. Valid values: `hedera-mainnet`, `hedera-testnet`. (Default `hedera-mainnet`). |
| `hiero.network.mirrornode` | *Optional.* Explicit endpoint URL for a custom mirror node. |
| `hiero.network.requestTimeoutInMs` | *Optional.* Network request timeout threshold in milliseconds. |

### Configure Custom Network Nodes

If your enterprise environment requires connecting to specific nodes, you can override the default network configuration using:

```properties
hiero.accountId=YOUR_ACCOUNT_ID
hiero.privateKey=YOUR_PRIVATE_KEY

hiero.network.nodes=1.2.3.4:50211:0.0.3,5.6.7.8:50211:0.0.4
```

!!! note

    Providing custom node mappings causes the framework to ignore the named `hiero.network.name` configuration and use the provided nodes instead.

---

## Using Hiero Services
After adding the dependency and configuration, Hiero clients can be injected directly into your application using CDI.

```java
@Path("/")
public class HieroAccountEndpoint { 
    private final AccountClient client;

    @Inject
    public HieroEndpoint(final AccountClient client, final BlockRepository blockRepository) {
        this.client = client;
        this.blockRepository = blockRepository;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String createAccount() {
        try {
            final Account account = client.createAccount();
            return "Account created!";
        } catch (final HieroException e) {
            throw new RuntimeException("Error in Hiero call", e);
        }
    }
}
```

---
## Sample Application

A complete working example is available in the repository under the [Microprofile Sample](https://github.com/hiero-ledger/hiero-enterprise-java/tree/main/hiero-enterprise-microprofile-sample) module.
It demonstrates a minimal setup using this integration and recommended project structure.
