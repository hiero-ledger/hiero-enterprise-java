package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.TopicId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.HieroException;
import org.hiero.base.TopicClient;
import org.hiero.base.data.Page;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class TopicRepositoryTest {

  @Inject private TopicRepository topicRepository;

  @Inject private TopicClient topicClient;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Test
  void testNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> topicRepository.findTopicById((TopicId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicRepository.getMessages((TopicId) null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> topicRepository.getMessageBySequenceNumber((TopicId) null, 1));
    Assertions.assertThrows(
        NullPointerException.class, () -> topicRepository.getMessageByConsensusTimestamp(null));
  }

  @Test
  void testFindTopicByIdReturnsEmptyOptional() throws HieroException {
    // given
    final TopicId topicId = TopicId.fromString("0.0.0");

    // when
    final Optional<Topic> result = topicRepository.findTopicById(topicId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testGetMessagesByTopicId() throws Exception {
    // given
    final TopicId topicId = topicClient.createTopic();
    topicClient.submitMessage(topicId, "Hello Hiero");
    // TODO: fix sleep
    Thread.sleep(10_000);

    // when
    final Page<TopicMessage> result = topicRepository.getMessages(topicId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getData().size());
  }

  @Test
  void testGetMessagesByTopicIdReturnsEmptyList() throws HieroException {
    // given
    final TopicId topicId = TopicId.fromString("1.2.3");

    // when
    final Page<TopicMessage> result = topicRepository.getMessages(topicId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.getData().isEmpty());
  }

  @Test
  void testGetMessagesByTopicIdAndSequenceNumber() throws Exception {
    // given
    final TopicId topicId = topicClient.createTopic();
    topicClient.submitMessage(topicId, "Hello Hiero 1");
    topicClient.submitMessage(topicId, "Hello Hiero 2");
    // TODO: fix sleep
    Thread.sleep(10_000);

    // when
    final Optional<TopicMessage> result = topicRepository.getMessageBySequenceNumber(topicId, 1);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals("Hello Hiero 1", result.get().message());
  }

  @Test
  void testGetMessagesByTopicIdAndSequenceNumberReturnEmptyOptional() throws Exception {
    // given
    final TopicId topicId = topicClient.createTopic();
    topicClient.submitMessage(topicId, "Hello Hiero 1");
    // TODO: fix sleep
    Thread.sleep(10_000);

    // when
    final Optional<TopicMessage> result = topicRepository.getMessageBySequenceNumber(topicId, 2);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testGetMessagesByTopicIdAndSequenceNumberThrowsException() throws HieroException {
    // given
    final TopicId topicId = TopicId.fromString("0.0.0");

    // then
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> topicRepository.getMessageBySequenceNumber(topicId, 0));
  }

  @Test
  void testGetMessageByConsensusTimestamp() throws Exception {
    // given
    final TopicId topicId = topicClient.createTopic();
    topicClient.submitMessage(topicId, "Hello Timestamp");
    // TODO: fix sleep
    Thread.sleep(10_000);

    final Optional<TopicMessage> bySeq = topicRepository.getMessageBySequenceNumber(topicId, 1);
    Assertions.assertTrue(bySeq.isPresent());

    final TopicMessage original = bySeq.get();
    final String timestamp =
        original.consensusTimestamp().getEpochSecond()
            + "."
            + String.format("%09d", original.consensusTimestamp().getNano());

    // when
    final Optional<TopicMessage> result = topicRepository.getMessageByConsensusTimestamp(timestamp);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(original.message(), result.get().message());
    Assertions.assertEquals(original.sequenceNumber(), result.get().sequenceNumber());
    Assertions.assertEquals(original.topicId(), result.get().topicId());
  }

  @Test
  void testGetMessageByConsensusTimestampReturnsEmptyForUnknownTimestamp() throws HieroException {
    // when
    final Optional<TopicMessage> result =
        topicRepository.getMessageByConsensusTimestamp("1.000000000");

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
