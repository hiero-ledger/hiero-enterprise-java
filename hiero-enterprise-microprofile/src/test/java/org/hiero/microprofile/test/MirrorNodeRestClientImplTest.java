package org.hiero.microprofile.test;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.hiero.base.HieroException;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies that {@link MirrorNodeRestClientImpl} reuses a single JAX-RS client across calls, closes
 * response resources, and translates HTTP status codes into the documented contract (404 -> empty
 * JSON, other errors -> {@link HieroException}).
 */
class MirrorNodeRestClientImplTest {

  private HttpServer server;
  private String baseUrl;

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.start();
    baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
  }

  @AfterEach
  void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  private void registerHandler(final String path, final HttpHandler handler) {
    server.createContext(path, handler);
  }

  private static HttpHandler staticResponse(final int status, final String body) {
    return exchange -> {
      final byte[] payload = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(status, payload.length == 0 ? -1 : payload.length);
      if (payload.length > 0) {
        exchange.getResponseBody().write(payload);
      }
      exchange.close();
    };
  }

  @Test
  void returnsParsedJsonForSuccessfulResponse() throws HieroException {
    registerHandler("/api/v1/accounts/0.0.42", staticResponse(200, "{\"account\":\"0.0.42\"}"));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final JsonObject result = client.doGetCall("/api/v1/accounts/0.0.42");
      Assertions.assertEquals("0.0.42", result.getString("account"));
    }
  }

  @Test
  void returnsEmptyJsonForNotFound() throws HieroException {
    registerHandler("/api/v1/accounts/missing", staticResponse(404, "{\"_status\":\"not found\"}"));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final JsonObject result = client.doGetCall("/api/v1/accounts/missing");
      Assertions.assertTrue(result.isEmpty());
    }
  }

  @Test
  void throwsHieroExceptionForClientError() {
    registerHandler("/api/v1/bad", staticResponse(400, "{\"_status\":\"bad request\"}"));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final HieroException thrown =
          Assertions.assertThrows(HieroException.class, () -> client.doGetCall("/api/v1/bad"));
      Assertions.assertTrue(thrown.getMessage().contains("400"));
      Assertions.assertTrue(thrown.getMessage().contains("/api/v1/bad"));
    }
  }

  @Test
  void throwsHieroExceptionForServerError() {
    registerHandler("/api/v1/oops", staticResponse(500, "{\"_status\":\"server error\"}"));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final HieroException thrown =
          Assertions.assertThrows(HieroException.class, () -> client.doGetCall("/api/v1/oops"));
      Assertions.assertTrue(thrown.getMessage().contains("500"));
      Assertions.assertTrue(thrown.getMessage().contains("/api/v1/oops"));
    }
  }

  @Test
  void throwsHieroExceptionForServerErrorWithoutBody() {
    registerHandler("/api/v1/no-body", staticResponse(500, null));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final HieroException thrown =
          Assertions.assertThrows(HieroException.class, () -> client.doGetCall("/api/v1/no-body"));
      Assertions.assertTrue(thrown.getMessage().contains("500"));
      Assertions.assertTrue(thrown.getMessage().contains("/api/v1/no-body"));
    }
  }

  @Test
  void parsesQueryStringIntoQueryParams() throws HieroException {
    final AtomicInteger hits = new AtomicInteger(0);
    registerHandler(
        "/api/v1/transactions",
        exchange -> {
          hits.incrementAndGet();
          Assertions.assertEquals(
              "account.id=0.0.98&transactiontype=CRYPTOCREATEACCOUNT",
              exchange.getRequestURI().getRawQuery());
          staticResponse(200, "{\"transactions\":[]}").handle(exchange);
        });
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      final JsonObject result =
          client.doGetCall(
              "/api/v1/transactions?account.id=0.0.98&transactiontype=CRYPTOCREATEACCOUNT");
      Assertions.assertEquals(0, result.getJsonArray("transactions").size());
    }
    Assertions.assertEquals(1, hits.get());
  }

  @Test
  void multipleCallsReuseTheSameClient() throws HieroException {
    registerHandler("/api/v1/network/fees", staticResponse(200, "{\"fees\":[]}"));
    try (final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl)) {
      for (int i = 0; i < 20; i++) {
        final JsonObject result = client.doGetCall("/api/v1/network/fees");
        Assertions.assertTrue(result.containsKey("fees"));
      }
    }
  }

  @Test
  void closeIsIdempotent() {
    final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(baseUrl);
    Assertions.assertDoesNotThrow(client::close);
    Assertions.assertDoesNotThrow(client::close);
  }

  @Test
  void constructorRejectsNullTarget() {
    Assertions.assertThrows(NullPointerException.class, () -> new MirrorNodeRestClientImpl(null));
  }
}
