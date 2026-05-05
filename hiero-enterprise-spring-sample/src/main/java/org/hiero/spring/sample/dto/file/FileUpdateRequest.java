package org.hiero.spring.sample.dto.file;

import java.time.Instant;

/**
 * Request DTO for updating a Hiero file.
 * @param content Optional Base64 encoded content to update.
 * @param expirationTime Optional new expiration time in seconds since epoch.
 */
public record FileUpdateRequest(
    String content,
    Long expirationTime
) {
  public byte[] getDecodedContent() {
    return content != null ? java.util.Base64.getDecoder().decode(content) : null;
  }

  public Instant getExpirationInstant() {
    return expirationTime != null ? Instant.ofEpochSecond(expirationTime) : null;
  }
}
