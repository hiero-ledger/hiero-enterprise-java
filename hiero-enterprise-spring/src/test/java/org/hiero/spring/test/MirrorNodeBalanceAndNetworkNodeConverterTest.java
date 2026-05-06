package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Instant;
import java.util.List;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.NetworkNode;
import org.hiero.spring.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Test;

class MirrorNodeBalanceAndNetworkNodeConverterTest {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final MirrorNodeJsonConverterImpl converter = new MirrorNodeJsonConverterImpl();

  @Test
  void parsesBalanceSnapshot() throws Exception {
    final JsonNode node = objectMapper.readTree(balancesJson());

    final BalanceSnapshot snapshot = converter.toBalanceSnapshot(node).orElseThrow();
    final List<AccountBalance> balances = converter.toAccountBalances(node);

    assertEquals(Instant.ofEpochSecond(1_234_567_890L, 123_456_789), snapshot.timestamp());
    assertEquals(1, snapshot.balances().size());
    assertEquals(1, balances.size());
    assertEquals(AccountId.fromString("0.0.1001"), balances.get(0).account());
    assertEquals(TokenId.fromString("0.0.2002"), balances.get(0).tokens().get(0).tokenId());
  }

  @Test
  void parsesNetworkNodes() throws Exception {
    final JsonNode node = objectMapper.readTree(networkNodesJson());

    final List<NetworkNode> nodes = converter.toNetworkNodes(node);

    assertEquals(1, nodes.size());
    assertEquals(0, nodes.get(0).nodeId());
    assertEquals(AccountId.fromString("0.0.3"), nodes.get(0).nodeAccountId());
    assertTrue(nodes.get(0).stakingPeriod() == null);
    assertEquals("127.0.0.1", nodes.get(0).serviceEndpoints().get(0).ipAddressV4());
  }

  private static String balancesJson() {
    return """
        {
          "timestamp": "1234567890.123456789",
          "balances": [
            {
              "account": "0.0.1001",
              "balance": 1000000,
              "tokens": [
                {"token_id": "0.0.2002", "balance": 42}
              ]
            }
          ],
          "links": {"next": "/api/v1/balances?account.id=gt:0.0.1001"}
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
              "description": "node zero",
              "memo": "memo",
              "public_key": "key",
              "node_cert_hash": "hash",
              "file_id": "0.0.102",
              "decline_reward": false,
              "max_stake": 100,
              "min_stake": 1,
              "stake": 50,
              "stake_not_rewarded": 10,
              "stake_rewarded": 40,
              "reward_rate_start": 2,
              "staking_period": null,
              "timestamp": {"from": "1234567890.000000001", "to": null},
              "service_endpoints": [
                {"ip_address_v4": "127.0.0.1", "domain_name": null, "port": 50211}
              ],
              "admin_key": "admin"
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
