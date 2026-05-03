package org.hiero.microprofile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.HookClient;
import org.hiero.base.NftClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.implementation.AccountClientImpl;
import org.hiero.base.implementation.AccountRepositoryImpl;
import org.hiero.base.implementation.BlockRepositoryImpl;
import org.hiero.base.implementation.ContractRepositoryImpl;
import org.hiero.base.implementation.FileClientImpl;
import org.hiero.base.implementation.FungibleTokenClientImpl;
import org.hiero.base.implementation.HookClientImpl;
import org.hiero.base.implementation.NetworkRepositoryImpl;
import org.hiero.base.implementation.NftClientImpl;
import org.hiero.base.implementation.NftRepositoryImpl;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.implementation.SmartContractClientImpl;
import org.hiero.base.implementation.TokenRepositoryImpl;
import org.hiero.base.implementation.TransactionRepositoryImpl;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.NetworkRepository;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.mirrornode.TokenRepository;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.microprofile.implementation.ContractVerificationClientImpl;
import org.hiero.microprofile.implementation.HieroConfigImpl;
import org.hiero.microprofile.implementation.HieroMetricsProxyFactory;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.jspecify.annotations.NonNull;

public class ClientProvider {

  @Inject @ConfigProperties private HieroOperatorConfiguration configuration;

  @Inject @ConfigProperties private HieroNetworkConfiguration networkConfiguration;
  @Inject private Instance<MetricRegistry> metricRegistry;

  @NonNull
  @Produces
  @ApplicationScoped
  HieroConfig createHieroConfig() {
    return new HieroConfigImpl(configuration, networkConfiguration);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  HieroContext createHieroContext(@NonNull final HieroConfig hieroConfig) {
    return hieroConfig.createHieroContext();
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ProtocolLayerClient createProtocolLayerClient(@NonNull final HieroContext hieroContext) {
    final ProtocolLayerClient client = new ProtocolLayerClientImpl(hieroContext);
    return wrapClient(client, ProtocolLayerClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FileClient createFileClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    final FileClient client = new FileClientImpl(protocolLayerClient);
    return wrapClient(client, FileClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  SmartContractClient createSmartContractClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final FileClient fileClient) {
    final SmartContractClient client = new SmartContractClientImpl(protocolLayerClient, fileClient);
    return wrapClient(client, SmartContractClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftClient createNftClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    final NftClient client =
        new NftClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
    return wrapClient(client, NftClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FungibleTokenClient createFungibleTokenClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    final FungibleTokenClient client =
        new FungibleTokenClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
    return wrapClient(client, FungibleTokenClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountClient createAccountClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    final AccountClient client = new AccountClientImpl(protocolLayerClient);
    return wrapClient(client, AccountClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  HookClient createHookClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    final HookClient client = new HookClientImpl(protocolLayerClient);
    return wrapClient(client, HookClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractVerificationClient createContractVerificationClient(
      @NonNull final HieroConfig hieroConfig) {
    final ContractVerificationClient client = new ContractVerificationClientImpl(hieroConfig);
    return wrapClient(client, ContractVerificationClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  MirrorNodeClient createMirrorNodeClient(@NonNull final HieroConfig hieroConfig) {
    final String target =
        hieroConfig.getMirrorNodeAddresses().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No mirror node addresses configured"));
    final MirrorNodeRestClientImpl restClient = new MirrorNodeRestClientImpl(target);
    final MirrorNodeJsonConverterImpl jsonConverter = new MirrorNodeJsonConverterImpl();
    final MirrorNodeClient client = new MirrorNodeClientImpl(restClient, jsonConverter);
    return wrapClient(client, MirrorNodeClient.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  BlockRepository createBlockRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final BlockRepository repository = new BlockRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, BlockRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountRepository createAccountRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final AccountRepository repository = new AccountRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, AccountRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NetworkRepository createNetworkRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final NetworkRepository repository = new NetworkRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, NetworkRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftRepository createNftRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final NftRepository repository = new NftRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, NftRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TransactionRepository createTransactionRepository(
      @NonNull final MirrorNodeClient mirrorNodeClient) {
    final TransactionRepository repository = new TransactionRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, TransactionRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TokenRepository createTokenRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final TokenRepository repository = new TokenRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, TokenRepository.class);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractRepository createContractRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    final ContractRepository repository = new ContractRepositoryImpl(mirrorNodeClient);
    return wrapRepository(repository, ContractRepository.class);
  }

  private <T> T wrapClient(@NonNull final T target, @NonNull final Class<T> type) {
    if (metricRegistry.isResolvable()) {
      final MetricRegistry registry = metricRegistry.get();
      return new HieroMetricsProxyFactory(registry).createProxy(target, type, "client");
    }
    return target;
  }

  private <T> T wrapRepository(@NonNull final T target, @NonNull final Class<T> type) {
    if (metricRegistry.isResolvable()) {
      final MetricRegistry registry = metricRegistry.get();
      return new HieroMetricsProxyFactory(registry).createProxy(target, type, "repository");
    }
    return target;
  }
}
