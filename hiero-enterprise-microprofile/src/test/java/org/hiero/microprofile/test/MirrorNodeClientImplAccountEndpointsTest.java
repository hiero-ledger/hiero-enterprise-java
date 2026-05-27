package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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
  void queryCryptoAllowancesMapsFields() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String cryptoAllowancesPath = "/api/v1/accounts/0.0.123/allowances/crypto";
    stubResponse(cryptoAllowancesPath, cryptoAllowancesJson());

    final Page<CryptoAllowance> result = client.queryCryptoAllowances(accountId);

    verify(mockBaseTarget).path(cryptoAllowancesPath);
    final CryptoAllowance allowance = result.getData().get(0);
    assertEquals(100L, allowance.amountGranted());
    assertEquals(AccountId.fromString("0.0.2"), allowance.owner());
    assertEquals(AccountId.fromString("0.0.8"), allowance.spender());
    assertNull(allowance.timestamp().to());
  }

  @Test
  void queryTokenAllowancesMapsFields() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String tokenAllowancesPath = "/api/v1/accounts/0.0.123/allowances/tokens";
    stubResponse(tokenAllowancesPath, tokenAllowancesJson());

    final Page<TokenAllowance> result = client.queryTokenAllowances(accountId);

    verify(mockBaseTarget).path(tokenAllowancesPath);
    final TokenAllowance allowance = result.getData().get(0);
    assertEquals(TokenId.fromString("0.0.9"), allowance.tokenId());
    assertEquals(75L, allowance.amount());
    assertEquals(AccountId.fromString("0.0.2"), allowance.owner());
  }

  @Test
  void queryNftAllowancesMapsFields() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String nftAllowancesPath = "/api/v1/accounts/0.0.123/allowances/nfts";
    stubResponse(nftAllowancesPath, nftAllowancesJson());

    final Page<NftAllowance> result = client.queryNftAllowances(accountId);

    verify(mockBaseTarget).path(nftAllowancesPath);
    final NftAllowance allowance = result.getData().get(0);
    assertFalse(allowance.approvedForAll());
    assertEquals(TokenId.fromString("0.0.99"), allowance.tokenId());
    assertNotNull(allowance.timestamp().to());
  }

  @Test
  void queryStakingRewardsMapsFields() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String stakingRewardsPath = "/api/v1/accounts/0.0.123/rewards";
    stubResponse(stakingRewardsPath, stakingRewardsJson());

    final Page<StakingReward> result = client.queryStakingRewards(accountId);

    verify(mockBaseTarget).path(stakingRewardsPath);
    final StakingReward reward = result.getData().get(0);
    assertEquals(AccountId.fromString("0.0.1000"), reward.accountId());
    assertEquals(10L, reward.amount());
    assertEquals(1234567890L, reward.timestamp().getEpochSecond());
    assertEquals(1, reward.timestamp().getNano());
  }

  @Test
  void queryOutstandingAirdropsMapsFieldsWithNullSerial() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String outstandingAirdropsPath = "/api/v1/accounts/0.0.123/airdrops/outstanding";
    stubResponse(outstandingAirdropsPath, tokenAirdropsJson());

    final Page<TokenAirdrop> result = client.queryOutstandingAirdrops(accountId);

    verify(mockBaseTarget).path(outstandingAirdropsPath);
    final TokenAirdrop airdrop = result.getData().get(0);
    assertEquals(AccountId.fromString("0.0.10"), airdrop.senderId());
    assertEquals(AccountId.fromString("0.0.15"), airdrop.receiverId());
    assertNull(airdrop.serialNumber());
  }

  @Test
  void queryPendingAirdropsMapsFieldsWithSerial() throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.123");
    final String pendingAirdropsPath = "/api/v1/accounts/0.0.123/airdrops/pending";
    stubResponse(pendingAirdropsPath, pendingAirdropsJson());

    final Page<TokenAirdrop> result = client.queryPendingAirdrops(accountId);

    verify(mockBaseTarget).path(pendingAirdropsPath);
    final TokenAirdrop airdrop = result.getData().get(0);
    assertEquals(42L, airdrop.serialNumber());
    assertEquals(TokenId.fromString("0.0.100"), airdrop.tokenId());
    assertEquals(10L, airdrop.amount());
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

  private static String pendingAirdropsJson() {
    return """
        {
          "airdrops": [
            {
              "amount": 10,
              "receiver_id": "0.0.15",
              "sender_id": "0.0.10",
              "serial_number": 42,
              "timestamp": {"from": "1651560386.060890949", "to": "1651560386.661997287"},
              "token_id": "0.0.100"
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
