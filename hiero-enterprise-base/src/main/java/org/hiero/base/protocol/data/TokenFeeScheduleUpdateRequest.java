package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.CustomFee;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to update the custom fees for a token.
 *
 * @param tokenId The ID of the token.
 * @param customFees The list of custom fees.
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenFeeScheduleUpdateRequest(
    @NonNull TokenId tokenId,
    @NonNull List<CustomFee> customFees,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenFeeScheduleUpdateRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(customFees, "customFees must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
  }

  public static TokenFeeScheduleUpdateRequest of(
      @NonNull final TokenId tokenId, @NonNull final List<CustomFee> customFees) {
    return new TokenFeeScheduleUpdateRequest(
        tokenId, customFees, Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
