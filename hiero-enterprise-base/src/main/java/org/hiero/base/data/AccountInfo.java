package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record AccountInfo(
    @NonNull AccountId accountId,
    String alias,
    Long autoRenewPeriod,
    long balance,
    Instant createdTimestamp,
    boolean declineReward,
    boolean deleted,
    long ethereumNonce,
    @NonNull String evmAddress,
    Instant expiryTimestamp,
    Key key,
    int maxAutomaticTokenAssociations,
    String memo,
    long pendingReward,
    boolean requireReceiverSignature,
    AccountId stakedAccountId,
    Long stakedNodeId,
    Instant stakePeriodStart,
    @NonNull List<TransactionInfo> transactions) {
  public AccountInfo {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(evmAddress, "evmAddress must not be null");
    Objects.requireNonNull(transactions, "transactions must not be null");
  }
}
