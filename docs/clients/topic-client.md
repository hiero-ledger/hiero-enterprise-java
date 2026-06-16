# Topic Client

`TopicClient` provides APIs for managing Hiero Consensus Service (HCS) topics, including topic creation, updates, deletion, and message submission.

!!! note

    Topic operations that submit transactions to the Hiero network require HBAR to pay transaction fees.
    The configured operator account is used as the transaction payer and must have sufficient HBAR balance.

---

## Methods

| Method | Description |
|:-------|:------------|
| `createTopic()` | Creates a new public topic using the operator account as admin key. |
| `createTopic(PrivateKey adminKey)` | Creates a new topic with the provided admin key. |
| `createTopic(String memo)` | Creates a new topic with the provided memo. |
| `createTopic(PrivateKey adminKey, String memo)` | Creates a new topic with admin key and memo. |
| `createPrivateTopic(PrivateKey submitKey)` | Creates a private topic with the provided submit key. |
| `createPrivateTopic(PrivateKey adminKey, PrivateKey submitKey)` | Creates a private topic with admin key and submit key. |
| `createPrivateTopic(PrivateKey submitKey, String memo)` | Creates a private topic with submit key and memo. |
| `createPrivateTopic(PrivateKey adminKey, PrivateKey submitKey, String memo)` | Creates a private topic with admin key, submit key, and memo. |
| `updateTopic(TopicId topicId, String memo)` | Updates a topic memo using the operator account as admin key. |
| `updateTopic(TopicId topicId, PrivateKey adminKey, String memo)` | Updates a topic using a provided admin key and memo. |
| `updateTopic(TopicId topicId, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)` | Updates topic admin key, submit key, and memo using operator account. |
| `updateTopic(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)` | Updates topic admin key, submit key, and memo using the provided admin key. |
| `updateAdminKey(TopicId topicId, PrivateKey updatedAdminKey)` | Updates the topic admin key using the operator account. |
| `updateAdminKey(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey)` | Updates the topic admin key using the provided admin key. |
| `updateSubmitKey(TopicId topicId, PrivateKey submitKey)` | Updates the topic submit key using the operator account. |
| `updateSubmitKey(TopicId topicId, PrivateKey adminKey, PrivateKey submitKey)` | Updates the topic submit key using the provided admin key. |
| `deleteTopic(TopicId topicId)` | Deletes a topic using the operator account. |
| `deleteTopic(String topicId)` | Deletes a topic using a topic ID string. |
| `deleteTopic(TopicId topicId, PrivateKey adminKey)` | Deletes a topic using the provided admin key. |
| `deleteTopic(String topicId, String adminKey)` | Deletes a topic using topic ID and admin key strings. |
| `submitMessage(TopicId topicId, byte[] message)` | Submits a byte array message to a topic. |
| `submitMessage(String topicId, byte[] message)` | Submits a byte array message using a topic ID string. |
| `submitMessage(TopicId topicId, String message)` | Submits a string message to a topic. |
| `submitMessage(String topicId, String message)` | Submits a string message using a topic ID string. |
| `submitMessage(TopicId topicId, PrivateKey submitKey, byte[] message)` | Submits a byte array message using a custom submit key. |
| `submitMessage(String topicId, String submitKey, byte[] message)` | Submits a byte array message using topic ID and submit key strings. |
| `submitMessage(TopicId topicId, PrivateKey submitKey, String message)` | Submits a string message using a custom submit key. |
| `submitMessage(String topicId, String submitKey, String message)` | Submits a string message using topic ID and submit key strings. |

---

## Create Topic

Creates a public topic using the operator account as the admin key.

```java title="createTopic()"
TopicId topicId = topicClient.createTopic();
```

```java title="createTopic(PrivateKey adminKey)"
PrivateKey adminKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createTopic(adminKey);
```

```java title="createTopic(String memo)"
TopicId topicId = topicClient.createTopic("My topic");
```

