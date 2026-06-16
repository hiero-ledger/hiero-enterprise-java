# Topic Repository

TopicRepository provides APIs for querying Hiero topic information from a Mirror Node, including retrieving topic details, querying topic messages, and searching messages by sequence number.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findTopicById(TopicId topicId)` | Retrieves topic information using a `TopicId` object. |
| `findTopicById(String topicId)` | Retrieves topic information using a topic ID string. |
| `getMessages(TopicId topicId)` | Retrieves messages published to a specific topic. |
| `getMessages(String topicId)` | Retrieves topic messages using a topic ID string. |
| `getMessageBySequenceNumber(TopicId topicId, long sequenceNumber)` | Retrieves a topic message using its sequence number. |
| `getMessageBySequenceNumber(String topicId, long sequenceNumber)` | Retrieves a topic message using topic ID string and sequence number. |

---

## Find Topic By ID

```java title="findTopicById(TopicId topicId)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

Optional<Topic> topic =
    topicRepository.findTopicById(topicId);

topic.ifPresent(System.out::println);
```


---

## Get Topic Messages

```java title="getMessages(TopicId topicId)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

Page<TopicMessage> messages =
    topicRepository.getMessages(topicId);

System.out.println(messages);
```

---

## Get Message By Sequence Number

```java title="getMessageBySequenceNumber(TopicId topicId, long sequenceNumber)"
TopicId topicId =
    TopicId.fromString("0.0.1234");

Optional<TopicMessage> message =
    topicRepository.getMessageBySequenceNumber(
        topicId,
        1
    );

message.ifPresent(System.out::println);
```


