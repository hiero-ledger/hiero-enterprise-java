package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenFreezeRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @NonNull PrivateKey freezeKey)
    implements TransactionRequest {

  public TokenFreezeRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(accountId, "accountId is required");
    Objects.requireNonNull(freezeKey, "freezeKey is required");
  }

  public static TokenFreezeRequest of(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey freezeKey) {
    return new TokenFreezeRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        accountId,
        freezeKey);
  }

  public static TokenFreezeRequest of(
      @NonNull String tokenId, @NonNull String accountId, @NonNull PrivateKey freezeKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(accountId, "accountId is required");
    return of(TokenId.fromString(tokenId), AccountId.fromString(accountId), freezeKey);
  }

  public static TokenFreezeRequest of(
      @NonNull String tokenId, @NonNull AccountId accountId, @NonNull PrivateKey freezeKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    return of(TokenId.fromString(tokenId), accountId, freezeKey);
  }
}
