package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a scheduled transaction returned by the Mirror Node REST API. */
public record Schedule(
    @NonNull ScheduleId scheduleId,
    @Nullable PublicKey adminKey,
    boolean deleted,
    @NonNull Instant consensusTimestamp,
    @NonNull AccountId creatorAccountId,
    @Nullable Instant executedTimestamp,
    @Nullable Instant expirationTime,
    @NonNull String memo,
    @Nullable AccountId payerAccountId,
    @NonNull List<ScheduleSignature> signatures,
    byte @Nullable [] transactionBody,
    boolean waitForExpiry) {

  public Schedule {
    Objects.requireNonNull(scheduleId, "scheduleId must not be null");
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(creatorAccountId, "creatorAccountId must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    signatures = List.copyOf(Objects.requireNonNull(signatures, "signatures must not be null"));
    transactionBody = transactionBody == null ? null : transactionBody.clone();
  }

  @Override
  public byte @Nullable [] transactionBody() {
    return transactionBody == null ? null : transactionBody.clone();
  }
}
