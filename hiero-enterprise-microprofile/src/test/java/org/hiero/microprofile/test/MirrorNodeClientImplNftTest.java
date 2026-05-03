package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.hiero.base.HieroException;
import org.hiero.base.data.Nft;
import org.hiero.base.data.Page;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MirrorNodeClientImplNftTest {

  private static final String HOST = "127.0.0.1";
  private static final String ACCOUNT_NFTS_PATH = "/api/v1/accounts/0.0.1001/nfts";
  private static final String TOKEN_NFTS_PATH = "/api/v1/tokens/0.0.2002/nfts";
  private static final String TOKEN_NFTS_FOR_ACCOUNT_PATH =
      TOKEN_NFTS_PATH + "?account.id=0.0.1001";
  private static final String EMPTY_NFTS_RESPONSE = "{\"nfts\":[],\"links\":{\"next\":null}}";
  private static final String NFT_METADATA = "test NFT";
  private static final String NFT_METADATA_BASE64 = "dGVzdCBORlQ=";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private HttpServer server;
  private String baseUrl;
  private final AtomicReference<String> lastRequestedPath = new AtomicReference<>();

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
    server.setExecutor(null);
    server.start();
    baseUrl = "http://" + HOST + ":" + server.getAddress().getPort();
  }

  @AfterEach
  void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void queryNftsByAccountHitsExpectedPath() throws HieroException {
    respondWith(EMPTY_NFTS_RESPONSE);

    final MirrorNodeClientImpl client = newClient();
    final Page<Nft> page = client.queryNftsByAccount(AccountId.fromString("0.0.1001"));

    assertNotNull(page);
    assertEquals(0, page.getSize());
    assertEquals(ACCOUNT_NFTS_PATH, lastRequestedPath.get());
  }

  @Test
  void queryNftsByTokenIdHitsExpectedPath() throws HieroException {
    respondWith(EMPTY_NFTS_RESPONSE);

    final MirrorNodeClientImpl client = newClient();
    final Page<Nft> page = client.queryNftsByTokenId(TokenId.fromString("0.0.2002"));

    assertNotNull(page);
    assertEquals(0, page.getSize());
    assertEquals(TOKEN_NFTS_PATH, lastRequestedPath.get());
  }

  @Test
  void queryNftsByAccountAndTokenIdIncludesAccountFilter() throws HieroException {
    respondWith(EMPTY_NFTS_RESPONSE);

    final MirrorNodeClientImpl client = newClient();
    final Page<Nft> page =
        client.queryNftsByAccountAndTokenId(
            AccountId.fromString("0.0.1001"), TokenId.fromString("0.0.2002"));

    assertNotNull(page);
    assertEquals(0, page.getSize());
    assertEquals(TOKEN_NFTS_FOR_ACCOUNT_PATH, lastRequestedPath.get());
  }

  @Test
  void queryNftsByAccountParsesResponse() throws HieroException {
    final String body =
        "{\"nfts\":[{"
            + "\"account_id\":\"0.0.1001\","
            + "\"created_timestamp\":\"1700000000.000000000\","
            + "\"modified_timestamp\":\"1700000001.000000000\","
            + "\"deleted\":false,"
            + "\"metadata\":\""
            + NFT_METADATA_BASE64
            + "\","
            + "\"serial_number\":1,"
            + "\"token_id\":\"0.0.2002\""
            + "}],\"links\":{\"next\":null}}";
    respondWith(body);

    final MirrorNodeClientImpl client = newClient();
    final Page<Nft> page = client.queryNftsByAccount(AccountId.fromString("0.0.1001"));

    assertEquals(1, page.getSize());
    final List<Nft> data = page.getData();
    assertEquals(TokenId.fromString("0.0.2002"), data.get(0).tokenId());
    assertEquals(1L, data.get(0).serial());
    assertArrayEquals(NFT_METADATA.getBytes(StandardCharsets.UTF_8), data.get(0).metadata());
  }

  @Test
  void queryNftsByAccountRejectsNullInput() {
    respondWith(EMPTY_NFTS_RESPONSE);

    final MirrorNodeClientImpl client = newClient();
    assertThrows(NullPointerException.class, () -> client.queryNftsByAccount((AccountId) null));
  }

  @Test
  void queryNftsByTokenIdRejectsNullInput() {
    respondWith(EMPTY_NFTS_RESPONSE);

    final MirrorNodeClientImpl client = newClient();
    assertThrows(NullPointerException.class, () -> client.queryNftsByTokenId((TokenId) null));
  }

  private MirrorNodeClientImpl newClient() {
    return new MirrorNodeClientImpl(
        new MirrorNodeRestClientImpl(baseUrl), new MirrorNodeJsonConverterImpl());
  }

  private void respondWith(final String body) {
    final HttpHandler handler =
        new HttpHandler() {
          @Override
          public void handle(final HttpExchange exchange) throws IOException {
            final String path = exchange.getRequestURI().getRawPath();
            final String query = exchange.getRequestURI().getRawQuery();
            lastRequestedPath.set(query == null ? path : path + "?" + query);
            final byte[] payload = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(CONTENT_TYPE_HEADER, APPLICATION_JSON);
            exchange.sendResponseHeaders(200, payload.length);
            exchange.getResponseBody().write(payload);
            exchange.close();
          }
        };
    server.createContext("/", handler);
  }
}
