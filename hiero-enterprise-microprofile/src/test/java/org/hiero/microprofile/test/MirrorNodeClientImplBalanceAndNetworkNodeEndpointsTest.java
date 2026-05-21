package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.NetworkNode;
import org.hiero.base.data.Page;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MirrorNodeClientImplBalanceAndNetworkNodeEndpointsTest {
  @Test
  void balanceAndNetworkNodeQueriesCallMirrorNodeEndpoints() throws Exception {
    final String restTarget = "mirror-node-target";
    final List<String> requestedPaths = new ArrayList<>();
    final MirrorNodeRestClientImpl restClient = mock(MirrorNodeRestClientImpl.class);
    when(restClient.getTarget()).thenReturn(restTarget);
    when(restClient.doGetCall(anyString()))
        .thenAnswer(
            invocation -> {
              final String path = invocation.getArgument(0);
              requestedPaths.add(path);
              return jsonObject(responseBodyFor(path));
            });
    doCallRealMethod().when(restClient).queryBalances();
    doCallRealMethod().when(restClient).queryNetworkNodeById(anyLong());

    final AccountId accountId = AccountId.fromString("0.0.1001");
    final String balancesPath = "/api/v1/balances";
    final String balancesByAccountPath = "/api/v1/balances?account.id=0.0.1001";
    final String nodesPath = "/api/v1/network/nodes";
    final String nodeByIdPath = "/api/v1/network/nodes?node.id=0";
    final MirrorNodeClientImpl client =
        new MirrorNodeClientImpl(restClient, new MirrorNodeJsonConverterImpl());

    try (MockedStatic<ClientBuilder> clientBuilder = mockStatic(ClientBuilder.class)) {
      mockRestBasedPageClient(clientBuilder, restTarget, requestedPaths);

      final Page<AccountBalance> balances = client.queryBalances();
      final Page<AccountBalance> balancesByAccount = client.queryBalancesByAccount(accountId);
      final BalanceSnapshot snapshot = client.queryBalanceSnapshot().orElseThrow();
      final Page<NetworkNode> nodes = client.queryNetworkNodes();
      final NetworkNode node = client.queryNetworkNodeById(0).orElseThrow();

      assertEquals(
          List.of(balancesPath, balancesByAccountPath, balancesPath, nodesPath, nodeByIdPath),
          requestedPaths);
      assertEquals(1, balances.getSize());
      assertEquals(1, balancesByAccount.getSize());
      assertEquals(1, snapshot.balances().size());
      assertEquals(1, nodes.getSize());
      assertEquals(0, node.nodeId());
    }
  }

  private static void mockRestBasedPageClient(
      MockedStatic<ClientBuilder> clientBuilder, String restTarget, List<String> requestedPaths) {
    final Client client = mock(Client.class);
    final WebTarget target = mock(WebTarget.class);
    final Invocation.Builder requestBuilder = mock(Invocation.Builder.class);
    final Response response = mock(Response.class);
    final String[] currentPath = new String[1];

    clientBuilder.when(ClientBuilder::newClient).thenReturn(client);
    when(client.target(restTarget)).thenReturn(target);
    when(target.path(anyString()))
        .thenAnswer(
            invocation -> {
              currentPath[0] = invocation.getArgument(0);
              return target;
            });
    when(target.queryParam(anyString(), any()))
        .thenAnswer(
            invocation -> {
              currentPath[0] =
                  appendQuery(currentPath[0], invocation.getArgument(0), invocation.getArgument(1));
              return target;
            });
    when(target.request(MediaType.APPLICATION_JSON)).thenReturn(requestBuilder);
    when(requestBuilder.get())
        .thenAnswer(
            invocation -> {
              requestedPaths.add(currentPath[0]);
              return response;
            });
    when(response.readEntity(JsonObject.class))
        .thenAnswer(
            invocation ->
                jsonObject(responseBodyFor(requestedPaths.get(requestedPaths.size() - 1))));
  }

  private static String appendQuery(String path, Object key, Object value) {
    final String separator = path.contains("?") ? "&" : "?";
    return path + separator + key + "=" + value;
  }

  private static JsonObject jsonObject(String json) {
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      return reader.readObject();
    }
  }

  private static String responseBodyFor(String path) {
    if (path.startsWith("/api/v1/network/nodes")) {
      return networkNodesJson();
    }
    return balancesJson();
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
