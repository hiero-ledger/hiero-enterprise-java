package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record AccountInfoRequest(
    @NonNull AccountId accountId, @Nullable Hbar queryPayment, @Nullable Hbar maxQueryPayment)
    implements QueryRequest {

  public AccountInfoRequest {
    Objects.requireNonNull(accountId, "accountId must not be null");
  }

  @NonNull
  public static AccountInfoRequest of(@NonNull AccountId accountId) {
    return new AccountInfoRequest(accountId, null, null);
  }

  @NonNull
  public static AccountInfoRequest of(@NonNull String accountId) {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return of(AccountId.fromString(accountId));
  }

  @Override
  public Hbar queryPayment() {
    return queryPayment;
  }

  @Override
  public Hbar maxQueryPayment() {
    return maxQueryPayment;
  }
}
