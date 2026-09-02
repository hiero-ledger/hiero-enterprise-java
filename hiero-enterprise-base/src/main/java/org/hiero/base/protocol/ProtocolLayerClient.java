package org.hiero.base.protocol;

import com.hedera.hashgraph.sdk.AccountId;
import org.hiero.base.HieroException;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountDeleteResult;
import org.hiero.base.protocol.data.AccountHookUpdateRequest;
import org.hiero.base.protocol.data.AccountHookUpdateResult;
import org.hiero.base.protocol.data.AccountInfoRequest;
import org.hiero.base.protocol.data.AccountInfoResponse;
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
import org.hiero.base.protocol.data.HbarAllowanceApproveRequest;
import org.hiero.base.protocol.data.HbarAllowanceApproveResult;
import org.hiero.base.protocol.data.HbarTransferRequest;
import org.hiero.base.protocol.data.HbarTransferResult;
import org.hiero.base.protocol.data.HookStoreRequest;
import org.hiero.base.protocol.data.HookStoreResult;
import org.hiero.base.protocol.data.NftAllowanceDeleteRequest;
import org.hiero.base.protocol.data.NftAllowanceDeleteResult;
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
import org.hiero.base.protocol.data.TokenFreezeRequest;
import org.hiero.base.protocol.data.TokenFreezeResult;
import org.hiero.base.protocol.data.TokenGrantKycRequest;
import org.hiero.base.protocol.data.TokenGrantKycResult;
import org.hiero.base.protocol.data.TokenMintRequest;
import org.hiero.base.protocol.data.TokenMintResult;
import org.hiero.base.protocol.data.TokenRevokeKycRequest;
import org.hiero.base.protocol.data.TokenRevokeKycResult;
import org.hiero.base.protocol.data.TokenTransferRequest;
import org.hiero.base.protocol.data.TokenTransferResult;
import org.hiero.base.protocol.data.TokenUnfreezeRequest;
import org.hiero.base.protocol.data.TokenUnfreezeResult;
import org.hiero.base.protocol.data.TokenUpdateNftsRequest;
import org.hiero.base.protocol.data.TokenUpdateNftsResult;
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
import org.jspecify.annotations.NonNull;

/** Interface for interacting with a Hiero network at the protocol level. */
public interface ProtocolLayerClient {

  /**
   * Execute an account balance query.
   *
   * @param request the request
   * @return the response
   * @throws HieroException if the query could not be executed
   */
  @NonNull AccountBalanceResponse executeAccountBalanceQuery(@NonNull AccountBalanceRequest request)
      throws HieroException;

  /**
   * Execute an account info query.
   *
   * @param request the request
   * @return the response containing information about the account
   * @throws HieroException if the query could not be executed
   */
  @NonNull AccountInfoResponse executeAccountInfoQuery(@NonNull AccountInfoRequest request)
      throws HieroException;

  /**
   * Execute a file contents query.
   *
   * @param request the request
   * @return the response
   * @throws HieroException if the query could not be executed
   */
  @NonNull FileContentsResponse executeFileContentsQuery(@NonNull FileContentsRequest request)
      throws HieroException;

  /**
   * Execute a file append transaction.
   *
   * @param request the request
   * @return the result
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull FileAppendResult executeFileAppendRequestTransaction(@NonNull FileAppendRequest request)
      throws HieroException;

  /**
   * Execute a file delete transaction.
   *
   * @param request the request
   * @return the result
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull FileDeleteResult executeFileDeleteTransaction(@NonNull FileDeleteRequest request)
      throws HieroException;

  /**
   * Execute a file create transaction.
   *
   * @param request the request
   * @return the result
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull FileCreateResult executeFileCreateTransaction(@NonNull FileCreateRequest request)
      throws HieroException;

  /**
   * Execute a file update transaction.
   *
   * @param request the request containing the details of the file update
   * @return the result of the file update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull FileUpdateResult executeFileUpdateRequestTransaction(@NonNull FileUpdateRequest request)
      throws HieroException;

  /**
   * Execute a file info query.
   *
   * @param request the request containing the details of the file info query
   * @return the response containing the information about the file
   * @throws HieroException if the query could not be executed
   */
  @NonNull FileInfoResponse executeFileInfoQuery(@NonNull FileInfoRequest request)
      throws HieroException;

