package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

public interface BalanceRepository {
  @NonNull Page<AccountBalance> findAll() throws HieroException;

  @NonNull Page<AccountBalance> findByAccount(@NonNull AccountId accountId) throws HieroException;

  @NonNull
  default Page<AccountBalance> findByAccount(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findByAccount(AccountId.fromString(accountId));
  }

  @NonNull Optional<BalanceSnapshot> findSnapshot() throws HieroException;
}
