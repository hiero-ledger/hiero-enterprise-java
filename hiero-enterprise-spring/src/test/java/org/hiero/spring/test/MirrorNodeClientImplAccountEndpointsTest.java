package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class MirrorNodeClientImplAccountEndpointsTest {

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
  void accountEndpointQueriesCallMirrorNodeEndpoints() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String cryptoAllowancesPath = "/api/v1/accounts/0.0.123/allowances/crypto";
    final String tokenAllowancesPath = "/api/v1/accounts/0.0.123/allowances/tokens";
    final String nftAllowancesPath = "/api/v1/accounts/0.0.123/allowances/nfts";
    final String stakingRewardsPath = "/api/v1/accounts/0.0.123/rewards";
    final String outstandingAirdropsPath = "/api/v1/accounts/0.0.123/airdrops/outstanding";
    final String pendingAirdropsPath = "/api/v1/accounts/0.0.123/airdrops/pending";
    respondWith(cryptoAllowancesPath, cryptoAllowancesJson());
    respondWith(tokenAllowancesPath, tokenAllowancesJson());
    respondWith(nftAllowancesPath, nftAllowancesJson());
    respondWith(stakingRewardsPath, stakingRewardsJson());
    respondWith(outstandingAirdropsPath, tokenAirdropsJson());
    respondWith(pendingAirdropsPath, tokenAirdropsJson());
    final MirrorNodeClientImpl client =
        new MirrorNodeClientImpl(RestClient.builder().baseUrl(baseUrl()));

    final Page<CryptoAllowance> cryptoAllowances = client.queryCryptoAllowances(accountId);
    final Page<TokenAllowance> tokenAllowances = client.queryTokenAllowances(accountId);
    final Page<NftAllowance> nftAllowances = client.queryNftAllowances(accountId);
    final Page<StakingReward> stakingRewards = client.queryStakingRewards(accountId);
    final Page<TokenAirdrop> outstandingAirdrops = client.queryOutstandingAirdrops(accountId);
    final Page<TokenAirdrop> pendingAirdrops = client.queryPendingAirdrops(accountId);

    assertEquals(
        List.of(
            cryptoAllowancesPath,
            tokenAllowancesPath,
            nftAllowancesPath,
            stakingRewardsPath,
            outstandingAirdropsPath,
            pendingAirdropsPath),
        requestedPaths);
    assertEquals(1, cryptoAllowances.getSize());
    assertEquals(1, tokenAllowances.getSize());
    assertEquals(1, nftAllowances.getSize());
    assertEquals(1, stakingRewards.getSize());
    assertEquals(1, outstandingAirdrops.getSize());
    assertEquals(1, pendingAirdrops.getSize());
    assertEquals(TokenId.fromString("0.0.9"), tokenAllowances.getData().get(0).tokenId());
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

  private static String cryptoAllowancesJson() {
    return """
        {
          "allowances": [
            {
              "amount": 75,
              "amount_granted": 100,
              "owner": "0.0.2",
              "spender": "0.0.8",
              "timestamp": {"from": "1586567700.453054000", "to": null}
            }
          ],
          "links": {"next": null}
        }
        """;
  }

  private static String tokenAllowancesJson() {
    return """
        {
          "allowances": [
            {
              "amount": 75,
              "amount_granted": 100,
              "owner": "0.0.2",
              "spender": "0.0.8",
              "timestamp": {"from": "1586567700.453054000", "to": null},
              "token_id": "0.0.9"
            }
          ],
          "links": {"next": null}
        }
        """;
  }

  private static String nftAllowancesJson() {
    return """
        {
          "allowances": [
            {
              "approved_for_all": false,
              "owner": "0.0.11",
              "spender": "0.0.15",
              "timestamp": {"from": "1651560386.060890949", "to": "1651560386.661997287"},
              "token_id": "0.0.99"
            }
          ],
          "links": {"next": null}
        }
        """;
  }

  private static String stakingRewardsJson() {
    return """
        {
          "rewards": [
            {
              "account_id": "0.0.1000",
              "amount": 10,
              "timestamp": "1234567890.000000001"
            }
          ],
          "links": {"next": null}
        }
        """;
  }

  private static String tokenAirdropsJson() {
    return """
        {
          "airdrops": [
            {
              "amount": 10,
              "receiver_id": "0.0.15",
              "sender_id": "0.0.10",
              "serial_number": null,
              "timestamp": {"from": "1651560386.060890949", "to": "1651560386.661997287"},
              "token_id": "0.0.99"
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
