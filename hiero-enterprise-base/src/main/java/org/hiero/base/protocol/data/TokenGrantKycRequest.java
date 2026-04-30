package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to grant KYC for a token for an account.
 *
 * @param tokenId The ID of the token.
 * @param accountId The ID of the account to grant KYC.
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenGrantKycRequest(
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenGrantKycRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
  }

  /**
   * Create a new TokenGrantKycRequest for the given token and account.
   *
   * @param tokenId the token ID
   * @param accountId the account ID
   * @return the request
   */
  public static TokenGrantKycRequest of(
      @NonNull final TokenId tokenId, @NonNull final AccountId accountId) {
    return new TokenGrantKycRequest(
        tokenId, accountId, Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
