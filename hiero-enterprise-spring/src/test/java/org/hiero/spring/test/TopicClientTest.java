package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import org.hiero.base.HieroBaseException;
import org.hiero.base.TopicClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class TopicClientTest {
  @Autowired private TopicClient topicClient;

  @Test
  void testCreateTopic() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic();
    Assertions.assertNotNull(topicId);
  }

  @Test
  void testCreatePrivateTopic() throws HieroBaseException {
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createPrivateTopic(submitKey);
    Assertions.assertNotNull(topicId);
  }

  @Test
  void testDeleteTopic() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic();
    Assertions.assertDoesNotThrow(() -> topicClient.deleteTopic(topicId));
  }

  @Test
  void testDeleteTopicThrowExceptionFroInvalidId() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    Assertions.assertThrows(HieroBaseException.class, () -> topicClient.deleteTopic(topicId));
  }

  @Test
  void testDeleteTopicForGivenAdminKey() throws HieroBaseException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createTopic(adminKey);
    Assertions.assertDoesNotThrow(() -> topicClient.deleteTopic(topicId, adminKey));
  }

  @Test
  void testDeleteTopicThrowExceptionForDifferentAdminKey() throws HieroBaseException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createTopic(adminKey);

    // operatorAccount privateKey will use as adminKey by default
    Assertions.assertThrows(HieroBaseException.class, () -> topicClient.deleteTopic(topicId));
  }

  @Test
  void testUpdateTopic() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic("Hello Hiero");
    final String updatedMemo = "Updated Hello Hiero";
    Assertions.assertDoesNotThrow(() -> topicClient.updateTopic(topicId, updatedMemo));
  }

  @Test
  void testUpdateTopicThrowExceptionFroInvalidId() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final String updatedMemo = "Updated Hello Hiero";
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.updateTopic(topicId, updatedMemo));
  }

  @Test
  void testUpdateTopicForGivenAdminKey() throws HieroBaseException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createTopic(adminKey, "Hello Hiero");
    final String updatedMemo = "Updated Hello Hiero";
    Assertions.assertDoesNotThrow(() -> topicClient.updateTopic(topicId, adminKey, updatedMemo));
  }

  @Test
  void testUpdateTopicThrowExceptionForDifferentAdminKey() throws HieroBaseException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createTopic(adminKey, "Hello Hiero");
    final String updatedMemo = "Updated Hello Hiero";

    // operatorAccount privateKey will use as adminKey by default
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.updateTopic(topicId, updatedMemo));
  }

  @Test
  void testUpdateAdminKey() throws HieroBaseException {
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();

    // operatorAccount privateKey will use as adminKey by default
    final TopicId topicId = topicClient.createTopic();
    Assertions.assertDoesNotThrow(() -> topicClient.updateAdminKey(topicId, updatedAdminKey));

    // to verify if adminKey is updated
    Assertions.assertThrows(HieroBaseException.class, () -> topicClient.deleteTopic(topicId));
    Assertions.assertDoesNotThrow(() -> topicClient.deleteTopic(topicId, updatedAdminKey));
  }

  @Test
  void testUpdateAdminKeyThrowExceptionFroInvalidId() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey updatedAdminKey = PrivateKey.generateECDSA();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.updateAdminKey(topicId, updatedAdminKey));
  }

  @Test
  void testUpdateSubmitKey() throws HieroBaseException {
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createPrivateTopic(submitKey);

    final PrivateKey updatedSubmitKey = PrivateKey.generateECDSA();
    Assertions.assertDoesNotThrow(() -> topicClient.updateSubmitKey(topicId, updatedSubmitKey));

    // to verify if submitKey is updated
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.submitMessage(topicId, submitKey, message));
    Assertions.assertDoesNotThrow(
        () -> topicClient.submitMessage(topicId, updatedSubmitKey, message));
  }

  @Test
  void testUpdateSubmitKeyForPublicTopic() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic();

    final PrivateKey updatedSubmitKey = PrivateKey.generateECDSA();
    Assertions.assertDoesNotThrow(() -> topicClient.updateSubmitKey(topicId, updatedSubmitKey));

    // to verify if submitKey is updated
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.submitMessage(topicId, message));
    Assertions.assertDoesNotThrow(
        () -> topicClient.submitMessage(topicId, updatedSubmitKey, message));
  }

  @Test
  void testUpdateSubmitKeyThrowExceptionFroInvalidId() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.updateSubmitKey(topicId, submitKey));
  }

  @Test
  void testSubmitMessage() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic();
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertDoesNotThrow(() -> topicClient.submitMessage(topicId, message));
  }

  @Test
  void testSubmitMessageGreaterThanMaxLength() throws HieroBaseException {
    final TopicId topicId = topicClient.createTopic();
    final byte[] message = new byte[1025];
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> topicClient.submitMessage(topicId, message));
  }

  @Test
  void testSubmitMessageToPrivateTopic() throws HieroBaseException {
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createPrivateTopic(submitKey);
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertDoesNotThrow(() -> topicClient.submitMessage(topicId, submitKey, message));
  }

  @Test
  void testSubmitMessageToPrivateTopicThrowExceptionIfSubmitKeyMissing() throws HieroBaseException {
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createPrivateTopic(submitKey);
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.submitMessage(topicId, message));
  }

  @Test
  void testSubmitMessageToPrivateTopicThrowExceptionForInvalidSubmitKey() throws HieroBaseException {
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createPrivateTopic(submitKey);
    final byte[] message = "Hello Hiero".getBytes();
    Assertions.assertThrows(
        HieroBaseException.class,
        () -> topicClient.submitMessage(topicId, PrivateKey.generateECDSA(), message));
  }

  @Test
  void testSubmitMessageThrowExceptionFroInvalidId() {
    final TopicId topicId = TopicId.fromString("1.2.3");
    final byte[] message = "Hello Hiero".getBytes();
    final PrivateKey submitKey = PrivateKey.generateECDSA();
    Assertions.assertThrows(
        HieroBaseException.class, () -> topicClient.submitMessage(topicId, submitKey, message));
  }
}
