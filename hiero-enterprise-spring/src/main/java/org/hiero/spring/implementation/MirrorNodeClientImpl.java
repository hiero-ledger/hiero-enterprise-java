package org.hiero.spring.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TopicId;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.hiero.base.HieroException;
import org.hiero.base.data.Balance;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Block;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.base.data.Result;
import org.hiero.base.data.Token;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.implementation.AbstractMirrorNodeClient;
import org.hiero.base.implementation.MirrorNodeJsonConverter;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;
import org.springframework.web.client.RestClient;

public class MirrorNodeClientImpl extends AbstractMirrorNodeClient<JsonNode> {

  private final ObjectMapper objectMapper;

  private final RestClient restClient;

  private final MirrorNodeRestClientImpl mirrorNodeRestClient;

  private final MirrorNodeJsonConverter<JsonNode> jsonConverter;

  /**
   * Constructor.
   *
   * @param restClientBuilder the builder for the REST client that must have the base URL set
   */
  public MirrorNodeClientImpl(final RestClient.Builder restClientBuilder) {
    Objects.requireNonNull(restClientBuilder, "restClientBuilder must not be null");
    mirrorNodeRestClient = new MirrorNodeRestClientImpl(restClientBuilder);
    jsonConverter = new MirrorNodeJsonConverterImpl();
    objectMapper = new ObjectMapper();
    restClient = restClientBuilder.build();
  }

  @Override
  protected final MirrorNodeRestClient<JsonNode> getRestClient() {
    return mirrorNodeRestClient;
  }

  @Override
  protected final MirrorNodeJsonConverter<JsonNode> getJsonConverter() {
    return jsonConverter;
  }

  @Override
  public Page<Nft> queryNftsByAccount(@NonNull final AccountId accountId) throws HieroException {
    Objects.requireNonNull(accountId, "newAccountId must not be null");
    final String path = "/api/v1/accounts/" + accountId + "/nfts";
    final Function<JsonNode, List<Nft>> dataExtractionFunction = node -> jsonConverter.toNfts(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public Page<Nft> queryNftsByAccountAndTokenId(
      @NonNull final AccountId accountId, @NonNull final TokenId tokenId) {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    final String path = "/api/v1/tokens/" + tokenId + "/nfts/?account.id=" + accountId;
    final Function<JsonNode, List<Nft>> dataExtractionFunction = node -> jsonConverter.toNfts(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public Page<Nft> queryNftsByTokenId(@NonNull TokenId tokenId) {
    final String path = "/api/v1/tokens/" + tokenId + "/nfts";
    final Function<JsonNode, List<Nft>> dataExtractionFunction = node -> jsonConverter.toNfts(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public Page<TransactionInfo> queryTransactionsByAccount(@NonNull final AccountId accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path = "/api/v1/transactions?account.id=" + accountId;
    final Function<JsonNode, List<TransactionInfo>> dataExtractionFunction =
        n -> jsonConverter.toTransactionInfos(n);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndType(
      @NonNull AccountId accountId, @NonNull TransactionType type) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path =
        "/api/v1/transactions?account.id=" + accountId + "&transactiontype=" + type.getType();
    final Function<JsonNode, List<TransactionInfo>> dataExtractionFunction =
        n -> jsonConverter.toTransactionInfos(n);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndResult(
      @NonNull AccountId accountId, @NonNull Result result) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path = "/api/v1/transactions?account.id=" + accountId + "&result=" + result.name();
    final Function<JsonNode, List<TransactionInfo>> dataExtractionFunction =
        n -> jsonConverter.toTransactionInfos(n);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndModification(
      @NonNull AccountId accountId, @NonNull BalanceModification type) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path = "/api/v1/transactions?account.id=" + accountId + "&type=" + type.name();
    final Function<JsonNode, List<TransactionInfo>> dataExtractionFunction =
        n -> jsonConverter.toTransactionInfos(n);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public Page<Token> queryTokensForAccount(@NonNull AccountId accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path = "/api/v1/tokens?account.id=" + accountId;
    final Function<JsonNode, List<Token>> dataExtractionFunction =
        node -> jsonConverter.toTokens(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<Balance> queryTokenBalances(TokenId tokenId) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    final String path = "/api/v1/tokens/" + tokenId + "/balances";
    final Function<JsonNode, List<Balance>> dataExtractionFunction =
        node -> jsonConverter.toBalances(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<Balance> queryTokenBalancesForAccount(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    final String path = "/api/v1/tokens/" + tokenId + "/balances?account.id=" + accountId;
    final Function<JsonNode, List<Balance>> dataExtractionFunction =
        node -> jsonConverter.toBalances(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<TopicMessage> queryTopicMessages(TopicId topicId) {
    Objects.requireNonNull(topicId, "topicId must not be null");
    final String path = "/api/v1/topics/" + topicId + "/messages";
    final Function<JsonNode, List<TopicMessage>> dataExtractionFunction =
        node -> jsonConverter.toTopicMessages(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }

  @Override
  public @NonNull Page<NftMetadata> findNftTypesByOwner(AccountId ownerId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public @NonNull Page<NftMetadata> findAllNftTypes() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public @NonNull Page<Block> queryBlocks() throws HieroException {
    final String path = "/api/v1/blocks";
    final Function<JsonNode, List<Block>> dataExtractionFunction =
        node -> jsonConverter.toBlocks(node);
    return new RestBasedPage<>(
        objectMapper, restClient.mutate().clone(), path, dataExtractionFunction);
  }
}
