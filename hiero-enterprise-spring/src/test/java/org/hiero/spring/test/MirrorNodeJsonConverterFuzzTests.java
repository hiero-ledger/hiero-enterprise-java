package org.hiero.spring.test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Method;
import java.time.Instant;
import org.hiero.spring.implementation.JsonParseException;
import org.junit.jupiter.api.Assertions;

public class MirrorNodeJsonConverterFuzzTests {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Class<?> CONVERTER_CLASS =
      org.hiero.spring.implementation.MirrorNodeJsonConverterImpl.class;
  private static final Method PARSE_INSTANT_METHOD;
  private static final Method PARSE_KEY_METHOD;

  static {
    try {
      PARSE_INSTANT_METHOD = CONVERTER_CLASS.getDeclaredMethod("parseInstant", String.class);
      PARSE_INSTANT_METHOD.setAccessible(true);
      PARSE_KEY_METHOD = CONVERTER_CLASS.getDeclaredMethod("parseKey", JsonNode.class);
      PARSE_KEY_METHOD.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final org.hiero.spring.implementation.MirrorNodeJsonConverterImpl converter =
      new org.hiero.spring.implementation.MirrorNodeJsonConverterImpl();

  @FuzzTest
  public void fuzzParseInstant(FuzzedDataProvider data) {
    String input = data.consumeString(128);
    try {
      invokeParseInstant(input);
    } catch (IllegalArgumentException | JsonParseException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during parseInstant", e);
    }
  }

  @FuzzTest
  public void fuzzParseKey(FuzzedDataProvider data) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("_type", data.consumeString(32));
    node.put("key", data.consumeString(256));
    try {
      invokeParseKey(node);
    } catch (IllegalArgumentException | JsonParseException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during parseKey", e);
    }
  }

  @FuzzTest
  public void fuzzToTopicMessage(FuzzedDataProvider data) {
    JsonNode input = parseRandomJson(data);
    try {
      converter.toTopicMessage(input);
    } catch (IllegalArgumentException | JsonParseException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during toTopicMessage", e);
    }
  }

  @FuzzTest
  public void fuzzToBlock(FuzzedDataProvider data) {
    JsonNode input = parseRandomJson(data);
    try {
      converter.toBlock(input);
    } catch (IllegalArgumentException | JsonParseException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during toBlock", e);
    }
  }

  @FuzzTest
  public void fuzzToContract(FuzzedDataProvider data) {
    JsonNode input = parseRandomJson(data);
    try {
      converter.toContract(input);
    } catch (IllegalArgumentException | JsonParseException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during toContract", e);
    }
  }

  private static Instant invokeParseInstant(String value) throws Exception {
    return (Instant) PARSE_INSTANT_METHOD.invoke(null, value);
  }

  private static Object invokeParseKey(JsonNode node) throws Exception {
    return PARSE_KEY_METHOD.invoke(null, node);
  }

  private JsonNode parseRandomJson(FuzzedDataProvider data) {
    try {
      return OBJECT_MAPPER.readTree(data.consumeRemainingAsString());
    } catch (Exception e) {
      return JsonNodeFactory.instance.objectNode();
    }
  }
}
