package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.List;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.HookDetails;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountHookUpdateRequest;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.jspecify.annotations.NonNull;

public class AccountClientImpl implements AccountClient {

  private final ProtocolLayerClient client;

  public AccountClientImpl(@NonNull final ProtocolLayerClient client) {
    this.client = Objects.requireNonNull(client, "client must not be null");
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
  public void updateAccountHooks(
      @NonNull final Account account,
      @NonNull final List<HookDetails> hooksToCreate,
      @NonNull final List<Long> hooksToDelete)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    Objects.requireNonNull(hooksToCreate, "hooksToCreate must not be null");
    Objects.requireNonNull(hooksToDelete, "hooksToDelete must not be null");
    final AccountHookUpdateRequest request =
        AccountHookUpdateRequest.of(account, hooksToCreate, hooksToDelete);
    client.executeAccountHookUpdateTransaction(request);
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
}
