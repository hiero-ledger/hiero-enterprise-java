package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Request for an account info query. */
public record AccountInfoRequest(
    @NonNull AccountId accountId,
    @Nullable Hbar queryPayment,
    @Nullable Hbar maxQueryPayment)
    implements QueryRequest {

  public static AccountInfoRequest of(@NonNull final AccountId accountId) {
    return new AccountInfoRequest(accountId, null, null);
  }
}
