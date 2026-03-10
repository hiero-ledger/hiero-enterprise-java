package org.hiero.microprofile.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.hiero.base.config.ConsensusNode;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.config.NetworkSettings;
import org.hiero.base.data.Account;
import org.hiero.microprofile.HieroNetworkConfiguration;
import org.hiero.microprofile.HieroOperatorConfiguration;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HieroConfigImpl implements HieroConfig {

  private static final Logger log = LoggerFactory.getLogger(HieroConfigImpl.class);

  private final Account operatorAccount;

  private final String networkName;

  private final Set<String> mirrorNodeAddresses;

  private final Set<ConsensusNode> consensusNodes;

  private final Long chainId;

  private final String relayUrl;

  private final Long requestTimeoutInMs;

  public HieroConfigImpl(
      @NonNull final HieroOperatorConfiguration configuration,
      @NonNull final HieroNetworkConfiguration networkConfiguration) {
    Objects.requireNonNull(configuration, "configuration must not be null");
    Objects.requireNonNull(networkConfiguration, "networkConfiguration must not be null");

    final AccountId operatorAccountId = AccountId.fromString(configuration.getAccountId());
    final PrivateKey operatorPrivateKey = PrivateKey.fromString(configuration.getPrivateKey());
    operatorAccount = Account.of(operatorAccountId, operatorPrivateKey);
    requestTimeoutInMs = networkConfiguration.getRequestTimeoutInMs().orElse(null);
    final Optional<NetworkSettings> networkSettings =
        networkConfiguration
            .getName()
            .map(name -> NetworkSettings.forIdentifier(name))
            .map(settings -> settings.orElse(null));
    if (networkSettings.isPresent()) {
      final NetworkSettings settings = networkSettings.get();
      networkName = settings.getNetworkName().orElse(networkConfiguration.getName().orElse(null));
      mirrorNodeAddresses = Collections.unmodifiableSet(settings.getMirrorNodeAddresses());
      consensusNodes = Collections.unmodifiableSet(settings.getConsensusNodes());
      chainId = settings.chainId().orElse(null);
      relayUrl = settings.relayUrl().orElse(null);
    } else {
      networkName = networkConfiguration.getName().orElse(null);
      mirrorNodeAddresses = networkConfiguration.getMirrornode().map(Set::of).orElse(Set.of());
      consensusNodes = Collections.unmodifiableSet(networkConfiguration.getNodes());
      chainId = null;
      relayUrl = null;
    }
  }

  @Override
  public Optional<Duration> getRequestTimeout() {
    return Optional.ofNullable(requestTimeoutInMs).map(Duration::ofMillis);
  }

  @Override
  public @NonNull Account getOperatorAccount() {
    return operatorAccount;
  }

  @Override
  public @NonNull Optional<String> getNetworkName() {
    return Optional.ofNullable(networkName);
  }

  @Override
  public @NonNull Set<String> getMirrorNodeAddresses() {
    return mirrorNodeAddresses;
  }

  @Override
  public @NonNull Set<ConsensusNode> getConsensusNodes() {
    return consensusNodes;
  }

  @Override
  public @NonNull Optional<Long> chainId() {
    return Optional.empty();
  }

  @Override
  public @NonNull Optional<String> relayUrl() {
    return Optional.empty();
  }
}
