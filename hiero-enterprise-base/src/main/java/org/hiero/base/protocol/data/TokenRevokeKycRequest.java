package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenRevokeKycRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @NonNull PrivateKey kycKey)
    implements TransactionRequest {

  public TokenRevokeKycRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(accountId, "accountId is required");
    Objects.requireNonNull(kycKey, "kycKey is required");
  }

  @NonNull
  public static TokenRevokeKycRequest of(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey kycKey) {
    return new TokenRevokeKycRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        accountId,
        kycKey);
  }

  @NonNull
  public static TokenRevokeKycRequest of(
      @NonNull String tokenId, @NonNull String accountId, @NonNull PrivateKey kycKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(accountId, "accountId is required");
    return of(TokenId.fromString(tokenId), AccountId.fromString(accountId), kycKey);
  }

  @NonNull
  public static TokenRevokeKycRequest of(
      @NonNull String tokenId, @NonNull AccountId accountId, @NonNull PrivateKey kycKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    return of(TokenId.fromString(tokenId), accountId, kycKey);
  }
}
