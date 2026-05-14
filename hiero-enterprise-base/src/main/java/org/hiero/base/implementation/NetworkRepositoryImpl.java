package org.hiero.base.implementation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroBaseException;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.NetworkRepository;
import org.jspecify.annotations.NonNull;

public class NetworkRepositoryImpl implements NetworkRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public NetworkRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public Optional<ExchangeRates> exchangeRates() throws HieroBaseException {
    return mirrorNodeClient.queryExchangeRates();
  }

  @Override
  public List<NetworkFee> fees() throws HieroBaseException {
    return mirrorNodeClient.queryNetworkFees();
  }

  @Override
  public Optional<NetworkStake> stake() throws HieroBaseException {
    return mirrorNodeClient.queryNetworkStake();
  }

  @Override
  public Optional<NetworkSupplies> supplies() throws HieroBaseException {
    return mirrorNodeClient.queryNetworkSupplies();
  }
}
