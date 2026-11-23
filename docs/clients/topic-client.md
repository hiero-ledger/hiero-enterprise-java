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
| `createImmutableTopic()` | Creates a topic without an admin key. It can never be updated or deleted. |
| `createImmutableTopic(String memo)` | Creates an immutable topic with the provided memo. |
| `createTopic(PrivateKey adminKey, PrivateKey submitKey, String memo, Account autoRenewAccount, Duration autoRenewPeriod)` | Creates a topic with full control over keys and auto renew settings. Every argument is optional. |
| `updateTopic(TopicId topicId, String memo)` | Updates a topic memo using the operator account as admin key. |
| `updateTopic(TopicId topicId, PrivateKey adminKey, String memo)` | Updates a topic using a provided admin key and memo. |
| `updateTopic(TopicId topicId, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)` | Updates topic admin key, submit key, and memo using operator account. |
| `updateTopic(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey, PrivateKey submitKey, String memo)` | Updates topic admin key, submit key, and memo using the provided admin key. |
| `updateAdminKey(TopicId topicId, PrivateKey updatedAdminKey)` | Updates the topic admin key using the operator account. |
| `updateAdminKey(TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey)` | Updates the topic admin key using the provided admin key. |
| `updateSubmitKey(TopicId topicId, PrivateKey submitKey)` | Updates the topic submit key using the operator account. |
| `updateSubmitKey(TopicId topicId, PrivateKey adminKey, PrivateKey submitKey)` | Updates the topic submit key using the provided admin key. |
| `updateAutoRenewAccount(TopicId topicId, Account autoRenewAccount)` | Sets a new auto renew account using the operator account as admin key. |
| `updateAutoRenewAccount(TopicId topicId, PrivateKey adminKey, Account autoRenewAccount)` | Sets a new auto renew account using the provided admin key. |
| `clearAutoRenewAccount(TopicId topicId)` | Removes the auto renew account using the operator account as admin key. |
| `clearAutoRenewAccount(TopicId topicId, PrivateKey adminKey)` | Removes the auto renew account using the provided admin key. |
| `updateAutoRenewPeriod(TopicId topicId, Duration autoRenewPeriod)` | Updates the auto renew period using the operator account as admin key. |
| `updateAutoRenewPeriod(TopicId topicId, PrivateKey adminKey, Duration autoRenewPeriod)` | Updates the auto renew period using the provided admin key. |
| `updateExpirationTime(TopicId topicId, Instant expirationTime)` | Extends the topic expiration time. No admin key is required. |
| `clearAdminKey(TopicId topicId)` | Removes the admin key, making the topic immutable, using the operator account as admin key. |
| `clearAdminKey(TopicId topicId, PrivateKey adminKey)` | Removes the admin key, making the topic immutable, using the provided admin key. |
| `clearSubmitKey(TopicId topicId)` | Removes the submit key, making the topic public, using the operator account as admin key. |
| `clearSubmitKey(TopicId topicId, PrivateKey adminKey)` | Removes the submit key, making the topic public, using the provided admin key. |
| `clearTopicMemo(TopicId topicId)` | Removes the topic memo using the operator account as admin key. |
| `clearTopicMemo(TopicId topicId, PrivateKey adminKey)` | Removes the topic memo using the provided admin key. |
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

## Create Immutable Topic

A topic created without an admin key is immutable: it can never be updated or deleted. Only its expiration time can be extended.

```java title="createImmutableTopic(String memo)"
TopicId topicId =
    topicClient.createImmutableTopic("Immutable topic");
```

---

## Create Topic with Auto Renew Settings

The auto renew account pays to extend the topic when it expires and must sign the create transaction, which is why an `Account` (ID and private key) is passed. If no auto renew account is given, the operator account is used. The auto renew period must be between roughly 30 and 92 days.

```java title="createTopic(PrivateKey adminKey, PrivateKey submitKey, String memo, Account autoRenewAccount, Duration autoRenewPeriod)"
Account autoRenewAccount = accountClient.createAccount(Hbar.from(1));

TopicId topicId =
    topicClient.createTopic(
        adminKey,
        null,              // no submit key: public topic
        "My topic",
        autoRenewAccount,
        Duration.ofDays(60)
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

## Update Auto Renew Settings

```java title="updateAutoRenewAccount(TopicId topicId, Account autoRenewAccount)"
Account autoRenewAccount = accountClient.createAccount(Hbar.from(1));

topicClient.updateAutoRenewAccount(topicId, autoRenewAccount);
```

```java title="clearAutoRenewAccount(TopicId topicId)"
topicClient.clearAutoRenewAccount(topicId);
```

```java title="updateAutoRenewPeriod(TopicId topicId, Duration autoRenewPeriod)"
topicClient.updateAutoRenewPeriod(topicId, Duration.ofDays(60));
```

---

## Extend Expiration Time

Extending the expiration time does not require the admin key, so it also works for immutable topics. The new expiration time must be later than the current one and at most roughly 92 days in the future.

```java title="updateExpirationTime(TopicId topicId, Instant expirationTime)"
topicClient.updateExpirationTime(
    topicId,
    Instant.now().plus(Duration.ofDays(91))
);
```

---

## Clear Topic Properties

```java title="clearAdminKey(TopicId topicId)"
// The topic becomes immutable and can no longer be updated or deleted
topicClient.clearAdminKey(topicId);
```

```java title="clearSubmitKey(TopicId topicId)"
// The topic becomes public: anyone can submit messages
topicClient.clearSubmitKey(topicId);
```

```java title="clearTopicMemo(TopicId topicId)"
topicClient.clearTopicMemo(topicId);
```

!!! warning

    Clearing the admin key cannot be undone. It also removes the auto renew account, because the network does not allow a topic without an admin key to have one.

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