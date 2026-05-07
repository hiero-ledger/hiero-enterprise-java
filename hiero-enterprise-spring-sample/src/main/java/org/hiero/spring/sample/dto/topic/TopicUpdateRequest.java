package org.hiero.spring.sample.dto.topic;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request DTO for updating an existing Consensus Topic. */
@Schema(
    name = "Consensus Topic: Update Request",
    description = "Request DTO for updating an existing Hiero Consensus Service (HCS) topic.")
public record TopicUpdateRequest(
    @Schema(
            description = "The ID of the topic to update.",
            example = "0.0.1234",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String topicId,
    @Schema(
            description = "The new memo for the topic.",
            example = "Updated topic memo",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String memo,
    @Schema(
            description = "The current admin key (required for most updates).",
            example = "302e020100300506032b657004220420...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String adminKey,
    @Schema(
            description = "The new admin key to set (optional).",
            example = "302e020100300506032b657004220420...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String updatedAdminKey,
    @Schema(
            description = "The new submit key to set (optional).",
            example = "302e020100300506032b657004220420...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String submitKey) {}
