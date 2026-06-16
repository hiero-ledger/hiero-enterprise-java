# Spring Boot Integration
The `hiero-enterprise-spring` module provides seamless Spring Boot integration for managed Hiero services, including client configuration, network setup, and dependency injection support.

---

## Dependency

Add the following dependency to your `pom.xml` to pull in the Spring Boot starter:

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

---

## Configuration

The Spring integration automatically processes properties defined within the `spring.hiero.*` namespace.

Configure your operator account credentials and target network in `application.properties` or `application.yml`.

=== "application.properties"
    ```properties
    spring.hiero.accountId=YOUR_ACCOUNT_ID
    spring.hiero.privateKey=YOUR_PRIVATE_KEY
    spring.hiero.network.name=hedera-testnet
    ```

=== "application.yml"
    ```yaml
    spring:
        hiero:
            accountId: YOUR_ACCOUNT_ID
            privateKey: YOUR_PRIVATE_KEY
            network:
                name: hedera-testnet
    ```

### Configuration Properties

| Property                                                                                                                                                                  | Description                                                                                        |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------|
| `spring.hiero.accountId`                                                                                                                                                  | **Required.** Your Hiero operator account ID.                                                      |
| `spring.hiero.privateKey`                                                                                                                                                 | **Required.** Your private key used to sign transactions.                                          |
| `spring.hiero.network.name`                                                                                                                                               | Target network name. Valid values: `hedera-mainnet`, `hedera-testnet`. (Default `hedera-mainnet`). |
| `spring.hiero.network.mirrorNode`                                                                                                                                         | *Optional.* Explicit endpoint URL for a custom mirror node.                                        |
| `spring.hiero.network.requestTimeoutInMs`                                                                                                                                 | *Optional.* Network request timeout threshold in milliseconds.                                     |

---
### Configure Custom Network Nodes

If your enterprise environment requires connecting to specific nodes, you can override the default named network defaults using:

=== "application.properties"
    ```properties
    spring.hiero.accountId=YOUR_ACCOUNT_ID
    spring.hiero.privateKey=YOUR_PRIVATE_KEY

    spring.hiero.network.nodes[0].ip=1.2.3.4
    spring.hiero.network.nodes[0].port=50211
    spring.hiero.network.nodes[0].account=0.0.3

    spring.hiero.network.nodes[1].ip=5.6.7.8
    spring.hiero.network.nodes[1].port=50211
    spring.hiero.network.nodes[1].account=0.0.4
    ```
=== "application.yml"
    ```yaml
    spring:
        hiero:
            accountId: YOUR_ACCOUNT_ID
            privateKey: YOUR_PRIVATE_KEY
            network:
                nodes:
                    - ip: "1.2.3.4"
                      port: 50211
                      account: "0.0.3"
                    - ip: "5.6.7.8"
                      port: 50211
                      account: "0.0.4"
    ```

!!! note
    
     When custom nodes are configured, the framework ignores the named network configuration from `spring.hiero.network.name` 
     and uses the provided node mappings instead.

---
## Enable Hiero Support

Add `@EnableHiero` to your Spring Boot application:

```java
import org.hiero.spring.EnableHiero;

@SpringBootApplication
@EnableHiero
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
The annotation registers the required Hiero clients and infrastructure components in the Spring application context.

---
## Inject Hiero Services

After enabling Hiero support, services are available as standard Spring beans and can be injected into your application components.

Example:
```java
@Service
public class HieroAccountService {
    private final AccountClient client;

    public HieroAccountService(AccountClient client) {
        this.client = client;
    }

    public String createAccount() {
        try {
            final Account account = client.createAccount();
            return "Account " + account.accountId() + " created!";
        } catch (final HieroException e) {
          throw new RuntimeException("Error in Hiero call", e);
        }
    }
}
```
---
## Sample Application

A complete working example is available in the repository under the [Spring Sample](https://github.com/hiero-ledger/hiero-enterprise-java/tree/main/hiero-enterprise-spring-sample) module.
It demonstrates a minimal setup using this integration and recommended project structure.
