package org.hiero.spring.sample.dto.topic;

/**
 * Request DTO for creating a new Consensus Topic.
 */
public record TopicCreateRequest(
    String memo,
    String adminKey,
    String submitKey
) {}
