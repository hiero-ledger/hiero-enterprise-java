package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenDeleteRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull PrivateKey adminKey)
    implements TransactionRequest {

  public TokenDeleteRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(adminKey, "adminKey is required");
  }

  @NonNull
  public static TokenDeleteRequest of(@NonNull TokenId tokenId, @NonNull PrivateKey adminKey) {
    return new TokenDeleteRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        adminKey);
  }

  @NonNull
  public static TokenDeleteRequest of(@NonNull String tokenId, @NonNull PrivateKey adminKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    return of(TokenId.fromString(tokenId), adminKey);
  }
}
