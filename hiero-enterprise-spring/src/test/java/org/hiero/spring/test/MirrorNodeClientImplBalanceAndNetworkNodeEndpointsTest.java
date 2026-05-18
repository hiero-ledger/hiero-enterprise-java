package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.hedera.hashgraph.sdk.AccountId;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.NetworkNode;
import org.hiero.base.data.Page;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

class MirrorNodeClientImplBalanceAndNetworkNodeEndpointsTest {
  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void balanceAndNetworkNodeQueriesCallMirrorNodeEndpoints() throws Exception {
    final List<String> requestedPaths = new ArrayList<>();
    final RestClient.Builder restClientBuilder = mock(RestClient.Builder.class);
    final RestClient restClient = mock(RestClient.class);
    final RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    final RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
    final RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    when(restClientBuilder.build()).thenReturn(restClient);
    when(restClientBuilder.clone()).thenReturn(restClientBuilder);
    when(restClient.mutate()).thenReturn(restClientBuilder);
    when(restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri(any(Function.class)))
        .thenAnswer(
            invocation -> {
              final Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
              requestedPaths.add(
                  requestPath(uriFunction.apply(UriComponentsBuilder.newInstance())));
              return headersSpec;
            });
    when(headersSpec.accept(APPLICATION_JSON)).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.toEntity(String.class))
        .thenAnswer(
            invocation ->
                ResponseEntity.ok(responseBodyFor(requestedPaths.get(requestedPaths.size() - 1))));

    final AccountId accountId = AccountId.fromString("0.0.1001");
    final String balancesPath = "/api/v1/balances";
    final String balancesByAccountPath = "/api/v1/balances?account.id=0.0.1001";
    final String nodesPath = "/api/v1/network/nodes";
    final String nodeByIdPath = "/api/v1/network/nodes?node.id=0";
    final MirrorNodeClientImpl client = new MirrorNodeClientImpl(restClientBuilder);

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

  private static String requestPath(URI uri) {
    return uri.getRawQuery() == null ? uri.getPath() : uri.getPath() + "?" + uri.getRawQuery();
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
