package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.TopicId;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.TopicRepository;

@Path("/")
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final TopicRepository topicRepository;

  @Inject
  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final TopicRepository topicRepository) {
    this.client = client;
    this.blockRepository = blockRepository;
    this.topicRepository = topicRepository;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GET
  @Path("/blocks")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GET
  @Path("/topic-messages")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<TopicMessage> getTopicMessages(@QueryParam("topicId") final String topicId) {
    try {
      return topicRepository.getMessages(TopicId.fromString(topicId));
    } catch (final Exception e) {
      throw new RuntimeException("Error querying topic messages", e);
    }
  }

  @GET
  @Path("/topic-message")
  @Produces(MediaType.APPLICATION_JSON)
  public TopicMessageResponse getTopicMessageByConsensusTimestamp(
      @QueryParam("consensusTimestamp") final String consensusTimestamp) {
    try {
      final Optional<TopicMessage> message =
          topicRepository.getMessageByConsensusTimestamp(consensusTimestamp);
      if (message.isPresent()) {
        final TopicMessage topicMessage = message.get();
        return new TopicMessageResponse(
            true,
            topicMessage.topicId().toString(),
            topicMessage.consensusTimestamp().toString(),
            topicMessage.sequenceNumber(),
            topicMessage.message(),
            null);
      }
      return new TopicMessageResponse(
          false,
          null,
          null,
          0,
          null,
          "No message found for consensus timestamp: " + consensusTimestamp);
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