  /**
   * Execute a contract create transaction.
   *
   * @param request the request
   * @return the result
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull ContractCreateResult executeContractCreateTransaction(
      @NonNull ContractCreateRequest request) throws HieroException;

  /**
   * Execute a contract call transaction.
   *
   * @param request the request
   * @return the result
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull ContractCallResult executeContractCallTransaction(@NonNull ContractCallRequest request)
      throws HieroException;

  /**
   * Executes a contract delete transaction.
   *
   * @param request the request containing the details of the contract delete transaction
   * @return the result of the contract delete transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull ContractDeleteResult executeContractDeleteTransaction(
      @NonNull final ContractDeleteRequest request) throws HieroException;

  /**
   * Executes an account create transaction.
   *
   * @param request the request containing the details of the account create transaction
   * @return the result of the account create transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull AccountCreateResult executeAccountCreateTransaction(
      @NonNull final AccountCreateRequest request) throws HieroException;

  /**
   * Executes an account delete transaction.
   *
   * @param request the request containing the details of the account delete transaction
   * @return the result of the account delete transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull AccountDeleteResult executeAccountDeleteTransaction(
      @NonNull AccountDeleteRequest request) throws HieroException;

  /**
   * Executes an account hook update transaction.
   *
   * @param request the request containing hooks to create and hooks to delete on an account
   * @return the result of the account hook update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull
  default AccountHookUpdateResult executeAccountHookUpdateTransaction(
      @NonNull AccountHookUpdateRequest request) throws HieroException {
    throw new UnsupportedOperationException("Account hook update transaction is not implemented.");
  }

  /**
   * Executes an account update transaction.
   *
   * @param request the request containing the details of the account update transaction
   * @return the result of the account update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull AccountUpdateResult executeAccountUpdateTransaction(
      @NonNull AccountUpdateRequest request) throws HieroException;

  /**
   * Executes a token create transaction.
   *
   * @param request the request containing the details of the token create transaction
   * @return the result of the token create transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenCreateResult executeTokenCreateTransaction(
      @NonNull final TokenCreateRequest request) throws HieroException;

  /**
   * Executes a token delete transaction.
   *
   * @param request the request containing the details of the token delete transaction
   * @return the result of the token delete transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenDeleteResult executeTokenDeleteTransaction(
      @NonNull final TokenDeleteRequest request) throws HieroException;

  /**
   * Executes a token update transaction.
   *
   * @param request the request containing the details of the token update transaction
   * @return the result of the token update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenUpdateResult executeTokenUpdateTransaction(
      @NonNull final TokenUpdateRequest request) throws HieroException;

  /**
   * Executes a token update NFTs transaction (updates metadata for NFT serials).
   *
   * @param request the request containing the details of the NFT metadata update
   * @return the result of the NFT metadata update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenUpdateNftsResult executeTokenUpdateNftsTransaction(
      @NonNull final TokenUpdateNftsRequest request) throws HieroException;

  /**
   * Executes a token associate transaction.
   *
   * @param request the request containing the details of the token associate transaction
   * @return the result of the token associate transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenAssociateResult executeTokenAssociateTransaction(
      @NonNull final TokenAssociateRequest request) throws HieroException;

  /**
   * Executes a token dissociate transaction.
   *
   * @param request the request containing the details of the token dissociate transaction
   * @return the result of the token dissociate transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenDissociateResult executeTokenDissociateTransaction(
      @NonNull final TokenDissociateRequest request) throws HieroException;

  /**
   * Executes a token freeze transaction.
   *
   * @param request the request containing the details of the token freeze transaction
   * @return the result of the token freeze transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenFreezeResult executeTokenFreezeTransaction(
      @NonNull final TokenFreezeRequest request) throws HieroException;

  /**
   * Executes a token unfreeze transaction.
   *
   * @param request the request containing the details of the token unfreeze transaction
   * @return the result of the token unfreeze transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenUnfreezeResult executeTokenUnfreezeTransaction(
      @NonNull final TokenUnfreezeRequest request) throws HieroException;

  /**
   * Executes a token grant KYC transaction.
   *
   * @param request the request containing the details of the token grant KYC transaction
   * @return the result of the token grant KYC transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenGrantKycResult executeTokenGrantKycTransaction(
      @NonNull final TokenGrantKycRequest request) throws HieroException;

  /**
   * Executes a token revoke KYC transaction.
   *
   * @param request the request containing the details of the token revoke KYC transaction
   * @return the result of the token revoke KYC transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenRevokeKycResult executeTokenRevokeKycTransaction(
      @NonNull final TokenRevokeKycRequest request) throws HieroException;

  /**
   * Executes a token mint transaction.
   *
   * @param request the request containing the details of the token mint transaction
   * @return the result of the token mint transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenMintResult executeMintTokenTransaction(@NonNull final TokenMintRequest request)
      throws HieroException;

  /**
   * Executes a token burn transaction.
   *
   * @param request the request containing the details of the token burn transaction
   * @return the result of the token burn transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenBurnResult executeBurnTokenTransaction(@NonNull final TokenBurnRequest request)
      throws HieroException;

  /**
   * Executes a token wipe transaction (removes NFT serials from a non-treasury account).
   *
   * @param request the request containing the details of the wipe transaction
   * @return the result of the wipe transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenWipeResult executeWipeTokenTransaction(@NonNull final TokenWipeRequest request)
      throws HieroException;

  /**
   * Executes a transfer transaction for an NFT.
   *
   * @param request the request containing the details of the token transfer transaction
   * @return the result of the token transfer transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TokenTransferResult executeTransferTransaction(
      @NonNull final TokenTransferRequest request) throws HieroException;

  /**
   * Executes an HBAR transfer transaction.
   *
   * @param request the request containing the details of the HBAR transfer transaction
   * @return the result of the HBAR transfer transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull HbarTransferResult executeHbarTransferTransaction(
      @NonNull final HbarTransferRequest request) throws HieroException;

  /**
   * Executes an HBAR allowance approve transaction.
   *
   * @param request the request containing the details of the allowance approve transaction
   * @return the result of the allowance approve transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull HbarAllowanceApproveResult executeHbarAllowanceApproveTransaction(
      @NonNull final HbarAllowanceApproveRequest request) throws HieroException;

  /**
   * Executes an NFT allowance delete transaction.
   *
   * @param request the request containing the details of the NFT allowance delete transaction
   * @return the result of the NFT allowance delete transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull NftAllowanceDeleteResult executeNftAllowanceDeleteTransaction(
      @NonNull final NftAllowanceDeleteRequest request) throws HieroException;

  /**
   * Executes a hook store transaction.
   *
   * @param request the request containing the details of the hook store transaction
   * @return the result of the hook store transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull HookStoreResult executeHookStoreTransaction(@NonNull final HookStoreRequest request)
      throws HieroException;

  /**
   * Executes a topic create transaction.
   *
   * @param request the request containing the details of the topic create transaction
   * @return the result of the topic create transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TopicCreateResult executeTopicCreateTransaction(@NonNull TopicCreateRequest request)
      throws HieroException;

  /**
   * Executes a topic update transaction.
   *
   * @param request the request containing the details of the topic update transaction
   * @return the result of the topic update transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TopicUpdateResult executeTopicUpdateTransaction(@NonNull TopicUpdateRequest request)
      throws HieroException;

  /**
   * Executes a topic delete transaction.
   *
   * @param request the request containing the details of the topic delete transaction
   * @return the result of the topic delete transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TopicDeleteResult executeTopicDeleteTransaction(@NonNull TopicDeleteRequest request)
      throws HieroException;

  /**
   * Executes a topic message submit transaction.
   *
   * @param request the request containing the details of the topic message submit transaction
   * @return the result of the topic message submit transaction
   * @throws HieroException if the transaction could not be executed
   */
  @NonNull TopicSubmitMessageResult executeTopicMessageSubmitTransaction(
      @NonNull TopicSubmitMessageRequest request) throws HieroException;

  /**
   * Executes a topic message query.
   *
   * @param request the request containing the details of the topic message query
   * @return the result of the topic message query
   * @throws HieroException if the query could not be executed
   */
  @NonNull TopicMessageResult executeTopicMessageQuery(@NonNull TopicMessageRequest request)
      throws HieroException;

  /**
   * Adds a transaction listener to the protocol layer client. The listener will be notified when a
   * transaction is executed.
   *
   * @param listener the transaction listener to be added
   * @return a Runnable object that can be used to remove the listener
   */
  @NonNull Runnable addTransactionListener(@NonNull TransactionListener listener);

  /**
   * Returns the account ID of the operator account.
   *
   * @return the account ID of the operator account
   */
  @NonNull AccountId getOperatorAccountId();
}
