# Observing Smart Contract Events

This guide describes how to use the `hiero-enterprise-java` library to observe and react to smart contract events emitted on a Hiero network.

## Configuration

To enable event observation, ensure `spring.hiero.eventsEnabled` is set to `true` in your `application.properties`:

```properties
spring.hiero.eventsEnabled=true
spring.hiero.mirrorNodeSupported=true
```

## Subscribing to Events

The `HieroEventSubscriber` service is the primary entry point for registering event listeners.

### Basic Subscription

You can subscribe to all events from a specific contract:

```java
@Service
public class MyService {

    public MyService(HieroEventSubscriber subscriber) {
        subscriber.subscribe(ContractId.fromString("0.0.1234"), event -> {
            System.out.println("Received event: " + event.eventName());
        });
    }
}
```

### Filtered Subscription

You can also filter by event signature:

```java
subscriber.subscribe(
    ContractId.fromString("0.0.1234"), 
    "Transfer(address,address,uint256)", 
    event -> {
        // Handle transfer
    }
);
```

## How it Works

1. **Polling**: The `DefaultEventObserver` background process periodically polls the Mirror Node REST API for new logs associated with subscribed contracts.
2. **Decoding**: Logs are fetched and decoded into `ContractEvent` objects.
3. **Notification**: Registered listeners are invoked with the decoded events.

## Sample Application

A complete working example is available in the `hiero-enterprise-events-sample` module. To run it:

1. Configure your operator ID and key in `application.properties`.
2. Run the application: `mvn spring-boot:run` in the sample directory.
3. Observe the console for incoming events.
4. Access `http://localhost:8080/api/events/last` to see recent events.

## Deployment Helper

For testing purposes, you can use `ContractDeploymentHelper` to deploy contracts that emit events:

```java
ContractDeploymentHelper helper = new ContractDeploymentHelper(contractClient);
ContractId contractId = helper.deployTestEventContract();
```
