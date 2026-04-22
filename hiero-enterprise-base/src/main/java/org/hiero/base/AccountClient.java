package org.hiero.base;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.List;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.hiero.base.data.HookDetails;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with a Hiero network. This interface provides methods for interacting
 * with Hedera accounts, like creating and deleting accounts. An implementation of this interface is
 * using an internal account to interact with a Hiero network. That account is the account that is
 * used to pay for the transactions that are sent to the network and called 'operator account'.
 */
public interface AccountClient {

  /**
   * Creates a new account. The account is created with an initial balance of 0 hbar. The account is
   * created by the operator account.
   *
   * @return the created account
   * @throws HieroException if the account could not be created
   */
  @NonNull
  default Account createAccount() throws HieroException {
    return createAccount(Hbar.ZERO);
  }

  /**
   * Creates a new account with the given initial balance. The account is created by the operator
   * account.
   *
   * @param initialBalance the initial balance of the account
   * @return the created account
   * @throws HieroException if the account could not be created
   */
  @NonNull Account createAccount(@NonNull Hbar initialBalance) throws HieroException;

  /**
   * Creates a new account with the given initial balance (in HBAR). The account is created by the
   * operator account.
   *
   * @param initialBalanceInHbar the initial balance of the account in HBAR
   * @return the created account
   * @throws HieroException if the account could not be created
   */
  @NonNull
  default Account createAccount(long initialBalanceInHbar) throws HieroException {
    if (initialBalanceInHbar < 0) {
      throw new IllegalArgumentException("initialBalanceInHbar must be non-negative");
    }
    return createAccount(Hbar.from(initialBalanceInHbar));
  }

  /**
   * Deletes the account with the given ID. All fees of that account are transferred to the operator
   * account.
   *
   * @param account the account to delete
   * @throws HieroException if the account could not be deleted
   */
  void deleteAccount(@NonNull Account account) throws HieroException;

  /**
   * Deletes the account with the given ID. All fees of that account are transferred to the given
   * toAccount.
   *
   * @param account the account to delete
   * @param toAccount the account to transfer the fees to
   * @throws HieroException if the account could not be deleted
   */
  void deleteAccount(@NonNull Account account, @NonNull Account toAccount) throws HieroException;

  /**
   * Updates the account key of the given account.
   *
   * @param account the account to update
   * @param updatedPrivateKey the new private key to set for the account
   * @return the updated account with the same account ID and new key pair
   * @throws HieroException if the account could not be updated
   */
  @NonNull Account updateAccountKey(@NonNull Account account, @NonNull PrivateKey updatedPrivateKey)
      throws HieroException;

  /**
   * Updates the memo of the given account.
   *
   * @param account the account to update
   * @param memo the new memo
   * @throws HieroException if the account could not be updated
   */
  void updateAccountMemo(@NonNull Account account, @NonNull String memo) throws HieroException;

  /**
   * Updates both key and memo of the given account.
   *
   * @param account the account to update
   * @param updatedPrivateKey the new private key to set for the account
   * @param memo the new memo
   * @return the updated account with the same account ID and new key pair
   * @throws HieroException if the account could not be updated
   */
  @NonNull Account updateAccount(
      @NonNull Account account, @NonNull PrivateKey updatedPrivateKey, @NonNull String memo)
      throws HieroException;

  /**
   * Returns the balance of the given account.
   *
   * @param accountId the ID of the account
   * @return the balance of the account
   * @throws HieroException if the balance could not be retrieved
   */
  @NonNull Hbar getAccountBalance(@NonNull AccountId accountId) throws HieroException;

  /**
   * Returns the balance of the given account.
   *
   * @param accountId the ID of the account
   * @return the balance of the account
   * @throws HieroException if the balance could not be retrieved
   */
  @NonNull
  default Hbar getAccountBalance(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return getAccountBalance(AccountId.fromString(accountId));
  }

  /**
   * Returns the balance of the operator account.
   *
   * @return the balance of the operator account
   * @throws HieroException if the balance could not be retrieved
   */
  @NonNull Hbar getOperatorAccountBalance() throws HieroException;

  /** Adds a hook to an account. */
  default void addHook(@NonNull Account account, @NonNull HookDetails hookDetails)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    Objects.requireNonNull(hookDetails, "hookDetails must not be null");
    updateHooks(account, List.of(hookDetails), List.of());
  }

  /** Deletes a hook from an account. */
  default void deleteHook(@NonNull Account account, long hookId) throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    if (hookId < 0) {
      throw new IllegalArgumentException("hookId must be non-negative");
    }
    updateHooks(account, List.of(), List.of(hookId));
  }

  /** Updates account hooks by creating and/or deleting hooks. */
  default void updateHooks(
      @NonNull Account account,
      @NonNull List<HookDetails> hooksToCreate,
      @NonNull List<Long> hookIdsToDelete)
      throws HieroException {
    throw new UnsupportedOperationException("Account hook management is not implemented yet.");
  }
}
