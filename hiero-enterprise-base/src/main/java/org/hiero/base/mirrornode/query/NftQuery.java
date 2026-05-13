package org.hiero.base.mirrornode.query;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import org.hiero.base.data.Order;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Optional;

/** Query object for NFTs. */
public final class NftQuery {

  private final AccountId accountId;
  private final TokenId tokenId;
  private final Integer limit;
  private final Order order;

  private NftQuery(Builder builder) {
    this.accountId = builder.accountId;
    this.tokenId = builder.tokenId;
    this.limit = builder.limit;
    this.order = builder.order;
  }

  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  public @NonNull Optional<AccountId> getAccountId() {
    return Optional.ofNullable(accountId);
  }

  public @NonNull Optional<TokenId> getTokenId() {
    return Optional.ofNullable(tokenId);
  }

  public @NonNull Optional<Integer> getLimit() {
    return Optional.ofNullable(limit);
  }

  public @NonNull Optional<Order> getOrder() {
    return Optional.ofNullable(order);
  }

  public static class Builder {
    private AccountId accountId;
    private TokenId tokenId;
    private Integer limit;
    private Order order;

    public Builder accountId(@Nullable AccountId accountId) {
      this.accountId = accountId;
      return this;
    }

    public Builder accountId(@Nullable String accountId) {
      this.accountId = accountId != null ? AccountId.fromString(accountId) : null;
      return this;
    }

    public Builder tokenId(@Nullable TokenId tokenId) {
      this.tokenId = tokenId;
      return this;
    }

    public Builder tokenId(@Nullable String tokenId) {
      this.tokenId = tokenId != null ? TokenId.fromString(tokenId) : null;
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

    @NonNull
    public NftQuery build() {
      return new NftQuery(this);
    }
  }
}
