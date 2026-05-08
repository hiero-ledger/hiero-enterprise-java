package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class MirrorNodeClientNftMetadataTest {

  private HttpServer server;

  private MirrorNodeClientImpl client;

  @BeforeEach
  void setUp() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/api/v1/tokens", this::handleTokensRequest);
    server.start();
    final String baseUrl = "http://localhost:" + server.getAddress().getPort();
    client = new MirrorNodeClientImpl(RestClient.builder().baseUrl(baseUrl));
  }

  @AfterEach
  void tearDown() {
    server.stop(0);
  }

  @Test
  void getNftMetadataReturnsNftTokenDetails() throws Exception {
    final Optional<NftMetadata> result = client.getNftMetadata(TokenId.fromString("0.0.1001"));

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(TokenId.fromString("0.0.1001"), result.get().tokenId());
    Assertions.assertEquals("Collection One", result.get().name());
    Assertions.assertEquals("COL1", result.get().symbol());
    Assertions.assertEquals(AccountId.fromString("0.0.5001"), result.get().treasuryAccountId());
  }

  @Test
  void findAllNftTypesReturnsOnlyNftTypes() {
    final Page<NftMetadata> page = client.findAllNftTypes();

    Assertions.assertEquals(1, page.getData().size());
    Assertions.assertEquals(TokenId.fromString("0.0.1001"), page.getData().getFirst().tokenId());
    Assertions.assertFalse(page.hasNext());
  }

  @Test
  void findNftTypesByOwnerFiltersOutFungibleTokens() {
    final Page<NftMetadata> page = client.findNftTypesByOwner(AccountId.fromString("0.0.2002"));

    Assertions.assertEquals(1, page.getData().size());
    Assertions.assertEquals(TokenId.fromString("0.0.1002"), page.getData().getFirst().tokenId());
    Assertions.assertEquals("Collection Two", page.getData().getFirst().name());
  }

  private void handleTokensRequest(final HttpExchange exchange) throws IOException {
    final String path = exchange.getRequestURI().getPath();
    final String query = exchange.getRequestURI().getQuery();

    if ("/api/v1/tokens".equals(path)) {
      if ("type=NON_FUNGIBLE_UNIQUE".equals(query)) {
        respond(
            exchange,
            """
            {"tokens":[{"token_id":"0.0.1001","metadata":"","name":"Collection One","symbol":"COL1","decimals":0,"type":"NON_FUNGIBLE_UNIQUE"}],"links":{"next":null}}
            """);
        return;
      }
      if ("account.id=0.0.2002".equals(query)) {
        respond(
            exchange,
            """
            {"tokens":[
              {"token_id":"0.0.1002","metadata":"","name":"Collection Two","symbol":"COL2","decimals":0,"type":"NON_FUNGIBLE_UNIQUE"},
              {"token_id":"0.0.2001","metadata":"","name":"Fungible Token","symbol":"FT","decimals":2,"type":"FUNGIBLE_COMMON"}
            ],"links":{"next":null}}
            """);
        return;
      }
    }

    if ("/api/v1/tokens/0.0.1001".equals(path)) {
      respond(exchange, tokenInfoJson("0.0.1001", "Collection One", "COL1", "0.0.5001"));
      return;
    }

    if ("/api/v1/tokens/0.0.1002".equals(path)) {
      respond(exchange, tokenInfoJson("0.0.1002", "Collection Two", "COL2", "0.0.5002"));
      return;
    }

    respond(exchange, "{}");
  }

  private static String tokenInfoJson(
      final String tokenId, final String name, final String symbol, final String treasuryId) {
    return """
        {
          "token_id":"%s",
          "type":"NON_FUNGIBLE_UNIQUE",
          "name":"%s",
          "symbol":"%s",
          "memo":"",
          "decimals":0,
          "metadata":"",
          "created_timestamp":1,
          "modified_timestamp":2,
          "expiry_timestamp":null,
          "supply_type":"FINITE",
          "initial_supply":"0",
          "total_supply":"1",
          "max_supply":"1000",
          "treasury_account_id":"%s",
          "deleted":false,
          "custom_fees":{"fixed_fees":[],"fractional_fees":[],"royalty_fees":[]}
        }
        """
        .formatted(tokenId, name, symbol, treasuryId);
  }

  private static void respond(final HttpExchange exchange, final String body) throws IOException {
    final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(bytes);
    }
  }
}
