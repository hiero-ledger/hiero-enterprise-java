package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.SubscriptionHandle;
import com.hedera.hashgraph.sdk.TopicId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.HieroException;
import org.hiero.base.TopicClient;
import org.hiero.microprofile.ClientProvider;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class TopicClientTest {

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private TopicClient topicClient;
  @Inject private HieroTestUtils hieroTestUtils;

  @Test
  void testSubscribeTopic() throws Exception {
    final String msg = "Hello Hiero";
    final List<String> messages = new ArrayList<>();
    final TopicId topicId = topicClient.createTopic();
    hieroTestUtils.waitForMirrorNodeRecords();

    final SubscriptionHandle handler =
        topicClient.subscribeTopic(
            topicId,
            (message) -> {
              messages.add(new String(message.contents));
            });

    topicClient.submitMessage(topicId, msg);
    hieroTestUtils.waitForMirrorNodeRecords();
    Thread.sleep(5000); // Make sure to wait after message get recorded in mirrornode

    Assertions.assertNotNull(handler);
    Assertions.assertEquals(1, messages.size());
    Assertions.assertEquals(msg, messages.getFirst());
    handler.unsubscribe();
  }

  @Test
  void testSubscribeTopicWithLimit() throws Exception {
    final String msg = "Hello Hiero";
    final long limit = 1;

    final List<String> messages = new ArrayList<>();
    final TopicId topicId = topicClient.createTopic();
    hieroTestUtils.waitForMirrorNodeRecords();

    final SubscriptionHandle handler =
        topicClient.subscribeTopic(
            topicId,
            (message) -> {
              messages.add(new String(message.contents));
            },
            limit);

    topicClient.submitMessage(topicId, msg);
    hieroTestUtils.waitForMirrorNodeRecords();
    Thread.sleep(5000); // Make sure to wait after message get recorded in mirrornode

    topicClient.submitMessage(topicId, msg);
    hieroTestUtils.waitForMirrorNodeRecords();
    Thread.sleep(5000); // Make sure to wait after message get recorded in mirrornode

    Assertions.assertNotNull(handler);
    Assertions.assertEquals(limit, messages.size());
    handler.unsubscribe();
  }

  @Test
  void testSubscribeTopicWithInvalidLimit() throws HieroException {
    final String msg = "limit must be -1 (infinite) or greater than 0";
    final long limit = -2;

    final TopicId topicId = topicClient.createTopic();
    hieroTestUtils.waitForMirrorNodeRecords();

    final IllegalArgumentException e =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> topicClient.subscribeTopic(topicId, (message) -> {}, limit));

    Assertions.assertEquals(msg, e.getMessage());
  }

  @Test
  void testSubscribeTopicWithStartAndEndTime() throws HieroException {
    final TopicId topicId = topicClient.createTopic();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Instant start = Instant.now().plus(Duration.ofMinutes(10));
    final Instant end = Instant.now().plus(Duration.ofDays(2));
    final SubscriptionHandle handler =
        Assertions.assertDoesNotThrow(
            () -> topicClient.subscribeTopic(topicId, (message) -> {}, start, end));

    Assertions.assertNotNull(handler);
    handler.unsubscribe();
  }

  @Test
  void testSubscribeTopicWithStartAndEndTimeWithInvalidParams() throws HieroException {
    final TopicId topicId = topicClient.createTopic();

    final Instant start = Instant.now().plus(Duration.ofMinutes(10));
    final Instant invalidEnd = start.minus(Duration.ofMinutes(1));

    final IllegalArgumentException e2 =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> topicClient.subscribeTopic(topicId, (message) -> {}, start, invalidEnd));

    Assertions.assertEquals("endTime cannot be before startTime", e2.getMessage());
  }
}
