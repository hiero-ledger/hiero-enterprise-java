package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class MirrorNodeClientImplNftTransactionHistoryTest {

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
  void queryNftTransactionHistoryCallsMirrorNodeEndpoint() throws Exception {
    final String expectedPath = "/api/v1/tokens/0.0.222/nfts/1/transactions";
    server.createContext(
        expectedPath,
        exchange -> {
          requestedPaths.add(exchange.getRequestURI().getPath());
          respond(exchange, nftTransactionHistoryJson());
        });
    final MirrorNodeClientImpl client =
        new MirrorNodeClientImpl(RestClient.builder().baseUrl(baseUrl()));

    final Page<NftTransactionTransfer> page =
        client.queryNftTransactionHistory(TokenId.fromString("0.0.222"), 1);

    assertEquals(List.of(expectedPath), requestedPaths);
    assertEquals(1, page.getSize());
    final NftTransactionTransfer transfer = page.getData().get(0);
    assertEquals(Instant.ofEpochSecond(1_618_591_023L, 997_420_021), transfer.consensusTimestamp());
    assertEquals(AccountId.fromString("0.0.11"), transfer.receiverAccountId());
    assertEquals(AccountId.fromString("0.0.10"), transfer.senderAccountId());
    assertEquals("0.0.19789-1618591023-997420021", transfer.transactionId());
    assertEquals(TransactionType.CRYPTO_TRANSFER, transfer.type());
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

  private static String nftTransactionHistoryJson() {
    return """
        {
          "transactions": [
            {
              "consensus_timestamp": "1618591023.997420021",
              "is_approval": false,
              "nonce": 0,
              "receiver_account_id": "0.0.11",
              "sender_account_id": "0.0.10",
              "transaction_id": "0.0.19789-1618591023-997420021",
              "type": "CRYPTOTRANSFER"
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
