package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
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
  @PutMapping
  public String updateTopic(@RequestBody final TopicUpdateRequest request) {
    try {
      final TopicId topicId = TopicId.fromString(request.topicId());
      final String memo = request.memo() != null ? request.memo() : "";
      
      if (request.adminKey() != null && request.updatedAdminKey() != null && request.submitKey() != null) {
          topicClient.updateTopic(topicId, 
              PrivateKey.fromString(request.adminKey()), 
              PrivateKey.fromString(request.updatedAdminKey()), 
              PrivateKey.fromString(request.submitKey()), 
              memo);
      } else if (request.adminKey() != null) {
          topicClient.updateTopic(topicId, PrivateKey.fromString(request.adminKey()), memo);
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
  @DeleteMapping("/{topicId}")
  public String deleteTopic(@PathVariable("topicId") final String topicId) {
    try {
      topicClient.deleteTopic(topicId);
      return "Topic " + topicId + " deleted successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to delete topic", e);
    }
  }

  /**
   * Submits a message to a topic.
   */
  @PostMapping("/message")
  public String submitMessage(@RequestBody final TopicMessageRequest request) {
    try {
      if (request.submitKey() != null) {
        topicClient.submitMessage(request.topicId(), request.submitKey(), request.message());
      } else {
        topicClient.submitMessage(request.topicId(), request.message());
      }
      return "Message submitted successfully to topic " + request.topicId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to submit message", e);
    }
  }

  /**
   * Retrieves info for a specific topic.
   */
  @GetMapping("/{topicId}/info")
  public Topic getTopicInfo(@PathVariable("topicId") final String topicId) {
    try {
      return topicRepository.findTopicById(topicId)
          .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic info", e);
    }
  }

  /**
   * Retrieves messages for a specific topic.
   */
  @GetMapping("/{topicId}/message")
  public Page<TopicMessage> getTopicMessages(@PathVariable("topicId") final String topicId) {
    try {
      return topicRepository.getMessages(topicId);
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic messages", e);
    }
  }

  /**
   * Retrieves a specific message by its sequence number.
   */
  @GetMapping("/{topicId}/message/{sequenceNumber}")
  public TopicMessage getTopicMessageBySequenceNumber(
      @PathVariable("topicId") final String topicId,
      @PathVariable("sequenceNumber") final long sequenceNumber) {
    try {
      return topicRepository.getMessageBySequenceNumber(topicId, sequenceNumber)
          .orElseThrow(() -> new RuntimeException("Message not found for Topic: " + topicId + " Sequence: " + sequenceNumber));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve topic message", e);
    }
  }
}
