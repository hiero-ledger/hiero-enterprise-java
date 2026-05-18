package org.hiero.microprofile.test;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.Optional;
import org.hiero.base.data.TopicMessage;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MirrorNodeJsonConverterImplTest {

  private final MirrorNodeJsonConverterImpl converter = new MirrorNodeJsonConverterImpl();

  private JsonObjectBuilder baseTopicMessageBuilder() {
    return Json.createObjectBuilder()
        .add("consensus_timestamp", "1234567890.000000000")
        .add("message", "SGVsbG8=")
        .add("payer_account_id", "0.0.1234")
        .add("running_hash", "abc")
        .add("running_hash_version", 3)
        .add("sequence_number", "1")
        .add("topic_id", "0.0.5678");
  }

  @Test
  void toTopicMessageDoesNotThrowWhenChunkInfoIsJsonNull() {
    final JsonObject json = baseTopicMessageBuilder().add("chunk_info", JsonValue.NULL).build();

    final Optional<TopicMessage> result = converter.toTopicMessage(json);

    Assertions.assertTrue(result.isPresent());
    Assertions.assertNull(result.get().chunkInfo());
  }

  @Test
  void toTopicMessageDoesNotThrowWhenChunkInfoIsMissing() {
    final JsonObject json = baseTopicMessageBuilder().build();

    final Optional<TopicMessage> result = converter.toTopicMessage(json);

    Assertions.assertTrue(result.isPresent());
    Assertions.assertNull(result.get().chunkInfo());
  }

  @Test
  void toTopicMessageParsesChunkInfoWhenPresent() {
    final JsonObject chunk =
        Json.createObjectBuilder()
            .add("initial_transaction_id", "0.0.1234@1234567890.000000000")
            .add("nonce", 0)
            .add("number", 1)
            .add("total", 2)
            .add("scheduled", false)
            .build();
    final JsonObject json = baseTopicMessageBuilder().add("chunk_info", chunk).build();

    final Optional<TopicMessage> result = converter.toTopicMessage(json);

    Assertions.assertTrue(result.isPresent());
    Assertions.assertNotNull(result.get().chunkInfo());
    Assertions.assertEquals(1, result.get().chunkInfo().number());
    Assertions.assertEquals(2, result.get().chunkInfo().total());
  }
}
