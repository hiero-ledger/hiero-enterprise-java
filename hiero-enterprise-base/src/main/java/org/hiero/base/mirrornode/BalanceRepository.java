package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with account and token balances on a Hiero network. This interface
 * provides methods for retrieving current account balances and balance snapshots.
 */
public interface BalanceRepository {
  /**
   * Returns a page of account balances.
   *
   * @return a page containing account balances
   * @throws HieroException if the balance data cannot be retrieved
   */
  @NonNull Page<AccountBalance> findAll() throws HieroException;

  /**
   * Returns the balance for the specified account.
   *
   * @param accountId the account whose balance is requested
   * @return optional containing the account balance
   * @throws HieroException if the balance data cannot be retrieved
   */
  @NonNull Optional<AccountBalance> findByAccount(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Returns the balance for the specified account.
   *
   * @param accountId the account whose balance is requested
   * @return optional containing the account balance
   * @throws HieroException if the balance data cannot be retrieved
   */
  @NonNull
  default Optional<AccountBalance> findByAccount(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findByAccount(AccountId.fromString(accountId));
  }

  /**
   * Returns latest snapshot of account and token balances on network.
   *
   * @return an optional containing the balance snapshot, or empty if no snapshot
   * @throws HieroException if the balance data cannot be retrieved
   */
  @NonNull Optional<BalanceSnapshot> getSnapshot() throws HieroException;
}
