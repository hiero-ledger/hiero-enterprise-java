package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record TokenUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull PrivateKey adminKey,
    @Nullable String name,
    @Nullable String symbol)
    implements TransactionRequest {

  public TokenUpdateRequest {

    Objects.requireNonNull(maxTransactionFee, "Max transaction fee cannot be null");
    Objects.requireNonNull(transactionValidDuration, "Transaction valid duration cannot be null");
    Objects.requireNonNull(tokenId, "Token ID cannot be null");
    Objects.requireNonNull(adminKey, "Admin key cannot be null");

    if (name == null && symbol == null) {
      throw new IllegalArgumentException("At least one of name or symbol must be provided");
    }

    if (symbol != null && symbol.length() > TokenCreateRequest.MAX_SYMBOL_LENGTH) {
      throw new IllegalArgumentException(
          "Symbol length must be less than or equal to " + TokenCreateRequest.MAX_SYMBOL_LENGTH);
    }
  }

  @NonNull
  public static TokenUpdateRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final PrivateKey adminKey,
      @Nullable final String name,
      @Nullable final String symbol) {
    return new TokenUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        adminKey,
        name,
        symbol);
  }

  @NonNull
  public static TokenUpdateRequest of(
      @NonNull String tokenId,
      @NonNull PrivateKey adminKey,
      @Nullable String name,
      @Nullable String symbol) {
    Objects.requireNonNull(tokenId, "Token ID cannot be null");
    return of(TokenId.fromString(tokenId), adminKey, name, symbol);
  }
}
