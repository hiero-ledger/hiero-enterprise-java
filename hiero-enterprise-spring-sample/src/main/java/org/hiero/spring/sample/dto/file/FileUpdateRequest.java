package org.hiero.spring.sample.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** Request DTO for updating a Hiero file. */
@Schema(
    name = "File: Update Request",
    description = "Request DTO for updating the content or expiration of a Hiero file.")
public record FileUpdateRequest(
    @Schema(
            description = "Optional Base64 encoded content to update.",
            example = "VXBkYXRlZCBjb250ZW50")
        String content,
    @Schema(
            description = "Optional new expiration time in seconds since epoch.",
            example = "1767225600")
        Long expirationTime) {
  public byte[] getDecodedContent() {
    return content != null ? java.util.Base64.getDecoder().decode(content) : null;
  }

  public Instant getExpirationInstant() {
    return expirationTime != null ? Instant.ofEpochSecond(expirationTime) : null;
  }
}
