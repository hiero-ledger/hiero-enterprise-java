package org.hiero.helidon.se.sample;

import com.hedera.hashgraph.sdk.TokenId;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.Objects;
import org.hiero.base.data.ContractCallResult;

public final class HelidonSeSampleMain {

  private HelidonSeSampleMain() {}

  public static void main(String[] args) {
    final HieroClients clients = HieroClients.fromEnv();
    final int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "8082"));

    final WebServer server =
        WebServer.builder()
            .port(port)
            .routing(
                builder ->
                    builder
                        .get("/", (req, res) -> res.send("Hiero Helidon SE sample is running"))
                        .post("/topics", (req, res) -> createTopic(req, res, clients))
                        .post("/tokens/transfer", (req, res) -> transferToken(req, res, clients))
                        .post("/contracts/call", (req, res) -> callContract(req, res, clients)))
            .build();

    server.start();
  }

  private static void createTopic(
      final ServerRequest request, final ServerResponse response, final HieroClients clients) {
    try {
      final String memo =
          valueOrDefault(request.query().first("memo").orElse(null), "sample-topic");
      final String topicId = clients.topicClient().createTopic(memo).toString();
      response.send("{\"topicId\":\"" + topicId + "\"}");
    } catch (final Exception e) {
      response.status(500).send(e.getMessage());
    }
  }

  private static void transferToken(
      final ServerRequest request, final ServerResponse response, final HieroClients clients) {
    try {
      final String tokenId = requiredQuery(request, "tokenId");
      final String toAccountId = requiredQuery(request, "toAccountId");
      final long amount = Long.parseLong(requiredQuery(request, "amount"));
      clients.tokenClient().transferToken(TokenId.fromString(tokenId), toAccountId, amount);
      response.send("{\"status\":\"Token transfer submitted\"}");
    } catch (final Exception e) {
      response.status(500).send(e.getMessage());
    }
  }

  private static void callContract(
      final ServerRequest request, final ServerResponse response, final HieroClients clients) {
    try {
      final String contractId = requiredQuery(request, "contractId");
      final String functionName = requiredQuery(request, "functionName");
      final ContractCallResult result =
          clients.smartContractClient().callContractFunction(contractId, functionName);
      response.send("{\"gasUsed\":" + result.gasUsed() + ",\"cost\":\"" + result.cost() + "\"}");
    } catch (final Exception e) {
      response.status(500).send(e.getMessage());
    }
  }

  private static String requiredQuery(final ServerRequest request, final String key) {
    return request
        .query()
        .first(key)
        .filter(value -> !value.isBlank())
        .orElseThrow(() -> new IllegalArgumentException("Missing query parameter: " + key));
  }

  private static String valueOrDefault(final String value, final String fallback) {
    if (Objects.isNull(value) || value.isBlank()) {
      return fallback;
    }
    return value;
  }
}
