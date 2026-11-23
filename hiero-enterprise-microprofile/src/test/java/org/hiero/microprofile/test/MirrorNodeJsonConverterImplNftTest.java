package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hiero.base.data.Nft;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Test;

class MirrorNodeJsonConverterImplNftTest {

  private static final String TOKEN_ID = "0.0.2002";
  private static final String ACCOUNT_ID = "0.0.1001";
  private static final String NFT_METADATA = "test NFT";
  private static final String NFT_METADATA_BASE64 = "dGVzdCBORlQ=";

  private final MirrorNodeJsonConverterImpl converter = new MirrorNodeJsonConverterImpl();

  @Test
  void toNftsReturnsEmptyWhenKeyMissing() {
    final JsonObject jsonObject = parse("{\"links\":{\"next\":null}}");

    final List<Nft> result = converter.toNfts(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toNftsReturnsEmptyForEmptyArray() {
    final JsonObject jsonObject = parse("{\"nfts\":[],\"links\":{\"next\":null}}");

    final List<Nft> result = converter.toNfts(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toNftsParsesSingleEntry() {
    final String json =
        "{\"nfts\":[{"
            + "\"account_id\":\""
            + ACCOUNT_ID
            + "\","
            + "\"created_timestamp\":\"1700000000.000000000\","
            + "\"modified_timestamp\":\"1700000001.000000000\","
            + "\"deleted\":false,"
            + "\"metadata\":\""
            + NFT_METADATA_BASE64
            + "\","
            + "\"serial_number\":1,"
            + "\"token_id\":\""
            + TOKEN_ID
            + "\""
            + "}],\"links\":{\"next\":null}}";
    final JsonObject jsonObject = parse(json);

    final List<Nft> result = converter.toNfts(jsonObject);

    assertEquals(1, result.size());
    final Nft nft = result.get(0);
    assertEquals(TokenId.fromString(TOKEN_ID), nft.tokenId());
    assertEquals(AccountId.fromString(ACCOUNT_ID), nft.owner());
    assertEquals(1L, nft.serial());
    assertArrayEquals(NFT_METADATA.getBytes(StandardCharsets.UTF_8), nft.metadata());
  }

  @Test
  void toNftsParsesMultipleEntries() {
    final String json =
        "{\"nfts\":[{"
            + "\"account_id\":\""
            + ACCOUNT_ID
            + "\","
            + "\"created_timestamp\":\"1700000000.000000000\","
            + "\"modified_timestamp\":\"1700000001.000000000\","
            + "\"deleted\":false,"
            + "\"metadata\":\"\","
            + "\"serial_number\":1,"
            + "\"token_id\":\""
            + TOKEN_ID
            + "\"},{"
            + "\"account_id\":\""
            + ACCOUNT_ID
            + "\","
            + "\"created_timestamp\":\"1700000000.000000000\","
            + "\"modified_timestamp\":\"1700000002.000000000\","
            + "\"deleted\":false,"
            + "\"metadata\":\"\","
            + "\"serial_number\":2,"
            + "\"token_id\":\""
            + TOKEN_ID
            + "\""
            + "}],\"links\":{\"next\":null}}";
    final JsonObject jsonObject = parse(json);

    final List<Nft> result = converter.toNfts(jsonObject);

    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).serial());
    assertEquals(2L, result.get(1).serial());
  }

  private static JsonObject parse(final String json) {
    return Json.createReader(new StringReader(json)).readObject();
  }
}
