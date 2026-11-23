package org.hiero.base.data;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a network block. */
public record Block(
    long count,
    @Nullable String hapiVersion,
    @NonNull String hash,
    @NonNull String name,
    long number,
    @NonNull String previousHash,
    @Nullable Long size,
    @NonNull TimestampRange timestamp,
    @Nullable Long gasUsed,
    @Nullable String logsBloom) {

  public Block {
    Objects.requireNonNull(hash, "hash must not be null");
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(previousHash, "previousHash must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
