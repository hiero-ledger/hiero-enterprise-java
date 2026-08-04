package org.hiero.microprofile.test;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
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
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  // Helper
  static JsonObject loadJson(String filename) {
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
}
