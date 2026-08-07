package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.query.AccountQuery;
import org.jspecify.annotations.NonNull;

public class AccountRepositoryImpl implements AccountRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public AccountRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public Optional<AccountInfo> findById(@NonNull AccountId accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return mirrorNodeClient.queryAccount(accountId);
  }

  @Override
  public @NonNull Page<AccountInfo> findAll(@NonNull AccountQuery query) throws HieroException {
    Objects.requireNonNull(query, "query must not be null");
    return mirrorNodeClient.queryAccounts(query);
  }
}
