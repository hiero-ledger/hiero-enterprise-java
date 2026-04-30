package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Request to update a token.
 *
 * @param tokenId The ID of the token to update.
 * @param name The new name of the token.
 * @param symbol The new symbol of the token.
 * @param treasuryAccountId The new treasury account ID.
 * @param adminKey The new admin key.
 * @param kycKey The new KYC key.
 * @param freezeKey The new freeze key.
 * @param wipeKey The new wipe key.
 * @param supplyKey The new supply key.
 * @param feeScheduleKey The new fee schedule key.
 * @param pauseKey The new pause key.
 * @param metadataKey The new metadata key.
 * @param tokenMemo The new token memo.
 * @param autoRenewAccountId The new auto-renew account ID.
 * @param autoRenewPeriod The new auto-renew period.
 * @param expirationTime The new expiration time.
 * @param maxTransactionFee The maximum transaction fee.
 * @param transactionValidDuration The transaction valid duration.
 */
public record TokenUpdateRequest(
    @NonNull TokenId tokenId,
    @Nullable String name,
    @Nullable String symbol,
    @Nullable AccountId treasuryAccountId,
    @Nullable Key adminKey,
    @Nullable Key kycKey,
    @Nullable Key freezeKey,
    @Nullable Key wipeKey,
    @Nullable Key supplyKey,
    @Nullable Key feeScheduleKey,
    @Nullable Key pauseKey,
    @Nullable Key metadataKey,
    @Nullable String tokenMemo,
    @Nullable AccountId autoRenewAccountId,
    @Nullable Duration autoRenewPeriod,
    @Nullable Instant expirationTime,
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration)
    implements TransactionRequest {

  public TokenUpdateRequest {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
  }

  /**
   * Create a new TokenUpdateRequest for the given token ID.
   *
   * @param tokenId the token ID
   * @return the request
   */
  public static TokenUpdateRequest of(@NonNull final TokenId tokenId) {
    return new TokenUpdateRequest(
        tokenId,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        Hbar.from(100),
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
  }
}
