package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Request to wipe tokens from an account.
 *
 * @param tokenId The ID of the token.
 * @param accountId The ID of the account to wipe tokens from.
 * @param amount The amount to wipe (for fungible tokens).
 * @param serials The serial numbers to wipe (for NFTs).
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenWipeRequest(
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @Nullable Long amount,
    @Nullable List<Long> serials,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenWipeRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    if (amount == null && (serials == null || serials.isEmpty())) {
      throw new IllegalArgumentException("either amount or serials must be provided");
    }
  }

  /**
   * Create a new TokenWipeRequest for fungible tokens.
   *
   * @param tokenId the token ID
   * @param accountId the account ID
   * @param amount the amount to wipe
   * @return the request
   */
  public static TokenWipeRequest of(
      @NonNull final TokenId tokenId, @NonNull final AccountId accountId, long amount) {
    return new TokenWipeRequest(
        tokenId,
        accountId,
        amount,
        null,
        Hbar.from(100),
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }

  /**
   * Create a new TokenWipeRequest for NFTs.
   *
   * @param tokenId the token ID
   * @param accountId the account ID
   * @param serials the serial numbers to wipe
   * @return the request
   */
  public static TokenWipeRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final AccountId accountId,
      @NonNull List<Long> serials) {
    return new TokenWipeRequest(
        tokenId,
        accountId,
        null,
        serials,
        Hbar.from(100),
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
