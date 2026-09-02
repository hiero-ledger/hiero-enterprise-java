package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents the balance information for an account.
 *
 * @param account account the network entity ID of the account
 * @param balance the account's hbar balance in the tinyhbar format
 * @param tokens the token balances associated with the account
 */
public record AccountBalance(
    @Nullable AccountId account, long balance, @NonNull List<TokenBalance> tokens) {
  public AccountBalance {
    tokens = List.copyOf(Objects.requireNonNull(tokens, "tokens must not be null"));
  }
}
