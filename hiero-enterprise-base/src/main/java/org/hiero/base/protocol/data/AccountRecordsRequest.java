package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Request for an account records query. */
public record AccountRecordsRequest(
    @NonNull AccountId accountId,
    @Nullable Hbar queryPayment,
    @Nullable Hbar maxQueryPayment)
    implements QueryRequest {

  public static AccountRecordsRequest of(@NonNull final AccountId accountId) {
    return new AccountRecordsRequest(accountId, null, null);
  }
}
