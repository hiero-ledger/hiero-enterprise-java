package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

/**
 * Request to wipe NFT serials from an account. Must be signed by the token wipe key. The account
 * must not be the token treasury.
 *
 * @param maxTransactionFee the max transaction fee
 * @param transactionValidDuration the transaction valid duration
 * @param tokenId the NFT type
 * @param accountId the account to wipe from
 * @param serials the serial numbers to wipe
 * @param wipeKey the wipe key of the token
 */
public record TokenWipeRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull AccountId accountId,
    @NonNull Set<Long> serials,
    @NonNull PrivateKey wipeKey)
    implements TransactionRequest {

  @NonNull
  public TokenWipeRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(wipeKey, "wipeKey must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    if (serials.stream().anyMatch(serial -> serial < 0)) {
      throw new IllegalArgumentException("serials must be positive");
    }
  }

  @NonNull
  public static TokenWipeRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final AccountId accountId,
      long serial,
      @NonNull final PrivateKey wipeKey) {
    return of(tokenId, accountId, Set.of(serial), wipeKey);
  }

  public static TokenWipeRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final AccountId accountId,
      @NonNull final Set<Long> serials,
      @NonNull final PrivateKey wipeKey) {
    return new TokenWipeRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        accountId,
        serials,
        wipeKey);
  }
}
