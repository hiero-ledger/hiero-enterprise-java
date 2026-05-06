package org.hiero.spring.sample.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Request DTO for creating a Hiero file.
 */
@Schema(name = "File: Create Request", description = "Request DTO for creating a new file on the Hiero File Service (HFS).")
public record FileCreateRequest(
    @Schema(description = "Base64 encoded content of the file.", example = "SGVsbG8gSGllcm8h")
    String content,
    @Schema(description = "Optional expiration time in seconds since epoch.", example = "1735689600")
    Long expirationTime
) {
  public byte[] getDecodedContent() {
    return java.util.Base64.getDecoder().decode(content);
  }

  public Instant getExpirationInstant() {
    return expirationTime != null ? Instant.ofEpochSecond(expirationTime) : null;
  }
}
