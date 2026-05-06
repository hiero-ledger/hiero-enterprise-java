package org.hiero.spring.sample.dto.topic;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for submitting a message to a Consensus Topic.
 */
@Schema(name = "Consensus Topic: Message Request", description = "Request DTO for submitting a message to a Hiero Consensus Service (HCS) topic.")
public record TopicMessageRequest(
    @Schema(description = "The ID of the topic to submit the message to.", example = "0.0.1234", requiredMode = Schema.RequiredMode.REQUIRED)
    String topicId,
    @Schema(description = "The message content to submit.", example = "Hello Hiero Consensus Service!", requiredMode = Schema.RequiredMode.REQUIRED)
    String message,
    @Schema(description = "The submit key (required if the topic is private).", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String submitKey
) {}
