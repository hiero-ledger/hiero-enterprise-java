package org.hiero.base.config.hedera;

import java.util.Optional;
import java.util.Set;
import org.hiero.base.config.ConsensusNode;
import org.hiero.base.config.NetworkSettings;
import org.jspecify.annotations.NonNull;

/** Network settings for the Hedera Testnet. */
public final class HederaTestnetSettings implements NetworkSettings {

  /** The network identifier. */
  public static final String NETWORK_IDENTIFIER = "hedera-testnet";

  @Override
  public @NonNull String getNetworkIdentifier() {
    return NETWORK_IDENTIFIER;
  }

  @Override
  public @NonNull Optional<String> getNetworkName() {
    return Optional.of("Hedera Testnet");
  }

  @Override
  public @NonNull Set<String> getMirrorNodeAddresses() {
    return Set.of("https://testnet.mirrornode.hedera.com:443");
  }

  @Override
  public @NonNull Set<ConsensusNode> getConsensusNodes() {
    return Set.of(new ConsensusNode("0.testnet.hedera.com", "50211", "0.0.3"));
  }

  @Override
  public @NonNull Optional<Long> chainId() {
    return Optional.of(296L);
  }

  @Override
  public @NonNull Optional<String> relayUrl() {
    return Optional.of("https://testnet.hashio.io/api");
  }
}
