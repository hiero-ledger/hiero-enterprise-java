package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.AccountId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.NetworkNode;
import org.hiero.base.data.Page;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class MirrorNodeClientImplBalanceAndNetworkNodeEndpointsTest {
  private HttpServer server;
  private final List<String> requestedPaths = new CopyOnWriteArrayList<>();

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.start();
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
  }

  @Test
  void balanceAndNetworkNodeQueriesCallMirrorNodeEndpoints() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.1001");
    final String balancesPath = "/api/v1/balances";
    final String balancesByAccountPath = "/api/v1/balances";
    final String nodesPath = "/api/v1/network/nodes";
    respondWith(balancesPath, balancesJson());
    respondWith(nodesPath, networkNodesJson());
    final MirrorNodeClientImpl client =
        new MirrorNodeClientImpl(RestClient.builder().baseUrl(baseUrl()));

    final Page<AccountBalance> balances = client.queryBalances();
    final Page<AccountBalance> balancesByAccount = client.queryBalancesByAccount(accountId);
    final BalanceSnapshot snapshot = client.queryBalanceSnapshot().orElseThrow();
    final Page<NetworkNode> nodes = client.queryNetworkNodes();
    final NetworkNode node = client.queryNetworkNodeById(0).orElseThrow();

    assertEquals(
        List.of(balancesPath, balancesByAccountPath, balancesPath, nodesPath, nodesPath),
        requestedPaths);
    assertEquals(1, balances.getSize());
    assertEquals(1, balancesByAccount.getSize());
    assertEquals(1, snapshot.balances().size());
    assertEquals(1, nodes.getSize());
    assertEquals(0, node.nodeId());
  }

  private void respondWith(String path, String body) {
    server.createContext(
        path,
        exchange -> {
          requestedPaths.add(exchange.getRequestURI().getPath());
          respond(exchange, body);
        });
  }

  private String baseUrl() {
    return "http://localhost:" + server.getAddress().getPort();
  }

  private static void respond(HttpExchange exchange, String body) throws IOException {
    final byte[] bytes = body.getBytes();
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(bytes);
    }
  }

  private static String balancesJson() {
    return """
        {
          "timestamp": "1234567890.123456789",
          "balances": [
            {"account": "0.0.1001", "balance": 1000000, "tokens": []}
          ],
          "links": {"next": null}
        }
        """;
  }

  private static String networkNodesJson() {
    return """
        {
          "nodes": [
            {
              "node_id": 0,
              "node_account_id": "0.0.3",
              "timestamp": {"from": "1234567890.000000001", "to": null},
              "service_endpoints": []
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
