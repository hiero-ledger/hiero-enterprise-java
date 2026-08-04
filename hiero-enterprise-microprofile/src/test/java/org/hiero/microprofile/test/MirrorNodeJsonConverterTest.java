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
    final JsonObject node = loadJson("account-info.json");
    final Optional<AccountInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toAccountInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Blocks
  @Test
  void shouldParseValidBlocks() {
    final JsonObject node = loadJson("block-list.json");
    final List<Block> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toBlocks(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidBlock() {
    final JsonObject node = loadJson("block.json");
    final Optional<Block> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toBlock(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Contracts
  @Test
  void shouldParseValidContractList() {
    final JsonObject node = loadJson("contract-list.json");
    final List<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContracts(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidContractPage() {
    final JsonObject node = loadJson("contract-list.json");
    final Page<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContractPage(node));
    Assertions.assertNotNull(result);
    // Single page set this to false
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  void shouldParseValidContract() {
    final JsonObject node = loadJson("contract.json");
    final Optional<Contract> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toContract(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Tokens
  @Test
  void shouldParseValidTokenList() {
    final JsonObject node = loadJson("token-list.json");
    final List<Token> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toTokens(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidTokenInfo() {
    final JsonObject node = loadJson("token-info.json");
    final Optional<TokenInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTokenInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Nft
  @Test
  void shouldParseValidNftList() {
    final JsonObject node = loadJson("nft-list.json");
    final List<Nft> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toNfts(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidNft() {
    final JsonObject node = loadJson("nft.json");
    final Optional<Nft> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toNft(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Topics
  @Test
  void shouldParseValidTopic() {
    final JsonObject node = loadJson("topic.json");
    final Optional<Topic> result = Assertions.assertDoesNotThrow(() -> jsonConverter.toTopic(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldParseValidTopicMessageList() {
    final JsonObject node = loadJson("topic-message-list.json");
    final List<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessages(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidTopicMessage() {
    final JsonObject node = loadJson("topic-message.json");
    final Optional<TopicMessage> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTopicMessage(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Transactions
  @Test
  void shouldParseValidTransactionList() {
    final JsonObject node = loadJson("transaction-list.json");
    final List<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfos(node));
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldParseValidTransaction() {
    final JsonObject node = loadJson("transaction.json");
    final Optional<TransactionInfo> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toTransactionInfo(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  // Network
  @Test
  void shouldParseValidNetworkSupplies() {
    final JsonObject node = loadJson("network-supply.json");
    final Optional<NetworkSupplies> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkSupplies(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldParseValidNetworkStakes() {
    final JsonObject node = loadJson("network-stake.json");
    final Optional<NetworkStake> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkStake(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldParseValidExchangeRate() {
    final JsonObject node = loadJson("exchange-rate.json");
    final Optional<ExchangeRates> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toExchangeRates(node));
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void shouldParseValidNetworkFees() {
    final JsonObject node = loadJson("network-fee.json");
    final List<NetworkFee> result =
        Assertions.assertDoesNotThrow(() -> jsonConverter.toNetworkFees(node));
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
