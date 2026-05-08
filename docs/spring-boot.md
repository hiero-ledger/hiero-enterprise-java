# Spring Boot Integration

The `hiero-enterprise-spring` module provides Spring Boot integration for the managed Hiero services.

## Dependency

```xml
<dependency>
    <groupId>org.hiero</groupId>
    <artifactId>hiero-enterprise-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

## Enable Hiero support

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

## Configuration

The Spring integration uses properties under `spring.hiero.*`.

Example:

```properties
spring.hiero.accountId=0.0.53854625
spring.hiero.privateKey=YOUR_DER_ENCODED_PRIVATE_KEY
spring.hiero.network.name=hedera-testnet
```

Important properties:

- `spring.hiero.accountId`
- `spring.hiero.privateKey`
- `spring.hiero.network.name`
- `spring.hiero.network.mirrorNode`
- `spring.hiero.network.requestTimeoutInMs`

You can also provide custom nodes through `spring.hiero.network.nodes`. When custom nodes are provided, the named network is ignored.

## Using managed services

Once Hiero support is enabled, the main services can be injected as Spring beans.

Example:

```java
@Service
public class HieroFileService {

    private final FileClient fileClient;

    public HieroFileService(FileClient fileClient) {
        this.fileClient = fileClient;
    }

    public String readFile(FileId fileId) {
        return new String(fileClient.readFile(fileId));
    }
}
```

## What the module provides

The Spring module wires:

- the Hiero configuration and context
- protocol-layer clients
- mirror-node clients and repositories
- Spring-specific REST and JSON converter implementations

## Sample application

See the Spring sample module in the repository for a minimal example application using this integration.
