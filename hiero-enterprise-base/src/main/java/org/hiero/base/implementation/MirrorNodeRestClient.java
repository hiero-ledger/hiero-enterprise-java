package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TopicId;
import java.util.Objects;
import org.hiero.base.HieroBaseException;
import org.jspecify.annotations.NonNull;

public interface MirrorNodeRestClient<JSON> {

  @NonNull
  default JSON queryNftsByTokenIdAndSerial(
      @NonNull final TokenId tokenId, @NonNull final long serialNumber) throws HieroBaseException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    if (serialNumber <= 0) {
      throw new IllegalArgumentException("serialNumber must be positive");
    }
    return doGetCall("/api/v1/tokens/" + tokenId + "/nfts/" + serialNumber);
  }

  @NonNull
  default JSON queryTransaction(@NonNull final String transactionId) throws HieroBaseException {
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    return doGetCall("/api/v1/transactions/" + transactionId);
  }

  @NonNull
  default JSON queryAccount(@NonNull AccountId accountId) throws HieroBaseException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return doGetCall("/api/v1/accounts/" + accountId);
  }

  @NonNull
  default JSON queryExchangeRates() throws HieroBaseException {
    return doGetCall("/api/v1/network/exchangerate");
  }

  @NonNull
  default JSON queryNetworkFees() throws HieroBaseException {
    return doGetCall("/api/v1/network/fees");
  }

  @NonNull
  default JSON queryNetworkStake() throws HieroBaseException {
    return doGetCall("/api/v1/network/stake");
  }

  @NonNull
  default JSON queryNetworkSupplies() throws HieroBaseException {
    return doGetCall("/api/v1/network/supply");
  }

  @NonNull
  default JSON queryTokenById(TokenId tokenId) throws HieroBaseException {
    return doGetCall("/api/v1/tokens/" + tokenId);
  }

  @NonNull
  default JSON queryTopicById(TopicId topicId) throws HieroBaseException {
    return doGetCall("/api/v1/topics/" + topicId);
  }

  @NonNull
  default JSON queryTopicMessageBySequenceNumber(TopicId topicId, long sequenceNumber)
      throws HieroBaseException {
    return doGetCall("/api/v1/topics/" + topicId + "/messages/" + sequenceNumber);
  }

  @NonNull JSON doGetCall(@NonNull String path) throws HieroBaseException;

  @NonNull
  default JSON queryContracts() throws HieroBaseException {
    return doGetCall("/api/v1/contracts");
  }

  @NonNull
  default JSON queryContractById(@NonNull final ContractId contractId) throws HieroBaseException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return doGetCall("/api/v1/contracts/" + contractId);
  }

  /**
   * Queries a block by its identifier (number or hash).
   *
   * @param blockIdentifier the block number or hash
   * @return the JSON response
   * @throws HieroBaseException if an error occurs
   */
  @NonNull
  default JSON queryBlock(@NonNull final String blockIdentifier) throws HieroBaseException {
    Objects.requireNonNull(blockIdentifier, "blockIdentifier must not be null");
    return doGetCall("/api/v1/blocks/" + blockIdentifier);
  }
}
