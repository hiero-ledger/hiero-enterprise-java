package org.hiero.microprofile.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TopicId;
import jakarta.json.JsonObject;
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

public class MirrorNodeClientImpl extends AbstractMirrorNodeClient<JsonObject> {

  private static final String TRANSACTIONS_PATH = "/api/v1/transactions";
  private static final String TOKENS_PATH = "/api/v1/tokens";
  private static final String TOPICS_PATH = "/api/v1/topics";

  private static final String MSG_REST_CLIENT_MUST_NOT_BE_NULL = "restClient must not be null";
  private static final String MSG_JSON_CONVERTER_MUST_NOT_BE_NULL =
      "jsonConverter must not be null";
  private static final String MSG_ACCOUNT_ID_MUST_NOT_BE_NULL = "accountId must not be null";
  private static final String MSG_TYPE_MUST_NOT_BE_NULL = "type must not be null";
  private static final String MSG_RESULT_MUST_NOT_BE_NULL = "result must not be null";
  private static final String MSG_TOKEN_ID_MUST_NOT_BE_NULL = "tokenId must not be null";
  private static final String MSG_TOPIC_ID_MUST_NOT_BE_NULL = "topicId must not be null";

  private final MirrorNodeRestClientImpl restClient;

  private final MirrorNodeJsonConverter<JsonObject> jsonConverter;

  public MirrorNodeClientImpl(
      MirrorNodeRestClientImpl restClient, MirrorNodeJsonConverter<JsonObject> jsonConverter) {
    this.restClient = Objects.requireNonNull(restClient, MSG_REST_CLIENT_MUST_NOT_BE_NULL);
    this.jsonConverter = Objects.requireNonNull(jsonConverter, MSG_JSON_CONVERTER_MUST_NOT_BE_NULL);
  }

  @Override
  protected @NonNull MirrorNodeRestClient<JsonObject> getRestClient() {
    return restClient;
  }

  @Override
  protected @NonNull MirrorNodeJsonConverter<JsonObject> getJsonConverter() {
    return jsonConverter;
  }

  @Override
  public @NonNull Page<Nft> queryNftsByAccount(@NonNull AccountId accountId) throws HieroException {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public @NonNull Page<Nft> queryNftsByAccountAndTokenId(
      @NonNull AccountId accountId, @NonNull TokenId tokenId) throws HieroException {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public @NonNull Page<Nft> queryNftsByTokenId(@NonNull TokenId tokenId) throws HieroException {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccount(@NonNull AccountId accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    final String path = TRANSACTIONS_PATH + "?account.id=" + accountId;
    final Function<JsonObject, List<TransactionInfo>> dataExtractionFunction =
        node -> jsonConverter.toTransactionInfos(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndType(
      @NonNull AccountId accountId, @NonNull TransactionType type) throws HieroException {
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    Objects.requireNonNull(type, MSG_TYPE_MUST_NOT_BE_NULL);
    final String path =
        TRANSACTIONS_PATH + "?account.id=" + accountId + "&transactiontype=" + type.getType();
    final Function<JsonObject, List<TransactionInfo>> dataExtractionFunction =
        node -> jsonConverter.toTransactionInfos(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndResult(
      @NonNull AccountId accountId, @NonNull Result result) throws HieroException {
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    Objects.requireNonNull(result, MSG_RESULT_MUST_NOT_BE_NULL);
    final String path = TRANSACTIONS_PATH + "?account.id=" + accountId + "&result=" + result.name();
    final Function<JsonObject, List<TransactionInfo>> dataExtractionFunction =
        node -> jsonConverter.toTransactionInfos(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<TransactionInfo> queryTransactionsByAccountAndModification(
      @NonNull AccountId accountId, @NonNull BalanceModification type) throws HieroException {
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    Objects.requireNonNull(type, MSG_TYPE_MUST_NOT_BE_NULL);
    final String path = TRANSACTIONS_PATH + "?account.id=" + accountId + "&type=" + type.name();
    final Function<JsonObject, List<TransactionInfo>> dataExtractionFunction =
        node -> jsonConverter.toTransactionInfos(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public Page<Token> queryTokensForAccount(@NonNull AccountId accountId) throws HieroException {
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    final String path = TOKENS_PATH + "?account.id=" + accountId;
    final Function<JsonObject, List<Token>> dataExtractionFunction =
        node -> jsonConverter.toTokens(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<Balance> queryTokenBalances(@NonNull TokenId tokenId) throws HieroException {
    Objects.requireNonNull(tokenId, MSG_TOKEN_ID_MUST_NOT_BE_NULL);
    final String path = TOKENS_PATH + "/" + tokenId + "/balances";
    final Function<JsonObject, List<Balance>> dataExtractionFunction =
        node -> jsonConverter.toBalances(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<Balance> queryTokenBalancesForAccount(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    Objects.requireNonNull(tokenId, MSG_TOKEN_ID_MUST_NOT_BE_NULL);
    Objects.requireNonNull(accountId, MSG_ACCOUNT_ID_MUST_NOT_BE_NULL);
    final String path = TOKENS_PATH + "/" + tokenId + "/balances?account.id=" + accountId;
    final Function<JsonObject, List<Balance>> dataExtractionFunction =
        node -> jsonConverter.toBalances(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<TopicMessage> queryTopicMessages(TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, MSG_TOPIC_ID_MUST_NOT_BE_NULL);
    final String path = TOPICS_PATH + "/" + topicId + "/messages";
    final Function<JsonObject, List<TopicMessage>> dataExtractionFunction =
        node -> jsonConverter.toTopicMessages(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }

  @Override
  public @NonNull Page<NftMetadata> findNftTypesByOwner(AccountId ownerId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public @NonNull Page<NftMetadata> findAllNftTypes() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public @NonNull Page<Block> queryBlocks() throws HieroException {
    final String path = "/api/v1/blocks";
    final Function<JsonObject, List<Block>> dataExtractionFunction =
        node -> jsonConverter.toBlocks(node);
    return new RestBasedPage<>(restClient.getTarget(), dataExtractionFunction, path);
  }
}
