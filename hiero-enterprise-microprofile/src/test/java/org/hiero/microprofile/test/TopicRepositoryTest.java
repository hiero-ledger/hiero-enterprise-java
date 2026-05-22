package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.PrivateKey;
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
import org.hiero.base.data.Topic;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.microprofile.ClientProvider;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class TopicRepositoryTest {
  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private HieroTestUtils hieroTestUtils;

  @Inject private TopicRepository topicRepository;

  @Inject private TopicClient topicClient;

  @Test
  @Disabled("Temporary disabled work on testnet not solo")
  void testFindTopicById() throws HieroException {
    final TopicId topicId = topicClient.createTopic();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<Topic> result = topicRepository.findTopicById(topicId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void testFindTopicByIdReturnsEmptyOptional() throws HieroException {
    final TopicId topicId = TopicId.fromString("0.0.0");
    final Optional<Topic> result = topicRepository.findTopicById(topicId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testShouldParseEcdsaKeys() throws Exception {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final TopicId topicId = topicClient.createTopic(adminKey);
    Assertions.assertNotNull(topicId);

    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<Topic> topic = topicRepository.findTopicById(topicId);
    Assertions.assertTrue(topic.isPresent());
    Assertions.assertEquals(topic.get().adminKey().toString(), adminKey.getPublicKey().toString());
  }

  @Test
  void testShouldParseEd25519Keys() throws Exception {
    final PrivateKey adminKey = PrivateKey.generateED25519();
    final TopicId topicId = topicClient.createTopic(adminKey);
    Assertions.assertNotNull(topicId);

    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<Topic> topic = topicRepository.findTopicById(topicId);
    Assertions.assertTrue(topic.isPresent());
    Assertions.assertEquals(topic.get().adminKey().toString(), adminKey.getPublicKey().toString());
  }
}
