package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.HbarAllowanceApproveRequest;
import org.hiero.base.protocol.data.HbarTransferRequest;
import org.hiero.base.protocol.data.NftAllowanceDeleteRequest;
import org.jspecify.annotations.NonNull;

public class AccountClientImpl implements AccountClient {

  private final ProtocolLayerClient client;

  private final Account operatorAccount;

  public AccountClientImpl(
      @NonNull final ProtocolLayerClient client, @NonNull final Account operatorAccount) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.operatorAccount =
        Objects.requireNonNull(operatorAccount, "operatorAccount must not be null");
  }

  @NonNull
  @Override
  public Account createAccount(@NonNull Hbar initialBalance) throws HieroException {
    if (initialBalance == null) {
      throw new NullPointerException("initialBalance must not be null");
    }

    if (initialBalance.toTinybars() < 0) {
      throw new HieroException("Invalid initial balance: must be non-negative");
    }

    try {
      final AccountCreateRequest request = AccountCreateRequest.of(initialBalance);
      final AccountCreateResult result = client.executeAccountCreateTransaction(request);
      return result.newAccount();
    } catch (IllegalArgumentException e) {
      throw new HieroException("Error while creating Account", e);
    }
  }

  @Override
  public void deleteAccount(@NonNull Account account) throws HieroException {
    final AccountDeleteRequest request = AccountDeleteRequest.of(account);
    client.executeAccountDeleteTransaction(request);
  }

  @Override
  public void deleteAccount(@NonNull Account account, @NonNull Account toAccount)
      throws HieroException {
    final AccountDeleteRequest request = AccountDeleteRequest.of(account, toAccount);
    client.executeAccountDeleteTransaction(request);
  }

  @Override
  public @NonNull Account updateAccountKey(
      @NonNull Account account, @NonNull PrivateKey updatedPrivateKey) throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey must not be null");
    final AccountUpdateRequest request = AccountUpdateRequest.updateKey(account, updatedPrivateKey);
    client.executeAccountUpdateTransaction(request);
    return Account.of(account.accountId(), updatedPrivateKey);
  }

  @Override
  public void updateAccountMemo(@NonNull Account account, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    final AccountUpdateRequest request = AccountUpdateRequest.updateMemo(account, memo);
    client.executeAccountUpdateTransaction(request);
  }

  @Override
  public @NonNull Account updateAccount(
      @NonNull Account account, @NonNull PrivateKey updatedPrivateKey, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    final AccountUpdateRequest request = AccountUpdateRequest.of(account, updatedPrivateKey, memo);
    client.executeAccountUpdateTransaction(request);
    return Account.of(account.accountId(), updatedPrivateKey);
  }

  @NonNull
  @Override
  public Hbar getAccountBalance(@NonNull AccountId account) throws HieroException {
    final AccountBalanceRequest request = AccountBalanceRequest.of(account);
    final AccountBalanceResponse response = client.executeAccountBalanceQuery(request);
    return response.hbars();
  }

  @Override
  public @NonNull Hbar getOperatorAccountBalance() throws HieroException {
    return getAccountBalance(client.getOperatorAccountId());
  }

  @Override
  public void transferHbar(@NonNull AccountId toAccountId, @NonNull Hbar amount)
      throws HieroException {
    transferHbar(operatorAccount.accountId(), operatorAccount.privateKey(), toAccountId, amount);
  }

  @Override
  public void transferHbar(
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId,
      @NonNull Hbar amount)
      throws HieroException {
    Objects.requireNonNull(fromAccountId, "fromAccountId must not be null");
    Objects.requireNonNull(fromAccountKey, "fromAccountKey must not be null");
    Objects.requireNonNull(toAccountId, "toAccountId must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    if (amount.toTinybars() <= 0) {
      throw new HieroException("Invalid transfer amount: must be positive");
    }
    try {
      final HbarTransferRequest request =
          HbarTransferRequest.of(fromAccountId, toAccountId, amount, fromAccountKey);
      client.executeHbarTransferTransaction(request);
    } catch (IllegalArgumentException e) {
      throw new HieroException("Error while transferring HBAR", e);
    }
  }

  @Override
  public void approveHbarAllowance(
      @NonNull Account owner, @NonNull AccountId spenderAccountId, @NonNull Hbar amount)
      throws HieroException {
    Objects.requireNonNull(owner, "owner must not be null");
    approveHbarAllowance(owner.accountId(), owner.privateKey(), spenderAccountId, amount);
  }

  @Override
  public void approveHbarAllowance(
      @NonNull AccountId ownerAccountId,
      @NonNull PrivateKey ownerPrivateKey,
      @NonNull AccountId spenderAccountId,
      @NonNull Hbar amount)
      throws HieroException {
    Objects.requireNonNull(ownerAccountId, "ownerAccountId must not be null");
    Objects.requireNonNull(ownerPrivateKey, "ownerPrivateKey must not be null");
    Objects.requireNonNull(spenderAccountId, "spenderAccountId must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    if (amount.toTinybars() < 0) {
      throw new HieroException("Invalid allowance amount: must be non-negative");
    }
    try {
      final HbarAllowanceApproveRequest request =
          HbarAllowanceApproveRequest.of(ownerAccountId, spenderAccountId, amount, ownerPrivateKey);
      client.executeHbarAllowanceApproveTransaction(request);
    } catch (IllegalArgumentException e) {
      throw new HieroException("Error while approving HBAR allowance", e);
    }
  }

  @Override
  public void deleteNftAllowance(@NonNull Account owner, @NonNull NftId nftId)
      throws HieroException {
    Objects.requireNonNull(owner, "owner must not be null");
    deleteNftAllowance(owner.accountId(), owner.privateKey(), nftId);
  }

  @Override
  public void deleteNftAllowance(
      @NonNull AccountId ownerAccountId, @NonNull PrivateKey ownerPrivateKey, @NonNull NftId nftId)
      throws HieroException {
    Objects.requireNonNull(ownerAccountId, "ownerAccountId must not be null");
    Objects.requireNonNull(ownerPrivateKey, "ownerPrivateKey must not be null");
    Objects.requireNonNull(nftId, "nftId must not be null");
    try {
      final NftAllowanceDeleteRequest request =
          NftAllowanceDeleteRequest.of(ownerAccountId, nftId, ownerPrivateKey);
      client.executeNftAllowanceDeleteTransaction(request);
    } catch (IllegalArgumentException e) {
      throw new HieroException("Error while deleting NFT allowance", e);
    }
  }
}
