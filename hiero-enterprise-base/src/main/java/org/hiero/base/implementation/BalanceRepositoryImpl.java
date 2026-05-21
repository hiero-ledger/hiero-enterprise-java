package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BalanceRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

public class BalanceRepositoryImpl implements BalanceRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public BalanceRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public @NonNull Page<AccountBalance> findAll() throws HieroException {
    return mirrorNodeClient.queryBalances();
  }

  @Override
  public @NonNull Page<AccountBalance> findByAccount(@NonNull AccountId accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return mirrorNodeClient.queryBalancesByAccount(accountId);
  }

  @Override
  public @NonNull Optional<BalanceSnapshot> findSnapshot() throws HieroException {
    return mirrorNodeClient.queryBalanceSnapshot();
  }
}
