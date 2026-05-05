package org.hiero.spring.sample.dto.topic;

/**
 * Request DTO for submitting a message to a Consensus Topic.
 */
public record TopicMessageRequest(
    String topicId,
    String message,
    String submitKey
) {}