```java title="createTopic(PrivateKey adminKey, String memo)"
PrivateKey adminKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createTopic(
        adminKey,
        "My topic"
    );
```

---

## Create Private Topic

Private topics require a submit key for publishing messages.

```java title="createPrivateTopic(PrivateKey submitKey)"
PrivateKey submitKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createPrivateTopic(submitKey);
```

```java title="createPrivateTopic(PrivateKey adminKey, PrivateKey submitKey)"
PrivateKey adminKey = PrivateKey.generateED25519();
PrivateKey submitKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createPrivateTopic(
        adminKey,
        submitKey
    );
```

```java title="createPrivateTopic(PrivateKey submitKey, String memo)"
PrivateKey submitKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createPrivateTopic(
        submitKey,
        "Private topic"
    );
```

```java title="createPrivateTopic(PrivateKey adminKey, PrivateKey submitKey, String memo)"
PrivateKey adminKey = PrivateKey.generateED25519();
PrivateKey submitKey = PrivateKey.generateED25519();

TopicId topicId =
    topicClient.createPrivateTopic(
        adminKey,
        submitKey,
        "Private topic"
    );
```

---

## Update Topic

Update topic properties such as memo, admin key, and submit key.

```java title="updateTopic(TopicId topicId, String memo)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.updateTopic(
    topicId,
    "Updated topic"
);
```

```java title="updateTopic(TopicId topicId, PrivateKey adminKey, String memo)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.updateTopic(
    topicId,
    adminKey,
    "Updated topic"
);
```

```java title="updateTopic(TopicId topicId, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)"
PrivateKey updatedAdminKey = PrivateKey.generateED25519();

topicClient.updateTopic(
    topicId,
    updatedAdminKey,
    submitKey,
    "Updated topic"
);
```

```java title="updateTopic(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)"
PrivateKey updatedAdminKey = PrivateKey.generateED25519();

topicClient.updateTopic(
    topicId,
    adminKey,
    updatedAdminKey,
    submitKey,
    "Updated topic"
);
```

---

## Update Topic Keys

```java title="updateAdminKey(TopicId topicId, PrivateKey updatedAdminKey)"
PrivateKey newAdminKey = PrivateKey.generateED25519();

topicClient.updateAdminKey(
    topicId,
    newAdminKey
);
```

```java title="updateSubmitKey(TopicId topicId, PrivateKey submitKey)"
PrivateKey newSubmitKey = PrivateKey.generateED25519();

topicClient.updateSubmitKey(
    topicId,
    newSubmitKey
);
```

```java title="updateAdminKey(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey)"
PrivateKey newAdminKey = PrivateKey.generateED25519();

topicClient.updateAdminKey(
    topicId,
    adminKey,
    newAdminKey
);
```

```java title="updateSubmitKey(TopicId topicId, PrivateKey adminKey, PrivateKey submitKey)"
PrivateKey newSubmitKey = PrivateKey.generateED25519();

topicClient.updateSubmitKey(
    topicId,
    adminKey,
    newSubmitKey
);
```

!!! info

    Provide an `adminKey` when the topic was created with a custom admin key that differs from the configured operator account key.

---

## Delete Topic

Delete the topic using topicId.

```java title="deleteTopic(TopicId topicId)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.deleteTopic(topicId);
```

```java title="deleteTopic(TopicId topicId, PrivateKey adminKey)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.deleteTopic(
    topicId,
    adminKey
);
```
!!! info

    Provide an `adminKey` when the topic was created with a custom admin key that differs from the configured operator account key.

---

## Submit Message

Submit message to the topic.

```java title="submitMessage(TopicId topicId, byte[] message)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.submitMessage(
    topicId,
    "Hello Hiero".getBytes()
);
```

```java title="submitMessage(TopicId topicId, String message)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.submitMessage(
    topicId,
    "Hello Hiero"
);
```

```java title="submitMessage(TopicId topicId, PrivateKey submitKey, String message)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

topicClient.submitMessage(
    topicId,
    submitKey,
    "Private message"
);
```