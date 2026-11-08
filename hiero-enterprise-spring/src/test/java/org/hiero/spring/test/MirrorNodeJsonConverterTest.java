package org.hiero.spring.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.ScheduleId;
import com.hedera.hashgraph.sdk.proto.SchedulableTransactionBody;
import java.io.InputStream;
import java.time.Instant;
import java.util.HexFormat;
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
import org.hiero.base.data.NftTransactionHistory;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.data.ScheduleSignature;
import org.hiero.base.data.ScheduleSignatureType;
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
import org.junit.jupiter.params.provider.EnumSource;
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

  @Test
  void shouldParseValidNftTransactionHistories() {
    final JsonNode node = loadJson("nft-transaction-history.json");
    final List<NftTransactionHistory> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNftTransactionHistories(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals(2, result.size());
  }

  @Test
  void shouldReturnEmptyNftTransactionHistoryList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNftTransactionHistories(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenTransactionHistoryIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"transactions\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNftTransactionHistories(node1));

    JsonNode node2 = mapper.readTree("{\"transactions\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNftTransactionHistories(node2));
  }

  // Schedules
  @Test
  void shouldParseValidSchedule() throws Exception {
    final JsonNode node = loadJson("schedule.json");
    final Optional<Schedule> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toSchedule(node));
    Assertions.assertTrue(result.isPresent());

    final Schedule schedule = result.get();
    final byte[] adminKeyBytes =
        HexFormat.of().parseHex("4fafbd7db4965bd2c109b2864fec4840c0b180a2b54446660ce4511f09287692");
    Assertions.assertEquals(ScheduleId.fromString("0.0.11296896"), schedule.scheduleId());
    Assertions.assertArrayEquals(
        adminKeyBytes,
        Assertions.assertInstanceOf(PublicKey.class, schedule.adminKey()).toBytesRaw());
    Assertions.assertEquals(
        Instant.ofEpochSecond(1793726878L, 208132774L), schedule.consensusTimestamp());
    Assertions.assertEquals(AccountId.fromString("0.0.4866188"), schedule.creatorAccountId());
    Assertions.assertFalse(schedule.deleted());
    Assertions.assertEquals(
        Instant.ofEpochSecond(1793726884L, 28797492L), schedule.executedTimestamp());
    Assertions.assertEquals(
        Instant.ofEpochSecond(1793986077L, 786000000L), schedule.expirationTime());
    Assertions.assertEquals("nodeStream automatic payments v1 29fc29ca586a5841", schedule.memo());
    Assertions.assertEquals(AccountId.fromString("0.0.4866188"), schedule.payerAccountId());
    Assertions.assertFalse(schedule.waitForExpiry());

    final SchedulableTransactionBody body =
        SchedulableTransactionBody.parseFrom(schedule.transactionBody());
    Assertions.assertEquals(200_000_000L, body.getTransactionFee());
    Assertions.assertEquals(schedule.memo(), body.getMemo());

    Assertions.assertEquals(3, schedule.signatures().size());
    final ScheduleSignature signature = schedule.signatures().get(0);
    Assertions.assertEquals(
        Instant.ofEpochSecond(1793726878L, 208132774L), signature.consensusTimestamp());
    Assertions.assertArrayEquals(adminKeyBytes, signature.publicKeyPrefix());
    // the recorded signature contains a line break inside its Base64 value
    Assertions.assertEquals(64, signature.signature().length);
    Assertions.assertEquals(ScheduleSignatureType.ED25519, signature.type());
  }

  @ParameterizedTest()
  @MethodSource("emptyNodes")
  void shouldReturnEmptyScheduleOptional(JsonNode node) {
    Assertions.assertTrue(jsonConverter.toSchedule(node).isEmpty());
  }

  @Test
  void shouldReturnEmptyScheduleOptionalForNotFoundResponse() throws Exception {
    JsonNode node =
        mapper.readTree("{\"_status\": {\"messages\": [{\"message\": \"Not found\"}]}}");
    Assertions.assertTrue(jsonConverter.toSchedule(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenScheduleTransactionBodyIsNull() {
    final ObjectNode node = (ObjectNode) loadJson("schedule.json");
    node.putNull("transaction_body");
    Assertions.assertThrows(IllegalStateException.class, () -> jsonConverter.toSchedule(node));
  }

  @Test
  void shouldParseScheduleWithNullIdAndAccounts() {
    final ObjectNode node = (ObjectNode) loadJson("schedule.json");
    node.putNull("schedule_id");
    node.putNull("creator_account_id");
    node.putNull("payer_account_id");

    final Schedule schedule = jsonConverter.toSchedule(node).orElseThrow();

    Assertions.assertNull(schedule.scheduleId());
    Assertions.assertNull(schedule.creatorAccountId());
    Assertions.assertNull(schedule.payerAccountId());
    Assertions.assertEquals(
        Instant.ofEpochSecond(1793726878L, 208132774L), schedule.consensusTimestamp());
    Assertions.assertEquals(3, schedule.signatures().size());
  }

  @ParameterizedTest
  @EnumSource(ScheduleSignatureType.class)
  void shouldParseScheduleSignatureType(ScheduleSignatureType type) {
    final ObjectNode node = (ObjectNode) loadJson("schedule.json");
    ((ObjectNode) node.get("signatures").get(0)).put("type", type.name());

    final Schedule schedule = jsonConverter.toSchedule(node).orElseThrow();

    Assertions.assertEquals(type, schedule.signatures().get(0).type());
  }

  @Test
  void shouldParseUnknownOrMissingScheduleSignatureTypeAsUnknown() {
    final ObjectNode node = (ObjectNode) loadJson("schedule.json");
    ((ObjectNode) node.get("signatures").get(0)).put("type", "ED448");
    ((ObjectNode) node.get("signatures").get(1)).putNull("type");
    ((ObjectNode) node.get("signatures").get(2)).remove("type");

    final Schedule schedule = jsonConverter.toSchedule(node).orElseThrow();

    Assertions.assertEquals(
        List.of(
            ScheduleSignatureType.UNKNOWN,
            ScheduleSignatureType.UNKNOWN,
            ScheduleSignatureType.UNKNOWN),
        schedule.signatures().stream().map(ScheduleSignature::type).toList());
  }

  @Test
  void shouldParseValidScheduleList() {
    final JsonNode node = loadJson("schedule-list.json");
    final List<Schedule> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toSchedules(node));
    Assertions.assertEquals(3, result.size());

    final Schedule waitingForExpiry = result.get(0);
    Assertions.assertEquals(ScheduleId.fromString("0.0.11295061"), waitingForExpiry.scheduleId());
    Assertions.assertEquals(ContractId.fromString("0.0.11294909"), waitingForExpiry.adminKey());
    Assertions.assertEquals(
        AccountId.fromString("0.0.11212783"), waitingForExpiry.creatorAccountId());
    Assertions.assertEquals(
        AccountId.fromString("0.0.11294909"), waitingForExpiry.payerAccountId());
    Assertions.assertTrue(waitingForExpiry.waitForExpiry());
    Assertions.assertTrue(waitingForExpiry.signatures().isEmpty());
    Assertions.assertEquals(68, waitingForExpiry.transactionBody().length);

    final Schedule deleted = result.get(1);
    Assertions.assertEquals(ScheduleId.fromString("0.0.11296689"), deleted.scheduleId());
    Assertions.assertTrue(deleted.deleted());
    Assertions.assertNull(deleted.executedTimestamp());
    Assertions.assertEquals(1, deleted.signatures().size());

    final Schedule withoutAdminKey = result.get(2);
    Assertions.assertEquals(ScheduleId.fromString("0.0.11297125"), withoutAdminKey.scheduleId());
    Assertions.assertNull(withoutAdminKey.adminKey());
    Assertions.assertNull(withoutAdminKey.expirationTime());
    Assertions.assertEquals("", withoutAdminKey.memo());
    Assertions.assertEquals(2, withoutAdminKey.signatures().size());
    Assertions.assertEquals(
        ScheduleSignatureType.ECDSA_SECP256K1, withoutAdminKey.signatures().get(0).type());
    Assertions.assertEquals(33, withoutAdminKey.signatures().get(0).publicKeyPrefix().length);
    Assertions.assertEquals(64, withoutAdminKey.signatures().get(0).signature().length);
  }

  @Test
  void shouldReturnEmptyScheduleList() throws Exception {
    JsonNode node = mapper.readTree("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toSchedules(node).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenSchedulesIsNotArray() throws Exception {
    JsonNode node1 = mapper.readTree("{\"schedules\": null}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toSchedules(node1));

    JsonNode node2 = mapper.readTree("{\"schedules\": {}}");
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonConverter.toSchedules(node2));
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
