package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to unfreeze a token for an account.
 *
 * @param tokenId The ID of the token.
 * @param accountId The ID of the account to unfreeze.
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenUnfreezeRequest(
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenUnfreezeRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
  }

  /**
   * Create a new TokenUnfreezeRequest for the given token and account.
   *
   * @param tokenId the token ID
   * @param accountId the account ID
   * @return the request
   */
  public static TokenUnfreezeRequest of(
      @NonNull final TokenId tokenId, @NonNull final AccountId accountId) {
    return new TokenUnfreezeRequest(
        tokenId, accountId, Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
