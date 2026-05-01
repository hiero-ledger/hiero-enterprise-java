package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.TopicId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.TopicRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final TopicRepository topicRepository;

  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final TopicRepository topicRepository) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
    this.topicRepository =
        Objects.requireNonNull(topicRepository, "topicRepository must not be null");
  }

  @GetMapping("/")
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account " + account.accountId() + " created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GetMapping("/blocks")
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GetMapping("/topic-messages")
  public Page<TopicMessage> getTopicMessages(@RequestParam String topicId) {
    try {
      return topicRepository.getMessages(TopicId.fromString(topicId));
    } catch (final Exception e) {
      throw new RuntimeException("Error querying topic messages", e);
    }
  }

  @GetMapping("/topic-message")
  public TopicMessageResponse getTopicMessageByConsensusTimestamp(
      @RequestParam String consensusTimestamp) {
    try {
      Optional<TopicMessage> message =
          topicRepository.getMessageByConsensusTimestamp(consensusTimestamp);
      if (message.isPresent()) {
        TopicMessage tm = message.get();
        return new TopicMessageResponse(
            true,
            tm.topicId().toString(),
            tm.consensusTimestamp().toString(),
            tm.sequenceNumber(),
            tm.message(),
            null);
      } else {
        return new TopicMessageResponse(
            false,
            null,
            null,
            0,
            null,
            "No message found for consensus timestamp: " + consensusTimestamp);
      }
    } catch (final Exception e) {
      return new TopicMessageResponse(
          false, null, null, 0, null, "Error: " + e.getClass().getName() + " - " + e.getMessage());
    }
  }

  public record TopicMessageResponse(
      boolean found,
      String topicId,
      String consensusTimestamp,
      long sequenceNumber,
      String message,
      String error) {}
}
