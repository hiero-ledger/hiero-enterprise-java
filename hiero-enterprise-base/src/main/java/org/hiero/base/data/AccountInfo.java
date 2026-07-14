package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record AccountInfo(
    @NonNull AccountId accountId,
    @Nullable String alias,
    @Nullable Long autoRenewPeriod,
    long balance,
    Instant createdTimestamp,
    boolean declineReward,
    boolean deleted,
    long ethereumNonce,
    String evmAddress,
    Instant expiryTimestamp,
    Key key,
    int maxAutomaticTokenAssociations,
    @NonNull String memo,
    long pendingReward,
    boolean requireReceiverSignature,
    @Nullable AccountId stakedAccountId,
    @Nullable Long stakedNodeId,
    @Nullable Instant stakePeriodStart,
    @NonNull List<TransactionInfo> transactions) {
  public AccountInfo {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    Objects.requireNonNull(transactions, "transactions must not be null");
  }
}
