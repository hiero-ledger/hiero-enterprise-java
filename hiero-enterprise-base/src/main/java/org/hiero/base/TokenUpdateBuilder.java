package org.hiero.base;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import java.time.Duration;
import java.time.Instant;
import org.hiero.base.protocol.data.TokenUpdateResult;
import org.jspecify.annotations.NonNull;

/** A fluent builder for updating a token. */
public interface TokenUpdateBuilder {

  /**
   * Sets the new name for the token.
   *
   * @param name the new name
   * @return this builder
   */
  @NonNull TokenUpdateBuilder name(@NonNull String name);

  /**
   * Sets the new symbol for the token.
   *
   * @param symbol the new symbol
   * @return this builder
   */
  @NonNull TokenUpdateBuilder symbol(@NonNull String symbol);

  /**
   * Sets the new treasury account ID for the token.
   *
   * @param treasuryAccountId the new treasury account ID
   * @return this builder
   */
  @NonNull TokenUpdateBuilder treasuryAccountId(@NonNull AccountId treasuryAccountId);

  /**
   * Sets the new admin key for the token.
   *
   * @param adminKey the new admin key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder adminKey(@NonNull Key adminKey);

  /**
   * Sets the new KYC key for the token.
   *
   * @param kycKey the new KYC key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder kycKey(@NonNull Key kycKey);

  /**
   * Sets the new freeze key for the token.
   *
   * @param freezeKey the new freeze key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder freezeKey(@NonNull Key freezeKey);

  /**
   * Sets the new wipe key for the token.
   *
   * @param wipeKey the new wipe key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder wipeKey(@NonNull Key wipeKey);

  /**
   * Sets the new supply key for the token.
   *
   * @param supplyKey the new supply key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder supplyKey(@NonNull Key supplyKey);

  /**
   * Sets the new fee schedule key for the token.
   *
   * @param feeScheduleKey the new fee schedule key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder feeScheduleKey(@NonNull Key feeScheduleKey);

  /**
   * Sets the new pause key for the token.
   *
   * @param pauseKey the new pause key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder pauseKey(@NonNull Key pauseKey);

  /**
   * Sets the new metadata key for the token.
   *
   * @param metadataKey the new metadata key
   * @return this builder
   */
  @NonNull TokenUpdateBuilder metadataKey(@NonNull Key metadataKey);

  /**
   * Sets the new memo for the token.
   *
   * @param memo the new memo
   * @return this builder
   */
  @NonNull TokenUpdateBuilder memo(@NonNull String memo);

  /**
   * Sets the new auto-renew account ID for the token.
   *
   * @param autoRenewAccountId the new auto-renew account ID
   * @return this builder
   */
  @NonNull TokenUpdateBuilder autoRenewAccountId(@NonNull AccountId autoRenewAccountId);

  /**
   * Sets the new auto-renew period for the token.
   *
   * @param autoRenewPeriod the new auto-renew period
   * @return this builder
   */
  @NonNull TokenUpdateBuilder autoRenewPeriod(@NonNull Duration autoRenewPeriod);

  /**
   * Sets the new expiration time for the token.
   *
   * @param expirationTime the new expiration time
   * @return this builder
   */
  @NonNull TokenUpdateBuilder expirationTime(@NonNull Instant expirationTime);

  /**
   * Executes the token update transaction.
   *
   * @return the result of the update
   * @throws HieroException if the update fails
   */
  @NonNull TokenUpdateResult execute() throws HieroException;
}
