package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TopicId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.query.AccountQuery;
import org.hiero.base.mirrornode.query.NftQuery;
import org.hiero.base.mirrornode.query.TransactionQuery;
import org.jspecify.annotations.NonNull;

public abstract class AbstractMirrorNodeClient<JSON> implements MirrorNodeClient {

  @NonNull
  protected abstract MirrorNodeRestClient<JSON> getRestClient();

  @NonNull
  protected abstract MirrorNodeJsonConverter<JSON> getJsonConverter();

  @Override
  public @NonNull final Optional<Nft> queryNftsByTokenIdAndSerial(
      @NonNull final TokenId tokenId, final long serialNumber) throws HieroException {
    final JSON json = getRestClient().queryNftsByTokenIdAndSerial(tokenId, serialNumber);
    return getJsonConverter().toNft(json);
  }

  @Override
  public @NonNull Page<Nft> queryNfts(@NonNull NftQuery query) throws HieroException {
    Objects.requireNonNull(query, "query must not be null");
    Map<String, List<String>> params = new HashMap<>();
    query.getAccountId().ifPresent(id -> addParam(params, "account.id", id.toString()));
    query.getTokenId().ifPresent(id -> addParam(params, "token.id", id.toString()));
    query.getLimit().ifPresent(l -> addParam(params, "limit", l.toString()));
    query.getOrder().ifPresent(o -> addParam(params, "order", o.toString()));

    final JSON json = getRestClient().doGetCall("/api/v1/nfts", params);
    return new org.hiero.base.data.SinglePage<>(getJsonConverter().toNfts(json));
  }

  @Override
  public @NonNull final Optional<AccountInfo> queryAccount(@NonNull final AccountId accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final JSON json = getRestClient().queryAccount(accountId);
    return getJsonConverter().toAccountInfo(json);
  }

  @Override
  public @NonNull Page<AccountInfo> queryAccounts(@NonNull AccountQuery query)
      throws HieroException {
    Objects.requireNonNull(query, "query must not be null");
    Map<String, List<String>> params = new HashMap<>();
    query.getLimit().ifPresent(l -> addParam(params, "limit", l.toString()));
    query.getOrder().ifPresent(o -> addParam(params, "order", o.toString()));
    query.getBalance().ifPresent(b -> addParam(params, "balance", b.toString()));

    final JSON json = getRestClient().doGetCall("/api/v1/accounts", params);
    return new org.hiero.base.data.SinglePage<>(getJsonConverter().toAccountInfos(json));
  }

  @Override
  public @NonNull final Optional<ExchangeRates> queryExchangeRates() throws HieroException {
    final JSON json = getRestClient().queryExchangeRates();
    return getJsonConverter().toExchangeRates(json);
  }

  @Override
  public @NonNull final List<NetworkFee> queryNetworkFees() throws HieroException {
    final JSON json = getRestClient().queryNetworkFees();
    return getJsonConverter().toNetworkFees(json);
  }

  @Override
  public @NonNull final Optional<NetworkStake> queryNetworkStake() throws HieroException {
    final JSON json = getRestClient().queryNetworkStake();
    return getJsonConverter().toNetworkStake(json);
  }

  @Override
  public @NonNull final Optional<NetworkSupplies> queryNetworkSupplies() throws HieroException {
    final JSON json = getRestClient().queryNetworkSupplies();
    return getJsonConverter().toNetworkSupplies(json);
  }

  @NonNull
  public final Optional<TokenInfo> queryTokenById(@NonNull TokenId tokenId) throws HieroException {
    final JSON json = getRestClient().queryTokenById(tokenId);
    return getJsonConverter().toTokenInfo(json);
  }

  @Override
  @NonNull
  public final Optional<TransactionInfo> queryTransaction(@NonNull String transactionId)
      throws HieroException {
    final JSON json = getRestClient().queryTransaction(transactionId);
    return getJsonConverter().toTransactionInfo(json);
  }

  @Override
  @NonNull
  public Page<TransactionInfo> queryTransactions(@NonNull TransactionQuery query)
      throws HieroException {
    Objects.requireNonNull(query, "query must not be null");
    Map<String, List<String>> params = new HashMap<>();
    query.getAccountId().ifPresent(id -> addParam(params, "account.id", id.toString()));
    query.getType().ifPresent(t -> addParam(params, "type", t.getType()));
    query.getResult().ifPresent(r -> addParam(params, "result", r.name().toLowerCase()));
    query.getLimit().ifPresent(l -> addParam(params, "limit", l.toString()));
    query.getOrder().ifPresent(o -> addParam(params, "order", o.toString()));
    query
        .getTimestampRange()
        .ifPresent(
            range -> {
              addParam(params, "timestamp", "gt:" + range.from());
              addParam(params, "timestamp", "lt:" + range.to());
            });

    final JSON json = getRestClient().doGetCall("/api/v1/transactions", params);
    return new org.hiero.base.data.SinglePage<>(getJsonConverter().toTransactionInfos(json));
  }

  @Override
  @NonNull
  public final Optional<Topic> queryTopicById(TopicId topicId) throws HieroException {
    final JSON json = getRestClient().queryTopicById(topicId);
    return getJsonConverter().toTopic(json);
  }

  @Override
  @NonNull
  public final Optional<TopicMessage> queryTopicMessageBySequenceNumber(
      TopicId topicId, long sequenceNumber) throws HieroException {
    final JSON json = getRestClient().queryTopicMessageBySequenceNumber(topicId, sequenceNumber);
    return getJsonConverter().toTopicMessage(json);
  }

  @Override
  public @NonNull Optional<NftMetadata> getNftMetadata(TokenId tokenId) throws HieroException {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public @NonNull Page<Contract> queryContracts() throws HieroException {
    final JSON json = getRestClient().queryContracts();
    return getJsonConverter().toContractPage(json);
  }

  @Override
  public @NonNull Optional<Contract> queryContractById(@NonNull final ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    final JSON json = getRestClient().queryContractById(contractId);
    return getJsonConverter().toContract(json);
  }

  @Override
  public @NonNull Optional<Block> queryBlockByNumber(long number) throws HieroException {
    if (number < 0) {
      throw new IllegalArgumentException("Block number must not be negative");
    }
    final JSON json = getRestClient().queryBlock(String.valueOf(number));
    return getJsonConverter().toBlock(json);
  }

  @Override
  public @NonNull Optional<Block> queryBlockByHash(@NonNull String hash) throws HieroException {
    Objects.requireNonNull(hash, "hash must not be null");
    final JSON json = getRestClient().queryBlock(hash);
    return getJsonConverter().toBlock(json);
  }

  private void addParam(Map<String, List<String>> params, String key, String value) {
    params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
  }
}
