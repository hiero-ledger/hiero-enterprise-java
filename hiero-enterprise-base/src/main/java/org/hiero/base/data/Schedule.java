package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a schedule entity that governs the execution of a scheduled transaction.
 *
 * @param scheduleId the ID of the schedule
 * @param adminKey the key that can delete the schedule, or {@code null} if the schedule is
 *     immutable
 * @param consensusTimestamp the consensus timestamp of the transaction that created the schedule
 * @param creatorAccountId the account that created the schedule
 * @param deleted whether the schedule has been deleted
 * @param executedTimestamp the consensus timestamp of the execution, or {@code null} if the
 *     scheduled transaction has not been executed
 * @param expirationTime the expiration time of the schedule, or {@code null} if no expiration time
 *     was set
 * @param memo the memo of the schedule
 * @param payerAccountId the account that pays for the execution of the scheduled transaction
 * @param signatures the signatures that have been collected for the scheduled transaction
 * @param transactionBody the protobuf encoded {@code SchedulableTransactionBody} of the scheduled
 *     transaction
 * @param waitForExpiry whether the scheduled transaction is executed at the expiration time instead
 *     of when all required signatures are collected
 */
public record Schedule(
    @NonNull ScheduleId scheduleId,
    @Nullable Key adminKey,
    @NonNull Instant consensusTimestamp,
    @NonNull AccountId creatorAccountId,
    boolean deleted,
    @Nullable Instant executedTimestamp,
    @Nullable Instant expirationTime,
    @NonNull String memo,
    @NonNull AccountId payerAccountId,
    @NonNull List<ScheduleSignature> signatures,
    @NonNull byte[] transactionBody,
    boolean waitForExpiry) {

  public Schedule {
    Objects.requireNonNull(scheduleId, "scheduleId must not be null");
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(creatorAccountId, "creatorAccountId must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    Objects.requireNonNull(payerAccountId, "payerAccountId must not be null");
    Objects.requireNonNull(signatures, "signatures must not be null");
    Objects.requireNonNull(transactionBody, "transactionBody must not be null");
  }
}
