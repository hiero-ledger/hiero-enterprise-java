package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to pause an NFT type. Must be signed by the token pause key.
 *
 * @param maxTransactionFee the max transaction fee
 * @param transactionValidDuration the transaction valid duration
 * @param tokenId the NFT type
 * @param pauseKey the pause key of the token
 */
public record TokenPauseRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull PrivateKey pauseKey)
    implements TransactionRequest {

  @NonNull
  public TokenPauseRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(pauseKey, "pauseKey must not be null");
  }

  public static TokenPauseRequest of(
      @NonNull final TokenId tokenId, @NonNull final PrivateKey pauseKey) {

    return new TokenPauseRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        pauseKey);
  }
}
