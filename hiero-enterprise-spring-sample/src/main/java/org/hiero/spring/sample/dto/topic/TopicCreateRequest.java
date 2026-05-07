package org.hiero.spring.sample.dto.topic;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request DTO for creating a new Consensus Topic. */
@Schema(
    name = "Consensus Topic: Create Request",
    description = "Request DTO for creating a new Hiero Consensus Service (HCS) topic.")
public record TopicCreateRequest(
    @Schema(description = "Optional memo for the topic.", example = "Project alerts topic")
        String memo,
    @Schema(
            description = "Optional admin key for the topic.",
            example = "302e020100300506032b657004220420...")
        String adminKey,
    @Schema(
            description = "Optional submit key for the topic.",
            example = "302e020100300506032b657004220420...")
        String submitKey) {}
