package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record AccountBalance(
    @NonNull AccountId account, long balance, @NonNull List<TokenBalance> tokens) {
  public AccountBalance {
    Objects.requireNonNull(account, "account must not be null");
    tokens = List.copyOf(Objects.requireNonNull(tokens, "tokens must not be null"));
  }
}
