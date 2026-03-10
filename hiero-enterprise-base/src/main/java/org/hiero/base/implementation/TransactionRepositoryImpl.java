package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Page;
import org.hiero.base.data.Result;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;

public class TransactionRepositoryImpl implements TransactionRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public TransactionRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<TransactionInfo> findByAccount(@NonNull final AccountId accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return this.mirrorNodeClient.queryTransactionsByAccount(accountId);
  }

  @Override
  public @NonNull Page<TransactionInfo> findByAccountAndType(
      @NonNull AccountId accountId, @NonNull TransactionType type) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(type, "type must not be null");
    return mirrorNodeClient.queryTransactionsByAccountAndType(accountId, type);
  }

  @Override
  public @NonNull Page<TransactionInfo> findByAccountAndResult(
      @NonNull AccountId accountId, @NonNull Result result) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(result, "result must not be null");
    return mirrorNodeClient.queryTransactionsByAccountAndResult(accountId, result);
  }

  @Override
  public @NonNull Page<TransactionInfo> findByAccountAndModification(
      @NonNull AccountId accountId, @NonNull BalanceModification type) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(type, "type must not be null");
    return mirrorNodeClient.queryTransactionsByAccountAndModification(accountId, type);
  }

  @NonNull
  @Override
  public Optional<TransactionInfo> findById(@NonNull final String transactionId)
      throws HieroException {
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    return this.mirrorNodeClient.queryTransaction(transactionId);
  }
}
