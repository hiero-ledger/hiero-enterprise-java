# Subscription

`Subscription` represents an active subscription to a resource or event source.

A subscription is returned by client methods that register a listener or subscribe to events. It can be cancelled by calling `unsubscribe()`.

---

## Methods

| Method          | Description                                                  |
| :-------------- | :----------------------------------------------------------- |
| `unsubscribe()` | Cancels the subscription and stops receiving further events. |

---

## Unsubscribe

The `Subscription` returned by a subscription method can be used to cancel the subscription when it is no longer needed.

```java
Subscription subscription =
    topicClient.subscribeTopic(
        topicId,
        message -> {
            System.out.println("Received message: " + message);
        }
    );

// Cancel the subscription
subscription.unsubscribe();
```

Calling `unsubscribe()` cancels the active subscription and stops the associated listener from receiving further events.

!!! tip 

    Keep a reference to the returned `Subscription` if you need to cancel the subscription later.


!!! note 

    The `Subscription` interface is not limited to topic subscriptions. 
    It provides a generic abstraction that can also be used by other client APIs that support subscriptions.

