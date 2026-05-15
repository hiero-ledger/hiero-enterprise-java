package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.util.List;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

class MirrorNodeClientImplAccountEndpointsTest {

  private static final String BASE_URL = "http://mirror-node.test";

  private MockedStatic<ClientBuilder> mockedClientBuilder;
  private WebTarget mockBaseTarget;
  private MirrorNodeClientImpl client;

  @BeforeEach
  void setUp() {
    final Client mockClient = mock(Client.class);
    mockBaseTarget = mock(WebTarget.class);

    mockedClientBuilder = mockStatic(ClientBuilder.class);
    mockedClientBuilder.when(ClientBuilder::newClient).thenReturn(mockClient);
    when(mockClient.target(BASE_URL)).thenReturn(mockBaseTarget);

    client =
        new MirrorNodeClientImpl(
            new MirrorNodeRestClientImpl(BASE_URL), new MirrorNodeJsonConverterImpl());
  }

  @AfterEach
  void tearDown() {
    mockedClientBuilder.close();
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
    stubResponse(cryptoAllowancesPath, cryptoAllowancesJson());
    stubResponse(tokenAllowancesPath, tokenAllowancesJson());
    stubResponse(nftAllowancesPath, nftAllowancesJson());
    stubResponse(stakingRewardsPath, stakingRewardsJson());
    stubResponse(outstandingAirdropsPath, tokenAirdropsJson());
    stubResponse(pendingAirdropsPath, tokenAirdropsJson());

    final Page<CryptoAllowance> cryptoAllowances = client.queryCryptoAllowances(accountId);
    final Page<TokenAllowance> tokenAllowances = client.queryTokenAllowances(accountId);
    final Page<NftAllowance> nftAllowances = client.queryNftAllowances(accountId);
    final Page<StakingReward> stakingRewards = client.queryStakingRewards(accountId);
    final Page<TokenAirdrop> outstandingAirdrops = client.queryOutstandingAirdrops(accountId);
    final Page<TokenAirdrop> pendingAirdrops = client.queryPendingAirdrops(accountId);

    final ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
    verify(mockBaseTarget, times(6)).path(pathCaptor.capture());
    assertEquals(
        List.of(
            cryptoAllowancesPath,
            tokenAllowancesPath,
            nftAllowancesPath,
            stakingRewardsPath,
            outstandingAirdropsPath,
            pendingAirdropsPath),
        pathCaptor.getAllValues());
    assertEquals(1, cryptoAllowances.getSize());
    assertEquals(1, tokenAllowances.getSize());
    assertEquals(1, nftAllowances.getSize());
    assertEquals(1, stakingRewards.getSize());
    assertEquals(1, outstandingAirdrops.getSize());
    assertEquals(1, pendingAirdrops.getSize());
    assertEquals(TokenId.fromString("0.0.9"), tokenAllowances.getData().get(0).tokenId());
  }

  private void stubResponse(String path, String body) {
    final WebTarget pathTarget = mock(WebTarget.class);
    final Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);
    final Response response = mock(Response.class);
    when(mockBaseTarget.path(path)).thenReturn(pathTarget);
    when(pathTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
    when(invocationBuilder.get()).thenReturn(response);
    when(response.readEntity(JsonObject.class)).thenReturn(parseJson(body));
  }

  private static JsonObject parseJson(String json) {
    return Json.createReader(new StringReader(json)).readObject();
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
