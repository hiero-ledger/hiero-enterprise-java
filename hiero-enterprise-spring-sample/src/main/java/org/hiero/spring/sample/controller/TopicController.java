package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.TopicClient;
import org.hiero.base.data.Page;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.spring.sample.dto.topic.TopicCreateRequest;
import org.hiero.spring.sample.dto.topic.TopicMessageRequest;
import org.hiero.spring.sample.dto.topic.TopicUpdateRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero Consensus Service (Topic) operations.
 */
@Tag(name = "Consensus Topics", description = "Operations related to Hiero Consensus Service (HCS)")
@RestController
@RequestMapping("/api/v1/hiero/topics")
@CrossOrigin(origins = "*")
public class TopicController {

  private final TopicClient topicClient;
  private final TopicRepository topicRepository;

  public TopicController(final TopicClient topicClient, final TopicRepository topicRepository) {
    this.topicClient = Objects.requireNonNull(topicClient, "topicClient must not be null");
    this.topicRepository = Objects.requireNonNull(topicRepository, "topicRepository must not be null");
  }

  /**
   * Creates a new topic.
   */
  @Operation(summary = "Create a new topic", description = "Creates a new public or private HCS topic.")
  @PostMapping
  public String createTopic(@RequestBody final TopicCreateRequest request) {
    try {
      final TopicId topicId;
      final PrivateKey adminKey = request.adminKey() != null ? PrivateKey.fromString(request.adminKey()) : null;
      final PrivateKey submitKey = request.submitKey() != null ? PrivateKey.fromString(request.submitKey()) : null;
      final String memo = request.memo() != null ? request.memo() : "";

      if (submitKey != null) {
        if (adminKey != null) {
          topicId = topicClient.createPrivateTopic(adminKey, submitKey, memo);
        } else {
          topicId = topicClient.createPrivateTopic(submitKey, memo);
        }
      } else {
        if (adminKey != null) {
          topicId = topicClient.createTopic(adminKey, memo);
        } else {
          topicId = topicClient.createTopic(memo);
        }
      }
      return "Topic " + topicId.toString() + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create topic", e);
    }
  }

  /**
   * Updates an existing topic.
   */
  @Operation(summary = "Update a topic", description = "Updates an existing HCS topic's memo or keys.")
  @PutMapping
  public String updateTopic(@RequestBody final TopicUpdateRequest request) {
    try {
      if (request.topicId() == null) {
        throw new IllegalArgumentException("Missing required field: topicId is mandatory.");
      }
      final TopicId topicId = TopicId.fromString(request.topicId().trim());
      final String memo = request.memo() != null ? request.memo() : "";
      
      if (request.adminKey() != null && request.updatedAdminKey() != null && request.submitKey() != null) {
          topicClient.updateTopic(topicId, 
              PrivateKey.fromString(request.adminKey().trim()), 
              PrivateKey.fromString(request.updatedAdminKey().trim()), 
              PrivateKey.fromString(request.submitKey().trim()), 
              memo);
      } else if (request.adminKey() != null) {
          topicClient.updateTopic(topicId, PrivateKey.fromString(request.adminKey().trim()), memo);
      } else {
          topicClient.updateTopic(topicId, memo);
      }
      return "Topic " + topicId.toString() + " updated successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to update topic", e);
    }
  }

  /**
   * Deletes a topic.
   */
  @Operation(summary = "Delete a topic", description = "Deletes an existing HCS topic.")
  @DeleteMapping("/{topicId}")
  public String deleteTopic(@PathVariable("topicId") final String topicId) {
    try {
      topicClient.deleteTopic(topicId.trim());
      return "Topic " + topicId + " deleted successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to delete topic", e);
    }
  }

  /**
   * Submits a message to a topic.
   */
  @Operation(summary = "Submit a message", description = "Submits a message to a specific HCS topic.")
  @PostMapping("/message")
  public String submitMessage(@RequestBody final TopicMessageRequest request) {
    try {
      if (request.topicId() == null || request.message() == null) {
        throw new IllegalArgumentException("Missing required fields: topicId and message are mandatory.");
      }
      final String trimmedTopicId = request.topicId().trim();
      if (request.submitKey() != null) {
        topicClient.submitMessage(trimmedTopicId, request.submitKey().trim(), request.message());
      } else {
        topicClient.submitMessage(trimmedTopicId, request.message());
      }
      return "Message submitted successfully to topic " + request.topicId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to submit message", e);
    }
  }

  /**
   * Retrieves info for a specific topic.
   */
  @Operation(summary = "Get topic information", description = "Retrieves detailed information about an HCS topic from the mirror node.")
  @GetMapping("/{topicId}/info")
  public Topic getTopicInfo(@PathVariable("topicId") final String topicId) {
    try {
      final String trimmedTopicId = topicId.trim();
      return topicRepository.findTopicById(trimmedTopicId)
          .orElseThrow(() -> new RuntimeException("Topic not found: " + trimmedTopicId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic info", e);
    }
  }

  /**
   * Retrieves messages for a specific topic.
   */
  @Operation(summary = "Get topic messages", description = "Retrieves a list of messages submitted to a specific HCS topic.")
  @GetMapping("/{topicId}/message")
  public Page<TopicMessage> getTopicMessages(@PathVariable("topicId") final String topicId) {
    try {
      return topicRepository.getMessages(topicId.trim());
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic messages", e);
    }
  }

  /**
   * Retrieves a specific message by its sequence number.
   */
  @Operation(summary = "Get topic message by sequence", description = "Retrieves a specific message from an HCS topic by its sequence number.")
  @GetMapping("/{topicId}/message/{sequenceNumber}")
  public TopicMessage getTopicMessageBySequenceNumber(
      @PathVariable("topicId") final String topicId,
      @PathVariable("sequenceNumber") final long sequenceNumber) {
    try {
      final String trimmedTopicId = topicId.trim();
      return topicRepository.getMessageBySequenceNumber(trimmedTopicId, sequenceNumber)
          .orElseThrow(() -> new RuntimeException("Message not found for Topic: " + trimmedTopicId + " Sequence: " + sequenceNumber));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic message", e);
    }
  }
}
