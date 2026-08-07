package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.TopicId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record Topic(
    @NonNull TopicId topicId,
    @Nullable Key adminKey,
    @Nullable AccountId autoRenewAccount,
    int autoRenewPeriod,
    @NonNull Instant createdTimestamp,
    @NonNull List<FixedFee> fixedFees,
    @Nullable List<Key> feeExemptKeyList,
    @Nullable Key feeScheduleKey,
    @Nullable Key submitKey,
    boolean deleted,
    String memo,
    @NonNull TimestampRange timestampRange) {
  public Topic {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(createdTimestamp, "createdTimestamp must not be null");
    Objects.requireNonNull(fixedFees, "fixedFees must not be null");
  }
}
