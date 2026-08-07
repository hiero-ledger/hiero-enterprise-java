package org.hiero.base.mirrornode.query;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.data.Order;
import org.hiero.base.data.Result;
import org.hiero.base.data.TimestampRange;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Query object for transactions. */
public final class TransactionQuery {

  private final AccountId accountId;
  private final TransactionType type;
  private final Result result;
  private final Integer limit;
  private final Order order;
  private final TimestampRange timestampRange;

  private TransactionQuery(Builder builder) {
    this.accountId = builder.accountId;
    this.type = builder.type;
    this.result = builder.result;
    this.limit = builder.limit;
    this.order = builder.order;
    this.timestampRange = builder.timestampRange;
  }

  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  public @NonNull Optional<AccountId> getAccountId() {
    return Optional.ofNullable(accountId);
  }

  public @NonNull Optional<TransactionType> getType() {
    return Optional.ofNullable(type);
  }

  public @NonNull Optional<Result> getResult() {
    return Optional.ofNullable(result);
  }

  public @NonNull Optional<Integer> getLimit() {
    return Optional.ofNullable(limit);
  }

  public @NonNull Optional<Order> getOrder() {
    return Optional.ofNullable(order);
  }

  public @NonNull Optional<TimestampRange> getTimestampRange() {
    return Optional.ofNullable(timestampRange);
  }

  public static class Builder {
    private AccountId accountId;
    private TransactionType type;
    private Result result;
    private Integer limit;
    private Order order;
    private TimestampRange timestampRange;

    public Builder accountId(@Nullable AccountId accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder accountId(@Nullable String accountId) {
      this.accountId = accountId != null ? AccountId.fromString(accountId) : null;
      return this;
    }

    public Builder type(@Nullable TransactionType type) {
      this.type = type;
      return this;
    }

    public Builder result(@Nullable Result result) {
      this.result = result;
      return this;
    }

    public Builder limit(@Nullable Integer limit) {
      this.limit = limit;
      return this;
    }

    public Builder order(@Nullable Order order) {
      this.order = order;
      return this;
    }

    public Builder timestampRange(@Nullable TimestampRange timestampRange) {
      this.timestampRange = timestampRange;
      return this;
    }

    @NonNull
    public TransactionQuery build() {
      return new TransactionQuery(this);
    }
  }
}
