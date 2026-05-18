package org.hiero.base.implementation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkNode;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Page;
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
  public Optional<ExchangeRates> exchangeRates() throws HieroException {
    return mirrorNodeClient.queryExchangeRates();
  }

  @Override
  public List<NetworkFee> fees() throws HieroException {
    return mirrorNodeClient.queryNetworkFees();
  }

  @Override
  public Optional<NetworkStake> stake() throws HieroException {
    return mirrorNodeClient.queryNetworkStake();
  }

  @Override
  public Optional<NetworkSupplies> supplies() throws HieroException {
    return mirrorNodeClient.queryNetworkSupplies();
  }

  @Override
  public Page<NetworkNode> nodes() throws HieroException {
    return mirrorNodeClient.queryNetworkNodes();
  }

  @Override
  public Optional<NetworkNode> nodeById(long nodeId) throws HieroException {
    return mirrorNodeClient.queryNetworkNodeById(nodeId);
  }
}
