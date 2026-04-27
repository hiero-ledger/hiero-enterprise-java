package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.Optional;
import java.util.Set;
import org.hiero.base.config.ConsensusNode;
import org.hiero.microprofile.HieroNetworkConfiguration;
import org.hiero.microprofile.HieroOperatorConfiguration;
import org.hiero.microprofile.implementation.HieroConfigImpl;
import org.junit.jupiter.api.Test;

class HieroConfigImplTest {

  @Test
  void shouldExposeChainIdAndRelayUrlFromKnownNetworkSettings() {
    final HieroOperatorConfiguration operatorConfiguration =
        new HieroOperatorConfiguration() {
          @Override
          public String getAccountId() {
            return "0.0.1001";
          }

          @Override
          public String getPrivateKey() {
            return PrivateKey.generateED25519().toString();
          }
        };

    final HieroNetworkConfiguration networkConfiguration =
        new HieroNetworkConfiguration() {
          @Override
          public Optional<String> getName() {
            return Optional.of("hedera-testnet");
          }

          @Override
          public Optional<String> getMirrornode() {
            return Optional.empty();
          }

          @Override
          public Optional<Long> getRequestTimeoutInMs() {
            return Optional.empty();
          }

          @Override
          public Set<ConsensusNode> getNodes() {
            return Set.of();
          }
        };

    final HieroConfigImpl config = new HieroConfigImpl(operatorConfiguration, networkConfiguration);

    assertEquals(Optional.of(296L), config.chainId());
    assertEquals(Optional.of("https://testnet.hashio.io/api"), config.relayUrl());
  }
}
