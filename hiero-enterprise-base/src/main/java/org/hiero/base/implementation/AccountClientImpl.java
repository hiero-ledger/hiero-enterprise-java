package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.AccountUpdateBuilder;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.HieroTransactionRecord;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountInfoRequest;
import org.hiero.base.protocol.data.AccountRecordsRequest;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.AllowanceApproveRequest;
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
  public @NonNull AccountInfo getAccountInfo(@NonNull AccountId accountId) throws HieroException {
    return client.executeAccountInfoQuery(AccountInfoRequest.of(accountId)).accountInfo();
  }

  @Override
  public @NonNull List<HieroTransactionRecord> getAccountRecords(@NonNull AccountId accountId)
      throws HieroException {
    return client.executeAccountRecordsQuery(AccountRecordsRequest.of(accountId)).records();
  }

  @Override
  public void approveHbarAllowance(@NonNull AccountId spenderId, @NonNull Hbar amount)
      throws HieroException {
    client.executeAccountAllowanceApproveTransaction(AllowanceApproveRequest.hbar(spenderId, amount));
  }

  @Override
  public void approveTokenAllowance(
      @NonNull TokenId tokenId, @NonNull AccountId spenderId, long amount) throws HieroException {
    client.executeAccountAllowanceApproveTransaction(
        AllowanceApproveRequest.token(tokenId, spenderId, amount));
  }

  @Override
  public @NonNull AccountUpdateBuilder updateAccount(@NonNull AccountId accountId) {
    return new AccountUpdateBuilderImpl(accountId);
  }

  private class AccountUpdateBuilderImpl implements AccountUpdateBuilder {
    private final AccountId accountId;
    private Key key;
    private String memo;
    private Duration autoRenewPeriod;
    private Boolean receiverSignatureRequired;
    private Integer maxAutomaticTokenAssociations;

    private AccountUpdateBuilderImpl(AccountId accountId) {
      this.accountId = Objects.requireNonNull(accountId);
    }

    @Override
    public @NonNull AccountUpdateBuilder key(@NonNull Key key) {
      this.key = Objects.requireNonNull(key);
      return this;
    }

    @Override
    public @NonNull AccountUpdateBuilder memo(@NonNull String memo) {
      this.memo = Objects.requireNonNull(memo);
      return this;
    }

    @Override
    public @NonNull AccountUpdateBuilder autoRenewPeriod(@NonNull Duration duration) {
      this.autoRenewPeriod = Objects.requireNonNull(duration);
      return this;
    }

    @Override
    public @NonNull AccountUpdateBuilder receiverSignatureRequired(boolean required) {
      this.receiverSignatureRequired = required;
      return this;
    }

    @Override
    public @NonNull AccountUpdateBuilder maxAutomaticTokenAssociations(int max) {
      this.maxAutomaticTokenAssociations = max;
      return this;
    }

    @Override
    public void execute() throws HieroException {
      client.executeAccountUpdateTransaction(
          new AccountUpdateRequest(
              accountId,
              key,
              memo,
              autoRenewPeriod,
              receiverSignatureRequired,
              maxAutomaticTokenAssociations,
              null,
              null,
              null,
              null));
    }
  }
}
