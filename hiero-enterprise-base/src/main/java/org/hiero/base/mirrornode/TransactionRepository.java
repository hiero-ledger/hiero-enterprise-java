package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroBaseException;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Page;
import org.hiero.base.data.Result;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with transactions on a Hiero network. This interface provides methods
 * for querying transactions.
 */
public interface TransactionRepository {
  /**
   * Find all transactions associated with a specific account.
   *
   * @param accountId id of the account
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull Page<TransactionInfo> findByAccount(@NonNull AccountId accountId) throws HieroBaseException;

  /**
   * Find all transactions associated with a specific account.
   *
   * @param accountId id of the account as a string
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull
  default Page<TransactionInfo> findByAccount(@NonNull String accountId) throws HieroBaseException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findByAccount(AccountId.fromString(accountId));
  }

  /**
   * Find all transactions associated with a specific account and has specific transaction type.
   *
   * @param accountId id of the account
   * @param type type of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull Page<TransactionInfo> findByAccountAndType(
      @NonNull AccountId accountId, @NonNull TransactionType type) throws HieroBaseException;

  /**
   * Find all transactions associated with a specific account and has specific transaction type.
   *
   * @param accountId id of the account
   * @param type type of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull
  default Page<TransactionInfo> findByAccountAndType(
      @NonNull String accountId, @NonNull TransactionType type) throws HieroBaseException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(type, "type must not be null");
    return findByAccountAndType(AccountId.fromString(accountId), type);
  }
  ;

  /**
   * Find all transactions associated with a specific account and has specific transaction result.
   *
   * @param accountId id of the account
   * @param result result of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull Page<TransactionInfo> findByAccountAndResult(
      @NonNull AccountId accountId, @NonNull Result result) throws HieroBaseException;

  /**
   * Find all transactions associated with a specific account and has specific transaction result.
   *
   * @param accountId id of the account
   * @param result result of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull
  default Page<TransactionInfo> findByAccountAndResult(
      @NonNull String accountId, @NonNull Result result) throws HieroBaseException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(result, "result must not be null");
    return findByAccountAndResult(AccountId.fromString(accountId), result);
  }
  ;

  /**
   * Find all transactions associated with a specific account and has specific transaction
   * modification type.
   *
   * @param accountId id of the account
   * @param type type of balance modification of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull Page<TransactionInfo> findByAccountAndModification(
      @NonNull AccountId accountId, @NonNull BalanceModification type) throws HieroBaseException;

  /**
   * Find all transactions associated with a specific account and has specific transaction
   * modification type.
   *
   * @param accountId id of the account
   * @param type type of balance modification of transaction
   * @return page of transactions
   * @throws HieroBaseException if the search fails
   */
  @NonNull
  default Page<TransactionInfo> findByAccountAndModification(
      @NonNull String accountId, @NonNull BalanceModification type) throws HieroBaseException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(type, "type must not be null");
    return findByAccountAndModification(AccountId.fromString(accountId), type);
  }
  ;

  /**
   * Find a specific transaction by its ID.
   *
   * @param transactionId the transaction ID
   * @return Optional containing the transaction if found
   * @throws HieroBaseException if the search fails
   */
  @NonNull Optional<TransactionInfo> findById(@NonNull String transactionId) throws HieroBaseException;
}
