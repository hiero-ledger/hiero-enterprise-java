package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenUpdateNftsRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull List<Long> serials,
    byte @NonNull [] metadata,
    @NonNull PrivateKey metadataKey)
    implements TransactionRequest {

  static final int MAX_SERIALS = 10;

  public TokenUpdateNftsRequest {
    Objects.requireNonNull(maxTransactionFee, "Max transaction fee cannot be null");
    Objects.requireNonNull(transactionValidDuration, "Transaction valid duration cannot be null");
    Objects.requireNonNull(tokenId, "Token ID cannot be null");
    Objects.requireNonNull(serials, "Serials cannot be null");
    Objects.requireNonNull(metadata, "Metadata cannot be null");
    Objects.requireNonNull(metadataKey, "Metadata key cannot be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("Serials list cannot be empty");
    }
    if (serials.size() > MAX_SERIALS) {
      throw new IllegalArgumentException(
          "serials must not contain more than " + MAX_SERIALS + " entries");
    }
    for (final Long serial : serials) {
      Objects.requireNonNull(serial, "Serials cannot contain null values");
      if (serial <= 0) {
        throw new IllegalArgumentException("Serials must be positive integers");
      }
    }
    if (metadata.length > TokenMintRequest.MAX_METADATA_SIZE) {
      throw new IllegalArgumentException(
          "Metadata size must be less than or equal to "
              + TokenMintRequest.MAX_METADATA_SIZE
              + " bytes");
    }
  }

  @NonNull
  public static TokenUpdateNftsRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serials,
      final byte @NonNull [] metadata,
      @NonNull final PrivateKey metadataKey) {
    return new TokenUpdateNftsRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        List.copyOf(serials),
        metadata,
        metadataKey);
  }

  @NonNull
  public static TokenUpdateNftsRequest of(
      @NonNull final TokenId tokenId,
      final long serial,
      final byte @NonNull [] metadata,
      @NonNull final PrivateKey metadataKey) {
    return of(tokenId, List.of(serial), metadata, metadataKey);
  }

  @NonNull
  public static TokenUpdateNftsRequest of(
      @NonNull final String tokenId,
      @NonNull final List<Long> serials,
      final byte @NonNull [] metadata,
      @NonNull final PrivateKey metadataKey) {
    Objects.requireNonNull(tokenId, "Token ID cannot be null");
    return of(TokenId.fromString(tokenId), serials, metadata, metadataKey);
  }
}
