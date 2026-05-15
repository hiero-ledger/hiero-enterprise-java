package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MirrorNodeClientImplAccountEndpointsTest {

  private static final String BASE_URL = "http://mirror-node.test";

  private MockRestServiceServer mockServer;
  private MirrorNodeClientImpl client;

  @BeforeEach
  void setUp() {
    final RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
    mockServer = MockRestServiceServer.bindTo(builder).build();
    client = new MirrorNodeClientImpl(builder);
  }

  @AfterEach
  void tearDown() {
    mockServer.verify();
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
    expect(cryptoAllowancesPath, cryptoAllowancesJson());
    expect(tokenAllowancesPath, tokenAllowancesJson());
    expect(nftAllowancesPath, nftAllowancesJson());
    expect(stakingRewardsPath, stakingRewardsJson());
    expect(outstandingAirdropsPath, tokenAirdropsJson());
    expect(pendingAirdropsPath, tokenAirdropsJson());

    final Page<CryptoAllowance> cryptoAllowances = client.queryCryptoAllowances(accountId);
    final Page<TokenAllowance> tokenAllowances = client.queryTokenAllowances(accountId);
    final Page<NftAllowance> nftAllowances = client.queryNftAllowances(accountId);
    final Page<StakingReward> stakingRewards = client.queryStakingRewards(accountId);
    final Page<TokenAirdrop> outstandingAirdrops = client.queryOutstandingAirdrops(accountId);
    final Page<TokenAirdrop> pendingAirdrops = client.queryPendingAirdrops(accountId);

    assertEquals(1, cryptoAllowances.getSize());
    assertEquals(1, tokenAllowances.getSize());
    assertEquals(1, nftAllowances.getSize());
    assertEquals(1, stakingRewards.getSize());
    assertEquals(1, outstandingAirdrops.getSize());
    assertEquals(1, pendingAirdrops.getSize());
    assertEquals(TokenId.fromString("0.0.9"), tokenAllowances.getData().get(0).tokenId());
  }

  private void expect(String path, String body) {
    mockServer
        .expect(requestTo(BASE_URL + path))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
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
