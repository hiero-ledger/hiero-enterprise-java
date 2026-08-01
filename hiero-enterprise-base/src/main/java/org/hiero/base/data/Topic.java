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
    @Nullable TopicId topicId,
    @Nullable Key adminKey,
    @Nullable AccountId autoRenewAccount,
    @Nullable Long autoRenewPeriod,
    @Nullable Instant createdTimestamp,
    @NonNull List<FixedFee> fixedFees,
    @NonNull List<Key> feeExemptKeyList,
    @Nullable Key feeScheduleKey,
    @Nullable Key submitKey,
    boolean deleted,
    String memo,
    @NonNull TimestampRange timestampRange) {
  public Topic {
    Objects.requireNonNull(fixedFees, "fixedFees must not be null");
    Objects.requireNonNull(feeExemptKeyList, "feeExemptKeyList must not be null");
    Objects.requireNonNull(timestampRange, "timestampRange must not be null");
  }
}
