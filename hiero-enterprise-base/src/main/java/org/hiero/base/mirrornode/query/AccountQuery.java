package org.hiero.base.mirrornode.query;

import org.hiero.base.data.Order;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Optional;

/** Query object for accounts. */
public final class AccountQuery {

  private final Integer limit;
  private final Order order;
  private final Boolean balance;

  private AccountQuery(Builder builder) {
    this.limit = builder.limit;
    this.order = builder.order;
    this.balance = builder.balance;
  }

  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  public @NonNull Optional<Integer> getLimit() {
    return Optional.ofNullable(limit);
  }

  public @NonNull Optional<Order> getOrder() {
    return Optional.ofNullable(order);
  }

  public @NonNull Optional<Boolean> getBalance() {
    return Optional.ofNullable(balance);
  }

  public static class Builder {
    private Integer limit;
    private Order order;
    private Boolean balance;

    public Builder limit(@Nullable Integer limit) {
      this.limit = limit;
      return this;
    }

    public Builder order(@Nullable Order order) {
      this.order = order;
      return this;
    }

    public Builder balance(@Nullable Boolean balance) {
      this.balance = balance;
      return this;
    }

    @NonNull
    public AccountQuery build() {
      return new AccountQuery(this);
    }
  }
}
