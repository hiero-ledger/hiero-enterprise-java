package org.hiero.base;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
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

  /**
   * Transfers HBAR from the operator account to another account.
   *
   * @param toAccountId the account that receives HBAR
   * @param amount the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  void transferHbar(@NonNull AccountId toAccountId, @NonNull Hbar amount) throws HieroException;

  /**
   * Transfers HBAR from the operator account to another account.
   *
   * @param toAccountId the account that receives HBAR
   * @param amount the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  default void transferHbar(@NonNull String toAccountId, @NonNull Hbar amount)
      throws HieroException {
    Objects.requireNonNull(toAccountId, "toAccountId must not be null");
    transferHbar(AccountId.fromString(toAccountId), amount);
  }

  /**
   * Transfers HBAR from the operator account to another account.
   *
   * @param toAccountId the account that receives HBAR
   * @param amountInHbar the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  default void transferHbar(@NonNull AccountId toAccountId, long amountInHbar)
      throws HieroException {
    if (amountInHbar <= 0) {
      throw new IllegalArgumentException("amountInHbar must be positive");
    }
    transferHbar(toAccountId, Hbar.from(amountInHbar));
  }

  /**
   * Transfers HBAR between accounts. The sending account must sign the transaction.
   *
   * @param fromAccountId the account that sends HBAR
   * @param fromAccountKey the private key of the sending account
   * @param toAccountId the account that receives HBAR
   * @param amount the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  void transferHbar(
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId,
      @NonNull Hbar amount)
      throws HieroException;

  /**
   * Transfers HBAR between accounts. The sending account must sign the transaction.
   *
   * @param fromAccount the account that sends HBAR
   * @param toAccountId the account that receives HBAR
   * @param amount the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  default void transferHbar(
      @NonNull Account fromAccount, @NonNull AccountId toAccountId, @NonNull Hbar amount)
      throws HieroException {
    Objects.requireNonNull(fromAccount, "fromAccount must not be null");
    Objects.requireNonNull(toAccountId, "toAccountId must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    transferHbar(fromAccount.accountId(), fromAccount.privateKey(), toAccountId, amount);
  }

  /**
   * Transfers HBAR between accounts. The sending account must sign the transaction.
   *
   * @param fromAccountId the account that sends HBAR
   * @param fromAccountKey the private key of the sending account
   * @param toAccountId the account that receives HBAR
   * @param amountInHbar the amount of HBAR to transfer
   * @throws HieroException if the transfer could not be executed
   */
  default void transferHbar(
      @NonNull String fromAccountId,
      @NonNull String fromAccountKey,
      @NonNull String toAccountId,
      long amountInHbar)
      throws HieroException {
    Objects.requireNonNull(fromAccountId, "fromAccountId must not be null");
    Objects.requireNonNull(fromAccountKey, "fromAccountKey must not be null");
    Objects.requireNonNull(toAccountId, "toAccountId must not be null");
    if (amountInHbar <= 0) {
      throw new IllegalArgumentException("amountInHbar must be positive");
    }
    transferHbar(
        AccountId.fromString(fromAccountId),
        PrivateKey.fromString(fromAccountKey),
        AccountId.fromString(toAccountId),
        Hbar.from(amountInHbar));
  }

  /**
   * Approves an HBAR allowance so a spender can transfer HBAR on behalf of the owner. The owner
   * account must sign the transaction. Set the amount to {@link Hbar#ZERO} to revoke an existing
   * allowance.
   *
   * @param owner the account that owns the HBAR
   * @param spenderAccountId the account authorized to spend on the owner's behalf
   * @param amount the allowance amount
   * @throws HieroException if the allowance could not be approved
   */
  void approveHbarAllowance(
      @NonNull Account owner, @NonNull AccountId spenderAccountId, @NonNull Hbar amount)
      throws HieroException;

  /**
   * Approves an HBAR allowance so a spender can transfer HBAR on behalf of the owner.
   *
   * @param ownerAccountId the account that owns the HBAR
   * @param ownerPrivateKey the private key of the owner account
   * @param spenderAccountId the account authorized to spend on the owner's behalf
   * @param amount the allowance amount
   * @throws HieroException if the allowance could not be approved
   */
  void approveHbarAllowance(
      @NonNull AccountId ownerAccountId,
      @NonNull PrivateKey ownerPrivateKey,
      @NonNull AccountId spenderAccountId,
      @NonNull Hbar amount)
      throws HieroException;

  /**
   * Approves an HBAR allowance so a spender can transfer HBAR on behalf of the owner.
   *
   * @param owner the account that owns the HBAR
   * @param spenderAccountId the account authorized to spend on the owner's behalf
   * @param amountInHbar the allowance amount in HBAR
   * @throws HieroException if the allowance could not be approved
   */
  default void approveHbarAllowance(
      @NonNull Account owner, @NonNull AccountId spenderAccountId, long amountInHbar)
      throws HieroException {
    if (amountInHbar < 0) {
      throw new IllegalArgumentException("amountInHbar must be non-negative");
    }
    approveHbarAllowance(owner, spenderAccountId, Hbar.from(amountInHbar));
  }

  /**
   * Approves an HBAR allowance so a spender can transfer HBAR on behalf of the owner.
   *
   * @param owner the account that owns the HBAR
   * @param spenderAccountId the account authorized to spend on the owner's behalf
   * @throws HieroException if the allowance could not be approved
   */
  default void approveHbarAllowance(
      @NonNull Account owner, @NonNull String spenderAccountId, long amountInHbar)
      throws HieroException {
    Objects.requireNonNull(spenderAccountId, "spenderAccountId must not be null");
    approveHbarAllowance(owner, AccountId.fromString(spenderAccountId), amountInHbar);
  }

  /**
   * Deletes an HBAR allowance previously granted to a spender by setting the approved amount to
   * zero.
   *
   * @param owner the account that owns the HBAR
   * @param spenderAccountId the account whose allowance is deleted
   * @throws HieroException if the allowance could not be deleted
   */
  default void deleteHbarAllowance(@NonNull Account owner, @NonNull AccountId spenderAccountId)
      throws HieroException {
    approveHbarAllowance(owner, spenderAccountId, Hbar.ZERO);
  }

  /**
   * Revokes an HBAR allowance previously granted to a spender.
   *
   * @param owner the account that owns the HBAR
   * @param spenderAccountId the account whose allowance is revoked
   * @throws HieroException if the allowance could not be revoked
   */
  default void revokeHbarAllowance(@NonNull Account owner, @NonNull AccountId spenderAccountId)
      throws HieroException {
    deleteHbarAllowance(owner, spenderAccountId);
  }

  /**
   * Deletes an NFT allowance for the given serial number. The owner account must sign the
   * transaction.
   *
   * @param owner the account that owns the NFT
   * @param nftId the NFT whose allowance is deleted
   * @throws HieroException if the allowance could not be deleted
   */
  void deleteNftAllowance(@NonNull Account owner, @NonNull NftId nftId) throws HieroException;

  /**
   * Deletes an NFT allowance for the given serial number. The owner account must sign the
   * transaction.
   *
   * @param ownerAccountId the account that owns the NFT
   * @param ownerPrivateKey the private key of the owner account
   * @param nftId the NFT whose allowance is deleted
   * @throws HieroException if the allowance could not be deleted
   */
  void deleteNftAllowance(
      @NonNull AccountId ownerAccountId, @NonNull PrivateKey ownerPrivateKey, @NonNull NftId nftId)
      throws HieroException;

  /**
   * Deletes an NFT allowance for the given serial number. The owner account must sign the
   * transaction.
   *
   * @param owner the account that owns the NFT
   * @param tokenId the token ID of the NFT
   * @param serial the serial number of the NFT
   * @throws HieroException if the allowance could not be deleted
   */
  default void deleteNftAllowance(@NonNull Account owner, @NonNull TokenId tokenId, long serial)
      throws HieroException {
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    if (serial < 0) {
      throw new IllegalArgumentException("serial must be non-negative");
    }
    deleteNftAllowance(owner, new NftId(tokenId, serial));
  }

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
