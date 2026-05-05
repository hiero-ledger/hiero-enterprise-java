package org.hiero.spring.sample.dto.file;

import java.time.Instant;

/**
 * Request DTO for creating a Hiero file.
 * @param content Base64 encoded content of the file.
 * @param expirationTime Optional expiration time in seconds since epoch.
 */
public record FileCreateRequest(
    String content,
    Long expirationTime
) {
  public byte[] getDecodedContent() {
    return java.util.Base64.getDecoder().decode(content);
  }

  public Instant getExpirationInstant() {
    return expirationTime != null ? Instant.ofEpochSecond(expirationTime) : null;
  }
}
