package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenBurnTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenDeleteTransaction;
import com.hedera.hashgraph.sdk.TokenDissociateTransaction;
import com.hedera.hashgraph.sdk.TokenFeeScheduleUpdateTransaction;
import com.hedera.hashgraph.sdk.TokenFreezeTransaction;
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenPauseTransaction;
import com.hedera.hashgraph.sdk.TokenRevokeKycTransaction;
import com.hedera.hashgraph.sdk.TokenUnfreezeTransaction;
import com.hedera.hashgraph.sdk.TokenUnpauseTransaction;
import com.hedera.hashgraph.sdk.TokenUpdateTransaction;
import com.hedera.hashgraph.sdk.TokenWipeTransaction;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicDeleteTransaction;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.TopicUpdateTransaction;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.TransactionListener;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountDeleteResult;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.AccountUpdateResult;
import org.hiero.base.protocol.data.ContractCallRequest;
import org.hiero.base.protocol.data.ContractCallResult;
import org.hiero.base.protocol.data.ContractCreateRequest;
import org.hiero.base.protocol.data.ContractCreateResult;
import org.hiero.base.protocol.data.ContractDeleteRequest;
import org.hiero.base.protocol.data.ContractDeleteResult;
import org.hiero.base.protocol.data.FileAppendRequest;
import org.hiero.base.protocol.data.FileAppendResult;
import org.hiero.base.protocol.data.FileContentsRequest;
import org.hiero.base.protocol.data.FileContentsResponse;
import org.hiero.base.protocol.data.FileCreateRequest;
import org.hiero.base.protocol.data.FileCreateResult;
import org.hiero.base.protocol.data.FileDeleteRequest;
import org.hiero.base.protocol.data.FileDeleteResult;
import org.hiero.base.protocol.data.FileInfoRequest;
import org.hiero.base.protocol.data.FileInfoResponse;
import org.hiero.base.protocol.data.FileUpdateRequest;
import org.hiero.base.protocol.data.FileUpdateResult;
import org.hiero.base.protocol.data.TokenAssociateRequest;
import org.hiero.base.protocol.data.TokenAssociateResult;
import org.hiero.base.protocol.data.TokenBurnRequest;
import org.hiero.base.protocol.data.TokenBurnResult;
import org.hiero.base.protocol.data.TokenCreateRequest;
import org.hiero.base.protocol.data.TokenCreateResult;
import org.hiero.base.protocol.data.TokenDeleteRequest;
import org.hiero.base.protocol.data.TokenDeleteResult;
import org.hiero.base.protocol.data.TokenDissociateRequest;
import org.hiero.base.protocol.data.TokenDissociateResult;
import org.hiero.base.protocol.data.TokenFeeScheduleUpdateRequest;
import org.hiero.base.protocol.data.TokenFeeScheduleUpdateResult;
import org.hiero.base.protocol.data.TokenFreezeRequest;
import org.hiero.base.protocol.data.TokenFreezeResult;
import org.hiero.base.protocol.data.TokenGrantKycRequest;
import org.hiero.base.protocol.data.TokenGrantKycResult;
import org.hiero.base.protocol.data.TokenMintRequest;
import org.hiero.base.protocol.data.TokenMintResult;
import org.hiero.base.protocol.data.TokenPauseRequest;
import org.hiero.base.protocol.data.TokenPauseResult;
import org.hiero.base.protocol.data.TokenRevokeKycRequest;
import org.hiero.base.protocol.data.TokenRevokeKycResult;
import org.hiero.base.protocol.data.TokenTransferRequest;
import org.hiero.base.protocol.data.TokenTransferResult;
import org.hiero.base.protocol.data.TokenUnfreezeRequest;
import org.hiero.base.protocol.data.TokenUnfreezeResult;
import org.hiero.base.protocol.data.TokenUnpauseRequest;
import org.hiero.base.protocol.data.TokenUnpauseResult;
import org.hiero.base.protocol.data.TokenUpdateRequest;
import org.hiero.base.protocol.data.TokenUpdateResult;
import org.hiero.base.protocol.data.TokenWipeRequest;
import org.hiero.base.protocol.data.TokenWipeResult;
import org.hiero.base.protocol.data.TopicCreateRequest;
import org.hiero.base.protocol.data.TopicCreateResult;
import org.hiero.base.protocol.data.TopicDeleteRequest;
import org.hiero.base.protocol.data.TopicDeleteResult;
import org.hiero.base.protocol.data.TopicMessageRequest;
import org.hiero.base.protocol.data.TopicMessageResult;
import org.hiero.base.protocol.data.TopicSubmitMessageRequest;
import org.hiero.base.protocol.data.TopicSubmitMessageResult;
import org.hiero.base.protocol.data.TopicUpdateRequest;
import org.hiero.base.protocol.data.TopicUpdateResult;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ProtocolLayerClientImpl implements ProtocolLayerClient {

  private static final Logger logger = LoggerFactory.getLogger(ProtocolLayerClientImpl.class);

  private final com.hedera.hashgraph.sdk.Client client;

  private final Collection<TransactionListener> transactionListeners = new CopyOnWriteArrayList<>();

  private org.hiero.base.interceptors.ReceiveRecordInterceptor recordInterceptor;

  public ProtocolLayerClientImpl(@NonNull final HieroContext hieroContext) {
    Objects.requireNonNull(hieroContext, "hieroContext must not be null");
    this.client = hieroContext.getClient();
  }

  @Override
  @NonNull
  public AccountBalanceResponse executeAccountBalanceQuery(
      @NonNull final AccountBalanceRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    final com.hedera.hashgraph.sdk.AccountBalanceQuery query =
        new com.hedera.hashgraph.sdk.AccountBalanceQuery()
            .setAccountId(request.accountId())
            .setQueryPayment(request.queryPayment())
            .setMaxQueryPayment(request.maxQueryPayment());
    final com.hedera.hashgraph.sdk.AccountBalance balance =
        (com.hedera.hashgraph.sdk.AccountBalance) executeQueryAndWait(query);
    return new AccountBalanceResponse(balance.hbars);
  }

  @Override
  @NonNull
  public FileContentsResponse executeFileContentsQuery(@NonNull final FileContentsRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    final com.hedera.hashgraph.sdk.FileContentsQuery query =
        new com.hedera.hashgraph.sdk.FileContentsQuery()
            .setFileId(request.fileId())
            .setQueryPayment(request.queryPayment())
            .setMaxQueryPayment(request.maxQueryPayment());
    final Object result = executeQueryAndWait(query);
    return new FileContentsResponse(request.fileId(), getBytes(result));
  }

  @Override
  @NonNull
  public FileAppendResult executeFileAppendRequestTransaction(
      @NonNull final FileAppendRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.FileAppendTransaction transaction =
          new com.hedera.hashgraph.sdk.FileAppendTransaction()
              .setFileId(request.fileId())
              .setContents(request.contents())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.FILE_APPEND);
      return new FileAppendResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute file append transaction", e);
    }
  }

  @Override
  @NonNull
  public FileDeleteResult executeFileDeleteTransaction(@NonNull final FileDeleteRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.FileDeleteTransaction transaction =
          new com.hedera.hashgraph.sdk.FileDeleteTransaction()
              .setFileId(request.fileId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.FILE_DELETE);
      return new FileDeleteResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute file delete transaction", e);
    }
  }

  @Override
  @NonNull
  public FileCreateResult executeFileCreateTransaction(@NonNull final FileCreateRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.FileCreateTransaction transaction =
          new com.hedera.hashgraph.sdk.FileCreateTransaction()
              .setContents(request.contents())
              .setFileMemo(request.fileMemo())
              .setExpirationTime(request.expirationTime())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.FILE_CREATE);
      return new FileCreateResult(
          record.transactionId, record.receipt.status, record.receipt.fileId);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute file create transaction", e);
    }
  }

  @Override
  @NonNull
  public FileUpdateResult executeFileUpdateRequestTransaction(
      @NonNull final FileUpdateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.FileUpdateTransaction transaction =
          new com.hedera.hashgraph.sdk.FileUpdateTransaction()
              .setFileId(request.fileId())
              .setContents(request.contents())
              .setFileMemo(request.fileMemo())
              .setExpirationTime(request.expirationTime())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.FILE_UPDATE);
      return new FileUpdateResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute file update transaction", e);
    }
  }

  @Override
  @NonNull
  public FileInfoResponse executeFileInfoQuery(@NonNull final FileInfoRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    final com.hedera.hashgraph.sdk.FileInfoQuery query =
        new com.hedera.hashgraph.sdk.FileInfoQuery()
            .setFileId(request.fileId())
            .setQueryPayment(request.queryPayment())
            .setMaxQueryPayment(request.maxQueryPayment());
    final com.hedera.hashgraph.sdk.FileInfo info =
        (com.hedera.hashgraph.sdk.FileInfo) executeQueryAndWait(query);
    return new FileInfoResponse(info.fileId, (int) info.size, info.isDeleted, info.expirationTime);
  }

  @Override
  @NonNull
  public ContractCreateResult executeContractCreateTransaction(
      @NonNull final ContractCreateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    throw new UnsupportedOperationException(
        "Contract operations are not fully supported in this baseline");
  }

  @Override
  @NonNull
  public ContractCallResult executeContractCallTransaction(
      @NonNull final ContractCallRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    throw new UnsupportedOperationException(
        "Contract operations are not fully supported in this baseline");
  }

  @Override
  @NonNull
  public ContractDeleteResult executeContractDeleteTransaction(
      @NonNull final ContractDeleteRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.ContractDeleteTransaction transaction =
          new com.hedera.hashgraph.sdk.ContractDeleteTransaction()
              .setContractId(request.contractId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.CONTRACT_DELETE);
      return new ContractDeleteResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute contract delete transaction", e);
    }
  }

  @Override
  @NonNull
  public AccountCreateResult executeAccountCreateTransaction(
      @NonNull final AccountCreateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final PrivateKey newKey = PrivateKey.generateED25519();
      final com.hedera.hashgraph.sdk.AccountCreateTransaction transaction =
          new com.hedera.hashgraph.sdk.AccountCreateTransaction()
              .setKey(newKey.getPublicKey())
              .setInitialBalance(request.initialBalance())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.ACCOUNT_CREATE);
      return new AccountCreateResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee,
          Account.of(record.receipt.accountId, newKey));
    } catch (final Exception e) {
      throw new HieroException("Failed to execute account create transaction", e);
    }
  }

  @Override
  @NonNull
  public AccountDeleteResult executeAccountDeleteTransaction(
      @NonNull final AccountDeleteRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.AccountDeleteTransaction transaction =
          new com.hedera.hashgraph.sdk.AccountDeleteTransaction()
              .setAccountId(request.toDelete().accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      if (request.transferFundsToAccount() != null) {
        transaction.setTransferAccountId(request.transferFundsToAccount().accountId());
      } else {
        transaction.setTransferAccountId(client.getOperatorAccountId());
      }

      transaction.freezeWith(client);
      transaction.sign(request.toDelete().privateKey());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.ACCOUNT_DELETE);
      return new AccountDeleteResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute account delete transaction", e);
    }
  }

  @Override
  @NonNull
  public AccountUpdateResult executeAccountUpdateTransaction(
      @NonNull final AccountUpdateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final com.hedera.hashgraph.sdk.AccountUpdateTransaction transaction =
          new com.hedera.hashgraph.sdk.AccountUpdateTransaction()
              .setAccountId(request.toUpdate().accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      if (request.updatedPrivateKey() != null) {
        transaction.setKey(request.updatedPrivateKey().getPublicKey());
      }
      if (request.memo() != null) {
        transaction.setAccountMemo(request.memo());
      }

      transaction.freezeWith(client);
      transaction.sign(request.toUpdate().privateKey());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.ACCOUNT_UPDATE);
      return new AccountUpdateResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute account update transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenCreateResult executeTokenCreateTransaction(@NonNull final TokenCreateRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenCreateTransaction transaction =
          new TokenCreateTransaction()
              .setTokenName(request.name())
              .setTokenSymbol(request.symbol())
              .setTreasuryAccountId(request.treasuryAccountId())
              .setTokenType(request.tokenType())
              .setSupplyKey(request.supplyKey())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      transaction.freezeWith(client);
      transaction.sign(request.treasuryKey());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_CREATE);
      return new TokenCreateResult(
          record.transactionId, record.receipt.status, record.receipt.tokenId);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token create transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenUpdateResult executeTokenUpdateTransaction(@NonNull final TokenUpdateRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenUpdateTransaction transaction =
          new TokenUpdateTransaction()
              .setTokenId(request.tokenId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_UPDATE);
      return new TokenUpdateResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token update transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenDeleteResult executeTokenDeleteTransaction(@NonNull final TokenDeleteRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenDeleteTransaction transaction =
          new TokenDeleteTransaction()
              .setTokenId(request.tokenId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_DELETE);
      return new TokenDeleteResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token delete transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenPauseResult executeTokenPauseTransaction(@NonNull final TokenPauseRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenPauseTransaction transaction =
          new TokenPauseTransaction()
              .setTokenId(request.tokenId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_PAUSE);
      return new TokenPauseResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token pause transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenUnpauseResult executeTokenUnpauseTransaction(
      @NonNull final TokenUnpauseRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenUnpauseTransaction transaction =
          new TokenUnpauseTransaction()
              .setTokenId(request.tokenId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_UNPAUSE);
      return new TokenUnpauseResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token unpause transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenFreezeResult executeTokenFreezeTransaction(@NonNull final TokenFreezeRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenFreezeTransaction transaction =
          new TokenFreezeTransaction()
              .setTokenId(request.tokenId())
              .setAccountId(request.accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_FREEZE);
      return new TokenFreezeResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token freeze transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenUnfreezeResult executeTokenUnfreezeTransaction(
      @NonNull final TokenUnfreezeRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenUnfreezeTransaction transaction =
          new TokenUnfreezeTransaction()
              .setTokenId(request.tokenId())
              .setAccountId(request.accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_UNFREEZE);
      return new TokenUnfreezeResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token unfreeze transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenGrantKycResult executeTokenGrantKycTransaction(
      @NonNull final TokenGrantKycRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenGrantKycTransaction transaction =
          new TokenGrantKycTransaction()
              .setTokenId(request.tokenId())
              .setAccountId(request.accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_GRANT_KYC);
      return new TokenGrantKycResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token grant KYC transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenRevokeKycResult executeTokenRevokeKycTransaction(
      @NonNull final TokenRevokeKycRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenRevokeKycTransaction transaction =
          new TokenRevokeKycTransaction()
              .setTokenId(request.tokenId())
              .setAccountId(request.accountId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_REVOKE_KYC);
      return new TokenRevokeKycResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token revoke KYC transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenWipeResult executeTokenWipeTransaction(@NonNull final TokenWipeRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenWipeTransaction transaction =
          new TokenWipeTransaction()
              .setTokenId(request.tokenId())
              .setAccountId(request.accountId())
              .setAmount(request.amount())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_WIPE);
      return new TokenWipeResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token wipe transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenFeeScheduleUpdateResult executeTokenFeeScheduleUpdateTransaction(
      @NonNull final TokenFeeScheduleUpdateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenFeeScheduleUpdateTransaction transaction =
          new TokenFeeScheduleUpdateTransaction()
              .setTokenId(request.tokenId())
              .setCustomFees(request.customFees())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_FEE_SCHEDULE_UPDATE);
      return new TokenFeeScheduleUpdateResult(
          record.transactionId,
          record.receipt.status,
          getBytes(record.transactionHash),
          record.consensusTimestamp,
          record.transactionFee);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute fee schedule update transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenAssociateResult executeTokenAssociateTransaction(
      @NonNull final TokenAssociateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenAssociateTransaction transaction =
          new TokenAssociateTransaction()
              .setAccountId(request.accountId())
              .setTokenIds(request.tokenIds())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_ASSOCIATE);
      return new TokenAssociateResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token associate transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenDissociateResult executeTokenDissociateTransaction(
      @NonNull final TokenDissociateRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenDissociateTransaction transaction =
          new TokenDissociateTransaction()
              .setAccountId(request.accountId())
              .setTokenIds(request.tokenIds())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_DISSOCIATE);
      return new TokenDissociateResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token dissociate transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenMintResult executeMintTokenTransaction(@NonNull final TokenMintRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenMintTransaction transaction =
          new TokenMintTransaction()
              .setTokenId(request.tokenId())
              .setAmount(request.amount() != null ? request.amount() : 0)
              .setMetadata(request.metadata())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_MINT);
      return new TokenMintResult(
          record.transactionId,
          record.receipt.status,
          record.receipt.serials,
          record.receipt.totalSupply);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token mint transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenBurnResult executeBurnTokenTransaction(@NonNull final TokenBurnRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TokenBurnTransaction transaction =
          new TokenBurnTransaction()
              .setTokenId(request.tokenId())
              .setAmount(request.amount() != null ? request.amount() : 0)
              .setSerials(
                  request.serials() != null ? new ArrayList<>(request.serials()) : List.of())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOKEN_BURN);
      return new TokenBurnResult(
          record.transactionId, record.receipt.status, record.receipt.totalSupply);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token burn transaction", e);
    }
  }

  @Override
  @NonNull
  public TokenTransferResult executeTransferTransaction(@NonNull final TokenTransferRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TransferTransaction transaction =
          new TransferTransaction()
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      if (request.amount() == null) {
        for (Long serial : request.serials()) {
          transaction.addNftTransfer(
              request.tokenId().nft(serial), request.sender(), request.receiver());
        }
      } else {
        transaction.addTokenTransfer(request.tokenId(), request.sender(), -request.amount());
        transaction.addTokenTransfer(request.tokenId(), request.receiver(), request.amount());
      }

      transaction.freezeWith(client);
      transaction.sign(request.senderKey());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.CRYPTO_TRANSFER);
      return new TokenTransferResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute token transfer transaction", e);
    }
  }

  @Override
  @NonNull
  public TopicCreateResult executeTopicCreateTransaction(@NonNull final TopicCreateRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TopicCreateTransaction transaction =
          new TopicCreateTransaction()
              .setAdminKey(request.adminKey())
              .setSubmitKey(request.submitKey())
              .setTopicMemo(request.memo())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOPIC_CREATE);
      return new TopicCreateResult(
          record.transactionId, record.receipt.status, record.receipt.topicId);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute topic create transaction", e);
    }
  }

  @Override
  @NonNull
  public TopicUpdateResult executeTopicUpdateTransaction(@NonNull final TopicUpdateRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TopicUpdateTransaction transaction =
          new TopicUpdateTransaction()
              .setTopicId(request.topicId())
              .setAdminKey(request.adminKey())
              .setSubmitKey(request.submitKey())
              .setTopicMemo(request.memo())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOPIC_UPDATE);
      return new TopicUpdateResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute topic update transaction", e);
    }
  }

  @Override
  @NonNull
  public TopicDeleteResult executeTopicDeleteTransaction(@NonNull final TopicDeleteRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TopicDeleteTransaction transaction =
          new TopicDeleteTransaction()
              .setTopicId(request.topicId())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOPIC_DELETE);
      return new TopicDeleteResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute topic delete transaction", e);
    }
  }

  @Override
  @NonNull
  public TopicSubmitMessageResult executeTopicMessageSubmitTransaction(
      @NonNull final TopicSubmitMessageRequest request) throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    try {
      final TopicMessageSubmitTransaction transaction =
          new TopicMessageSubmitTransaction()
              .setTopicId(request.topicId())
              .setMessage(request.message())
              .setMaxTransactionFee(request.maxTransactionFee())
              .setTransactionValidDuration(request.transactionValidDuration());

      final TransactionRecord record =
          executeTransactionAndWaitOnRecord(transaction, TransactionType.TOPIC_MESSAGE_SUBMIT);
      return new TopicSubmitMessageResult(record.transactionId, record.receipt.status);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute topic message submit transaction", e);
    }
  }

  @Override
  @NonNull
  public TopicMessageResult executeTopicMessageQuery(@NonNull final TopicMessageRequest request)
      throws HieroException {
    Objects.requireNonNull(request, "request must not be null");
    return new TopicMessageResult();
  }

  @Override
  @NonNull
  public Runnable addTransactionListener(@NonNull final TransactionListener listener) {
    Objects.requireNonNull(listener, "listener must not be null");
    transactionListeners.add(listener);
    return () -> transactionListeners.remove(listener);
  }

  public void setRecordInterceptor(
      final org.hiero.base.interceptors.ReceiveRecordInterceptor recordInterceptor) {
    this.recordInterceptor = recordInterceptor;
  }

  @Override
  @NonNull
  public AccountId getOperatorAccountId() {
    return client.getOperatorAccountId();
  }

  private Object executeQueryAndWait(@NonNull final Object query) throws HieroException {
    try {
      return query
          .getClass()
          .getMethod("execute", com.hedera.hashgraph.sdk.Client.class)
          .invoke(query, client);
    } catch (final Exception e) {
      throw new HieroException("Failed to execute query", e);
    }
  }

  private TransactionRecord executeTransactionAndWaitOnRecord(
      @NonNull final Object transactionObj, @NonNull final TransactionType transactionType)
      throws HieroException {
    try {
      final com.hedera.hashgraph.sdk.Transaction transaction =
          (com.hedera.hashgraph.sdk.Transaction) transactionObj;
      final TransactionResponse response = (TransactionResponse) transaction.execute(client);

      transactionListeners.forEach(
          listener -> listener.transactionSubmitted(transactionType, response.transactionId));

      final TransactionRecord record;
      if (recordInterceptor != null) {
        record =
            recordInterceptor.getRecordFor(
                new org.hiero.base.interceptors.ReceiveRecordInterceptor.ReceiveRecordHandler(
                    transaction,
                    response.getReceipt(client),
                    receipt -> response.getRecord(client)));
      } else {
        record = response.getRecord(client);
      }

      transactionListeners.forEach(
          listener ->
              listener.transactionHandled(
                  transactionType, record.transactionId, record.receipt.status));
      return record;
    } catch (final Exception e) {
      throw new HieroException("Failed to execute transaction via reflection", e);
    }
  }

  private byte[] getBytes(Object obj) {
    if (obj == null) return null;
    try {
      return (byte[]) obj.getClass().getMethod("toByteArray").invoke(obj);
    } catch (Exception e) {
      logger.error("Failed to convert object to byte array via reflection", e);
      return null;
    }
  }
}
