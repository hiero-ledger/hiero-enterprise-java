package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Request to create a topic.
 *
 * @param maxTransactionFee the maximum transaction fee
 * @param transactionValidDuration the transaction valid duration
 * @param adminKey the admin key of the topic, or null to create an immutable topic
 * @param submitKey the submit key of the topic, or null to create a public topic
 * @param memo the memo of the topic, or null
 * @param autoRenewAccountId the account that pays for auto renewal, or null to use the default (the
 *     transaction payer)
 * @param autoRenewAccountKey the private key of the auto renew account; required if the auto renew
 *     account is not the transaction payer
 * @param autoRenewPeriod the initial lifetime and auto renew period of the topic, or null to use
 *     the default
 */
public record TopicCreateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @Nullable PrivateKey adminKey,
    @Nullable PrivateKey submitKey,
    @Nullable String memo,
    @Nullable AccountId autoRenewAccountId,
    @Nullable PrivateKey autoRenewAccountKey,
    @Nullable Duration autoRenewPeriod)
    implements TransactionRequest {

  public TopicCreateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee cannot be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration cannot be null");
    if (autoRenewAccountKey != null && autoRenewAccountId == null) {
      throw new IllegalArgumentException(
          "autoRenewAccountKey requires autoRenewAccountId to be set");
    }
    if (autoRenewPeriod != null && (autoRenewPeriod.isNegative() || autoRenewPeriod.isZero())) {
      throw new IllegalArgumentException("autoRenewPeriod must be positive");
    }
  }

  public static TopicCreateRequest of(PrivateKey adminKey) {
    return of(adminKey, null, null);
  }

  public static TopicCreateRequest of(PrivateKey adminKey, String memo) {
    return of(adminKey, null, memo);
  }

  public static TopicCreateRequest of(PrivateKey adminKey, PrivateKey submitKey) {
    return of(adminKey, submitKey, null);
  }

  public static TopicCreateRequest of(PrivateKey adminKey, PrivateKey submitKey, String memo) {
    Objects.requireNonNull(adminKey, "adminKey cannot be null");
    return of(adminKey, submitKey, memo, null, null);
  }

  /**
   * Creates a request for an immutable topic. A topic without an admin key cannot be updated or
   * deleted; only its expiration time can be extended.
   *
   * @param submitKey the submit key of the topic, or null to create a public topic
   * @param memo the memo of the topic, or null
   * @return the request
   */
  public static TopicCreateRequest immutable(
      @Nullable PrivateKey submitKey, @Nullable String memo) {
    return of(null, submitKey, memo, null, null);
  }

  public static TopicCreateRequest of(
      @Nullable PrivateKey adminKey,
      @Nullable PrivateKey submitKey,
      @Nullable String memo,
      @Nullable Account autoRenewAccount,
      @Nullable Duration autoRenewPeriod) {
    return new TopicCreateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        adminKey,
        submitKey,
        memo,
        autoRenewAccount == null ? null : autoRenewAccount.accountId(),
        autoRenewAccount == null ? null : autoRenewAccount.privateKey(),
        autoRenewPeriod);
  }
}
