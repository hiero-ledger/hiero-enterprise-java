package org.hiero.spring.sample.dto.topic;

/**
 * Request DTO for updating an existing Consensus Topic.
 */
public record TopicUpdateRequest(
    String topicId,
    String memo,
    String adminKey,
    String updatedAdminKey,
    String submitKey
) {}
