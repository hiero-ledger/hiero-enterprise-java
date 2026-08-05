package org.hiero.spring.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Nft;
import org.hiero.base.data.Page;
import org.hiero.base.data.Token;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.implementation.MirrorNodeJsonConverter;
import org.hiero.spring.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MirrorNodeJsonConverterTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private MirrorNodeJsonConverterImpl jsonConverter;

  @BeforeEach
  void setUp() {
    jsonConverter = new MirrorNodeJsonConverterImpl();
  }

  // Accounts
  @Test
  void shouldParseValidAccountInfo() {
    final JsonNode node = loadJson("account-info.json");
    final Optional<AccountInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toAccountInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyAccountInfoOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toAccountInfo(node).isEmpty());
  }

  // Blocks
  @Test
  void shouldParseValidBlocks() {
    final JsonNode node = loadJson("block-list.json");
    final List<Block> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toBlocks(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyBlockList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toBlocks(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenBlocksIsNotArray() throws Exception {
    // null value
    JsonNode node1 = mapper.readTree("{\"blocks\": null}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toBlocks(node1));

    // not array
    JsonNode node2 = mapper.readTree("{\"blocks\": {}}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toBlocks(node2));
  }

  @Test
  void shouldParseValidBlock() {
    final JsonNode node = loadJson("block.json");
    final Optional<Block> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toBlock(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyBlockOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toBlock(node).isEmpty());
  }

  // Contracts
  @Test
  void shouldParseValidContractList() {
    final JsonNode node = loadJson("contract-list.json");
    final List<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContracts(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyContractList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toContracts(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenContractsIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"contracts\": null}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toContracts(node1));

    JsonNode node2 = mapper.readTree("{\"contracts\": {}}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toContracts(node2));
  }

  @Test
  void shouldParseValidContractPage() {
    final JsonNode node = loadJson("contract-list.json");
    final Page<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContractPage(node));
    Assertions.assertNotNull(result);
    // Single page set this to false
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  void shouldParseValidContract() {
    final JsonNode node = loadJson("contract.json");
    final Optional<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContract(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyContractOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toContract(node).isEmpty());
  }

  // Tokens
  @Test
  void shouldParseValidTokenList() {
    final JsonNode node = loadJson("token-list.json");
    final List<Token> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toTokens(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyTokenList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toTokens(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenTokensIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"tokens\": null}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toTokens(node1));

    JsonNode node2 = mapper.readTree("{\"tokens\": {}}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toTokens(node2));
  }

  @Test
  void shouldParseValidTokenInfo() {
    final JsonNode node = loadJson("token-info.json");
    final Optional<TokenInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTokenInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyTokenInfoOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toTokenInfo(node).isEmpty());
  }

  // Nft
  @Test
  void shouldParseValidNftList() {
    final JsonNode node = loadJson("nft-list.json");
    final List<Nft> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toNfts(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyNftList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNfts(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenNftsIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"nfts\": null}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toNfts(node1));

    JsonNode node2 = mapper.readTree("{\"nfts\": {}}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toNfts(node2));
  }

  @Test
  void shouldParseValidNft() {
    final JsonNode node = loadJson("nft.json");
    final Optional<Nft> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toNft(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyNftOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toNft(node).isEmpty());
  }

  // Topics
  @Test
  void shouldParseValidTopic() {
    final JsonNode node = loadJson("topic.json");
    final Optional<Topic> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toTopic(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyTopicOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toTopic(node).isEmpty());
  }

  @Test
  void shouldParseValidTopicMessageList() {
    final JsonNode node = loadJson("topic-message-list.json");
    final List<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessages(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyTopicMessageList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toTopicMessages(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenMessagesIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"messages\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTopicMessages(node1));

    JsonNode node2 = mapper.readTree("{\"messages\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTopicMessages(node2));
  }

  @Test
  void shouldParseValidTopicMessage() {
    final JsonNode node = loadJson("topic-message.json");
    final Optional<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessage(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyTopicMessageOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toTopicMessage(node).isEmpty());
  }

  // Transactions
  @Test
  void shouldParseValidTransactionList() {
    final JsonNode node = loadJson("transaction-list.json");
    final List<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfos(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidTransaction() {
    final JsonNode node = loadJson("transaction.json");
    final Optional<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyTransactionInfoOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toTransactionInfo(node).isEmpty());
  }

  // Network
  @Test
  void shouldParseValidNetworkSupplies() {
    final JsonNode node = loadJson("network-supply.json");
    final Optional<NetworkSupplies> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkSupplies(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyNetworkSuppliesOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toNetworkSupplies(node).isEmpty());
  }

  @Test
  void shouldParseValidNetworkStakes() {
    final JsonNode node = loadJson("network-stake.json");
    final Optional<NetworkStake> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkStake(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyNetworkStakesOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toNetworkStake(node).isEmpty());
  }

  @Test
  void shouldParseValidExchangeRate() {
    final JsonNode node = loadJson("exchange-rate.json");
    final Optional<ExchangeRates> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toExchangeRates(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyExchangeRatesOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toExchangeRates(node).isEmpty());
  }

  @Test
  void shouldParseValidNetworkFees() {
    final JsonNode node = loadJson("network-fee.json");
    final List<NetworkFee> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkFees(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyFeeList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNetworkFees(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenFeesIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"fees\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNetworkFees(node1));

    JsonNode node2 = mapper.readTree("{\"fees\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNetworkFees(node2));
  }

  // Helper
  private JsonNode loadJson(String filename) {
    String path = "/json/" + filename;
    try (InputStream stream = MirrorNodeJsonConverter.class.getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Json fixture not found on classpath: " + path);
      }
      return mapper.readTree(stream);
    } catch (Exception e) {
      throw new RuntimeException("Failed to read json fixture: " + path, e);
    }
  }

  private static Stream<JsonNode> emptyNodes() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    return Stream.of(mapper.readTree("{}"), NullNode.getInstance());
  }
}
