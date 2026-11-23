package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.CustomFee;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenFeeScheduleUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull List<CustomFee> customFees,
    @NonNull PrivateKey feeScheduleKey)
    implements TransactionRequest {

  public static final int MAX_CUSTOM_FEES = 10;

  public TokenFeeScheduleUpdateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(tokenId, "tokenId is required");
    Objects.requireNonNull(customFees, "customFees is required");
    Objects.requireNonNull(feeScheduleKey, "feeScheduleKey is required");
    if (customFees.size() > MAX_CUSTOM_FEES) {
      throw new IllegalArgumentException(
          "customFees must contain at most " + MAX_CUSTOM_FEES + " entries");
    }
    customFees = List.copyOf(customFees);
  }

  @NonNull
  public static TokenFeeScheduleUpdateRequest of(
      @NonNull TokenId tokenId,
      @NonNull List<CustomFee> customFees,
      @NonNull PrivateKey feeScheduleKey) {
    return new TokenFeeScheduleUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        customFees,
        feeScheduleKey);
  }

  @NonNull
  public static TokenFeeScheduleUpdateRequest of(
      @NonNull String tokenId,
      @NonNull List<CustomFee> customFees,
      @NonNull PrivateKey feeScheduleKey) {
    Objects.requireNonNull(tokenId, "tokenId is required");
    return of(TokenId.fromString(tokenId), customFees, feeScheduleKey);
  }
}
