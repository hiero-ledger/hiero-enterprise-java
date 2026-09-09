package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.ScheduleId;
import com.hedera.hashgraph.sdk.proto.SchedulableTransactionBody;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.InputStream;
import java.io.StringReader;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.BalanceSnapshot;
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
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class MirrorNodeJsonConverterTest {
  private MirrorNodeJsonConverterImpl jsonConverter;

  @BeforeEach
  void setUp() {
    jsonConverter = new MirrorNodeJsonConverterImpl();
  }

  // Accounts
  @Test
  void shouldParseValidAccountInfo() {
    final JsonObject jsonObject = loadJson("account-info.json");
    final Optional<AccountInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toAccountInfo(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyAccountInfoOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toAccountInfo(jsonObject).isEmpty());
  }

  // Blocks
  @Test
  void shouldParseValidBlocks() {
    final JsonObject jsonObject = loadJson("block-list.json");
    final List<Block> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toBlocks(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyBlockList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toBlocks(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenBlocksIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"blocks\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toBlocks(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"blocks\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toBlocks(jsonObject2));
  }

  @Test
  void shouldParseValidBlock() {
    final JsonObject jsonObject = loadJson("block.json");
    final Optional<Block> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toBlock(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyBlockOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toBlock(jsonObject).isEmpty());
  }

  // Contracts
  @Test
  void shouldParseValidContractList() {
    final JsonObject jsonObject = loadJson("contract-list.json");
    final List<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContracts(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyContractList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toContracts(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenContractsIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"contracts\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toContracts(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"contracts\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toContracts(jsonObject2));
  }

  @Test
  void shouldParseValidContractPage() {
    final JsonObject jsonObject = loadJson("contract-list.json");
    final Page<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContractPage(jsonObject));
    Assertions.assertNotNull(result);
    // Single page set this to false
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  void shouldParseValidContract() {
    final JsonObject jsonObject = loadJson("contract.json");
    final Optional<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContract(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyContractOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toContract(jsonObject).isEmpty());
  }

  // Tokens
  @Test
  void shouldParseValidTokenList() {
    final JsonObject jsonObject = loadJson("token-list.json");
    final List<Token> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTokens(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyTokenList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toTokens(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenTokensIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"tokens\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTokens(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"tokens\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTokens(jsonObject2));
  }

  @Test
  void shouldParseValidTokenInfo() {
    final JsonObject jsonObject = loadJson("token-info.json");
    final Optional<TokenInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTokenInfo(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyTokenInfoOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toTokenInfo(jsonObject).isEmpty());
  }

  // Nft
  @Test
  void shouldParseValidNftList() {
    final JsonObject jsonObject = loadJson("nft-list.json");
    final List<Nft> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toNfts(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyNftList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNfts(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenNftsIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"nfts\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNfts(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"nfts\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNfts(jsonObject2));
  }

  @Test
  void shouldParseValidNft() {
    final JsonObject jsonObject = loadJson("nft.json");
    final Optional<Nft> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNft(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyNftOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toNft(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidNftTransactionHistories() {
    final JsonObject jsonObject = loadJson("nft-transaction-history.json");
    final List<NftTransactionHistory> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNftTransactionHistories(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals(2, result.size());
  }

  @Test
  void shouldReturnEmptyNftTransactionHistoryList() throws Exception {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNftTransactionHistories(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenTransactionHistoryIsNotArray() throws Exception {
    JsonObject jsonObject1 = parseJson("{\"transactions\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNftTransactionHistories(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"transactions\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNftTransactionHistories(jsonObject2));
  }

  // Schedules
  @Test
  void shouldParseValidSchedule() throws Exception {
    final JsonObject jsonObject = loadJson("schedule.json");
    final Optional<Schedule> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toSchedule(jsonObject));
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

  @Test
  void shouldReturnEmptyScheduleOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toSchedule(jsonObject).isEmpty());
  }

  @Test
  void shouldReturnEmptyScheduleOptionalForNotFoundResponse() {
    final JsonObject jsonObject =
        parseJson("{\"_status\": {\"messages\": [{\"message\": \"Not found\"}]}}");
    Assertions.assertTrue(jsonConverter.toSchedule(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenScheduleTransactionBodyIsNull() {
    final JsonObject jsonObject =
        Json.createObjectBuilder(loadJson("schedule.json")).addNull("transaction_body").build();
    Assertions.assertThrows(
        IllegalStateException.class, () -> jsonConverter.toSchedule(jsonObject));
  }

  @Test
  void shouldParseScheduleWithNullIdAndAccounts() {
    final JsonObject jsonObject =
        Json.createObjectBuilder(loadJson("schedule.json"))
            .addNull("schedule_id")
            .addNull("creator_account_id")
            .addNull("payer_account_id")
            .build();

    final Schedule schedule = jsonConverter.toSchedule(jsonObject).orElseThrow();

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
    final JsonObject schedule = loadJson("schedule.json");
    final JsonObject signature = schedule.getJsonArray("signatures").getJsonObject(0);
    final JsonObject jsonObject =
        Json.createObjectBuilder(schedule)
            .add(
                "signatures",
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder(signature).add("type", type.name())))
            .build();

    final Schedule result = jsonConverter.toSchedule(jsonObject).orElseThrow();

    Assertions.assertEquals(type, result.signatures().get(0).type());
  }

  @Test
  void shouldParseUnknownOrMissingScheduleSignatureTypeAsUnknown() {
    final JsonObject schedule = loadJson("schedule.json");
    final JsonArray signatures = schedule.getJsonArray("signatures");
    final JsonObject jsonObject =
        Json.createObjectBuilder(schedule)
            .add(
                "signatures",
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder(signatures.getJsonObject(0)).add("type", "ED448"))
                    .add(Json.createObjectBuilder(signatures.getJsonObject(1)).addNull("type"))
                    .add(Json.createObjectBuilder(signatures.getJsonObject(2)).remove("type")))
            .build();

    final Schedule result = jsonConverter.toSchedule(jsonObject).orElseThrow();

    Assertions.assertEquals(
        List.of(
            ScheduleSignatureType.UNKNOWN,
            ScheduleSignatureType.UNKNOWN,
            ScheduleSignatureType.UNKNOWN),
        result.signatures().stream().map(ScheduleSignature::type).toList());
  }

  @Test
  void shouldParseValidScheduleList() {
    final JsonObject jsonObject = loadJson("schedule-list.json");
    final List<Schedule> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toSchedules(jsonObject));
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
  void shouldReturnEmptyScheduleList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toSchedules(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenSchedulesIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"schedules\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toSchedules(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"schedules\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toSchedules(jsonObject2));
  }

  // Topics
  @Test
  void shouldParseValidTopic() {
    final JsonObject jsonObject = loadJson("topic.json");
    final Optional<Topic> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopic(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyTopicOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toTopic(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidTopicMessageList() {
    final JsonObject jsonObject = loadJson("topic-message-list.json");
    final List<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessages(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyTopicMessageList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toTopicMessages(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenMessagesIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"messages\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTopicMessages(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"messages\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toTopicMessages(jsonObject2));
  }

  @Test
  void shouldParseValidTopicMessage() {
    final JsonObject jsonObject = loadJson("topic-message.json");
    final Optional<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessage(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyTopicMessageOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toTopicMessage(jsonObject).isEmpty());
  }

  // Transactions
  @Test
  void shouldParseValidTransactionList() {
    final JsonObject jsonObject = loadJson("transaction-list.json");
    final List<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfos(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidTransaction() {
    final JsonObject jsonObject = loadJson("transaction.json");
    final Optional<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfo(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyTransactionInfoOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toTransactionInfo(jsonObject).isEmpty());
  }

  // Network
  @Test
  void shouldParseValidNetworkSupplies() {
    final JsonObject jsonObject = loadJson("network-supply.json");
    final Optional<NetworkSupplies> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkSupplies(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyNetworkSuppliesOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toNetworkSupplies(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidNetworkStakes() {
    final JsonObject jsonObject = loadJson("network-stake.json");
    final Optional<NetworkStake> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkStake(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyNetworkStakesOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toNetworkStake(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidExchangeRate() {
    final JsonObject jsonObject = loadJson("exchange-rate.json");
    final Optional<ExchangeRates> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toExchangeRates(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyExchangeRatesOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toExchangeRates(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidNetworkFees() {
    final JsonObject jsonObject = loadJson("network-fee.json");
    final List<NetworkFee> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkFees(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyFeeList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toNetworkFees(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenFeesIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"fees\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNetworkFees(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"fees\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toNetworkFees(jsonObject2));
  }

  // Balance
  @Test
  void shouldParseValidBalanceSnapshot() {
    final JsonObject jsonObject = loadJson("balance.json");
    final Optional<BalanceSnapshot> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toBalanceSnapshot(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyBalanceSnapshotOptional() {
    final JsonObject jsonObject = Json.createObjectBuilder().build();
    Assertions.assertTrue(jsonConverter.toBalanceSnapshot(jsonObject).isEmpty());
  }

  @Test
  void shouldParseValidAccountBalance() {
    final JsonObject jsonObject = loadJson("balance.json");
    final List<AccountBalance> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toAccountBalances(jsonObject));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyAccountBalanceList() {
    JsonObject jsonObject = parseJson("{\"unknow-field\": []}");
    Assertions.assertTrue(jsonConverter.toAccountBalances(jsonObject).isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenAccountBalanceIsNotArray() {
    JsonObject jsonObject1 = parseJson("{\"balances\": null}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toAccountBalances(jsonObject1));

    JsonObject jsonObject2 = parseJson("{\"balances\": {}}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> jsonConverter.toAccountBalances(jsonObject2));
  }

  // Helpers
  private JsonObject loadJson(String filename) {
    String path = "/json/" + filename;
    try (InputStream stream = MirrorNodeJsonConverter.class.getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Json fixture not found on classpath: " + path);
      }
      try (JsonReader reader = Json.createReader(stream)) {
        return reader.readObject();
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to read json fixture: " + path, e);
    }
  }

  private JsonObject parseJson(String json) {
    try (StringReader stringReader = new StringReader(json);
        JsonReader jsonReader = Json.createReader(stringReader)) {

      return jsonReader.readObject();
    }
  }
}
