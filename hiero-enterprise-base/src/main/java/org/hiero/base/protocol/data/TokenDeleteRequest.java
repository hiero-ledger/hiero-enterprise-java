package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to delete a token.
 *
 * @param tokenId The ID of the token to delete.
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenDeleteRequest(
    @NonNull TokenId tokenId,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenDeleteRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
  }

  /**
   * Create a new TokenDeleteRequest for the given token ID.
   *
   * @param tokenId the token ID
   * @return the request
   */
  public static TokenDeleteRequest of(@NonNull final TokenId tokenId) {
    return new TokenDeleteRequest(
        tokenId, Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
