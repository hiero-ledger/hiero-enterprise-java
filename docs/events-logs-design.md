# Design Document: Smart Contract Event Observation

## Overview
This feature adds the ability to observe smart contract events and logs emitted on a Hiero network via the Mirror Node. It provides a high-level Java API and Spring Boot integration to simplify event-driven development.

## Package Structure
- `org.hiero.base.events`: Core interfaces and models.
- `org.hiero.base.mirrornode`: Repository interfaces for log retrieval.
- `org.hiero.spring.implementation`: Spring-specific auto-configuration and listener logic.

## Key Components

### `ContractEvent` (Model)
Represents a decoded event.
- `contractId`: The address of the emitting contract.
- `eventName`: Name of the event (e.g., "Transfer").
- `parameters`: Map of named parameters and their decoded values.
- `timestamp`: When the event was recorded.
- `blockNumber`: The block containing the event.

### `ContractEventListener` (Interface)
A callback interface for processing events.
```java
public interface ContractEventListener {
    void onEvent(ContractEvent event);
}
```

### `EventSubscription` (Handle)
Allows managing an active subscription (e.g., unsubscribing).

### `EventRepository` (Mirror Node)
Fetches logs from the Mirror Node REST API.
- Supports filtering by contract, event signature, and time range.

### `EventObserver` (Engine)
A background process (polling-based) that monitors the Mirror Node for new logs and notifies registered listeners.

## Spring Boot Integration
- `@EnableHieroEvents`: Annotation to enable the event observation system.
- `HieroEventSubscriber`: Service to register listeners programmatically.
- Configuration properties for polling interval and Mirror Node endpoints.

## Example Usage
```java
@Component
public class MyEventListener implements ContractEventListener {
    @Override
    public void onEvent(ContractEvent event) {
        if ("Transfer".equals(event.eventName())) {
            // Process transfer
        }
    }
}
```
