package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Request to update a topic.
 *
 * @param maxTransactionFee the maximum transaction fee
 * @param transactionValidDuration the transaction valid duration
 * @param topicId the topic to update
 * @param adminKey the current admin key of the topic; may only be null if the request does nothing
 *     but extend the expiration time
 * @param updatedAdminKey the new admin key, or null to keep the current one
 * @param submitKey the new submit key, or null to keep the current one
 * @param memo the new memo, or null to keep the current one
 * @param autoRenewAccountId the new auto renew account, or null to keep the current one
 * @param autoRenewAccountKey the private key of the new auto renew account
 * @param autoRenewPeriod the new auto renew period, or null to keep the current one
 * @param expirationTime the new expiration time, or null to keep the current one
 * @param clearAdminKey whether to remove the admin key, making the topic immutable
 * @param clearSubmitKey whether to remove the submit key, making the topic public
 * @param clearMemo whether to remove the memo
 * @param clearAutoRenewAccountId whether to remove the auto renew account
 */
public record TopicUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TopicId topicId,
    @Nullable PrivateKey adminKey,
    @Nullable PrivateKey updatedAdminKey,
    @Nullable PrivateKey submitKey,
    @Nullable String memo,
    @Nullable AccountId autoRenewAccountId,
    @Nullable PrivateKey autoRenewAccountKey,
    @Nullable Duration autoRenewPeriod,
    @Nullable Instant expirationTime,
    boolean clearAdminKey,
    boolean clearSubmitKey,
    boolean clearMemo,
    boolean clearAutoRenewAccountId)
    implements TransactionRequest {

  public TopicUpdateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee cannot be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration cannot be null");
    Objects.requireNonNull(topicId, "topicId cannot be null");
    requireNotBoth(updatedAdminKey != null, clearAdminKey, "admin key");
    requireNotBoth(submitKey != null, clearSubmitKey, "submit key");
    requireNotBoth(memo != null, clearMemo, "memo");
    requireNotBoth(autoRenewAccountId != null, clearAutoRenewAccountId, "auto renew account");
    if (clearAdminKey && autoRenewAccountId != null) {
      throw new IllegalArgumentException(
          "A topic without an admin key cannot have an auto renew account");
    }
    if (autoRenewAccountKey != null && autoRenewAccountId == null) {
      throw new IllegalArgumentException(
          "autoRenewAccountKey requires autoRenewAccountId to be set");
    }
    if (autoRenewPeriod != null && (autoRenewPeriod.isNegative() || autoRenewPeriod.isZero())) {
      throw new IllegalArgumentException("autoRenewPeriod must be positive");
    }
    final boolean requiresAdminKey =
        updatedAdminKey != null
            || submitKey != null
            || memo != null
            || autoRenewAccountId != null
            || autoRenewPeriod != null
            || clearAdminKey
            || clearSubmitKey
            || clearMemo
            || clearAutoRenewAccountId;
    if (requiresAdminKey) {
      Objects.requireNonNull(
          adminKey, "adminKey cannot be null unless only the expiration time is updated");
    }
  }

  private static void requireNotBoth(boolean set, boolean clear, String field) {
    if (set && clear) {
      throw new IllegalArgumentException("Cannot both set and clear the " + field);
    }
  }

  public static TopicUpdateRequest of(TopicId topicId, PrivateKey adminKey, String memo) {
    return of(topicId, adminKey, null, null, memo);
  }

  public static TopicUpdateRequest of(
      TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey, PrivateKey submitKey) {
    return of(topicId, adminKey, updatedAdminKey, submitKey, null);
  }

  public static TopicUpdateRequest updateAdminKey(
      TopicId topicId, PrivateKey adminKey, PrivateKey updatedAdminKey) {
    return of(topicId, adminKey, updatedAdminKey, null, null);
  }

  public static TopicUpdateRequest updateSubmitKey(
      TopicId topicId, PrivateKey adminKey, PrivateKey submitKey) {
    return of(topicId, adminKey, null, submitKey, null);
  }

  public static TopicUpdateRequest of(
      TopicId topicId,
      PrivateKey adminKey,
      PrivateKey updatedAdminKey,
      PrivateKey submitKey,
      String memo) {
    return new TopicUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        topicId,
        adminKey,
        updatedAdminKey,
        submitKey,
        memo,
        null,
        null,
        null,
        null,
        false,
        false,
        false,
        false);
  }

  public static TopicUpdateRequest updateAutoRenewAccount(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Account autoRenewAccount) {
    Objects.requireNonNull(autoRenewAccount, "autoRenewAccount cannot be null");
    return new TopicUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        topicId,
        adminKey,
        null,
        null,
        null,
        autoRenewAccount.accountId(),
        autoRenewAccount.privateKey(),
        null,
        null,
        false,
        false,
        false,
        false);
  }

  public static TopicUpdateRequest updateAutoRenewPeriod(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Duration autoRenewPeriod) {
    Objects.requireNonNull(autoRenewPeriod, "autoRenewPeriod cannot be null");
    return new TopicUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        topicId,
        adminKey,
        null,
        null,
        null,
        null,
        null,
        autoRenewPeriod,
        null,
        false,
        false,
        false,
        false);
  }

  /**
   * Creates a request that only extends the expiration time of a topic. No admin key is required,
   * so this also works for immutable topics.
   *
   * @param topicId the topic to update
   * @param expirationTime the new expiration time
   * @return the request
   */
  public static TopicUpdateRequest updateExpirationTime(
      @NonNull TopicId topicId, @NonNull Instant expirationTime) {
    Objects.requireNonNull(expirationTime, "expirationTime cannot be null");
    return new TopicUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        topicId,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        expirationTime,
        false,
        false,
        false,
        false);
  }

  /**
   * Creates a request that removes the admin key, making the topic immutable. The auto renew
   * account is removed in the same transaction, because the network rejects a topic without an
   * admin key that still has an auto renew account ({@code AUTORENEW_ACCOUNT_NOT_ALLOWED}).
   *
   * @param topicId the topic to update
   * @param adminKey the current admin key of the topic
   * @return the request
   */
  public static TopicUpdateRequest clearAdminKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey) {
    return clear(topicId, adminKey, true, false, false, true);
  }

  public static TopicUpdateRequest clearSubmitKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey) {
    return clear(topicId, adminKey, false, true, false, false);
  }

  public static TopicUpdateRequest clearMemo(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey) {
    return clear(topicId, adminKey, false, false, true, false);
  }

  public static TopicUpdateRequest clearAutoRenewAccount(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey) {
    return clear(topicId, adminKey, false, false, false, true);
  }

  private static TopicUpdateRequest clear(
      TopicId topicId,
      PrivateKey adminKey,
      boolean clearAdminKey,
      boolean clearSubmitKey,
      boolean clearMemo,
      boolean clearAutoRenewAccountId) {
    return new TopicUpdateRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        topicId,
        adminKey,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        clearAdminKey,
        clearSubmitKey,
        clearMemo,
        clearAutoRenewAccountId);
  }
}
