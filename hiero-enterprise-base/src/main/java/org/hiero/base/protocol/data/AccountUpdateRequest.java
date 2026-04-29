package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Request for an account update transaction. */
public record AccountUpdateRequest(
    @NonNull AccountId accountId,
    @Nullable Key key,
    @Nullable String memo,
    @Nullable Duration autoRenewPeriod,
    @Nullable Boolean receiverSignatureRequired,
    @Nullable Integer maxAutomaticTokenAssociations,
    @Nullable Hbar maxTransactionFee,
    @Nullable Duration transactionValidDuration,
    @Nullable PrivateKey updatedPrivateKey,
    @Nullable Account toUpdate // Compatibility with main branch
) implements TransactionRequest {

  public static AccountUpdateRequest of(@NonNull AccountId accountId) {
    return new AccountUpdateRequest(
        accountId, null, null, null, null, null, null, null, null, null);
  }

  @NonNull
  public static AccountUpdateRequest updateKey(
      @NonNull Account toUpdate, @NonNull PrivateKey updatedPrivateKey) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey is required");
    return new AccountUpdateRequest(
        toUpdate.accountId(),
        updatedPrivateKey.getPublicKey(),
        null,
        null,
        null,
        null,
        null,
        null,
        updatedPrivateKey,
        toUpdate);
  }

  @NonNull
  public static AccountUpdateRequest updateMemo(@NonNull Account toUpdate, @NonNull String memo) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(memo, "memo is required");
    return new AccountUpdateRequest(
        toUpdate.accountId(), null, memo, null, null, null, null, null, null, toUpdate);
  }

  @NonNull
  public static AccountUpdateRequest of(
      @NonNull Account toUpdate, @NonNull PrivateKey updatedPrivateKey, @NonNull String memo) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey is required");
    Objects.requireNonNull(memo, "memo is required");
    return new AccountUpdateRequest(
        toUpdate.accountId(),
        updatedPrivateKey.getPublicKey(),
        memo,
        null,
        null,
        null,
        null,
        null,
        updatedPrivateKey,
        toUpdate);
  }
}
