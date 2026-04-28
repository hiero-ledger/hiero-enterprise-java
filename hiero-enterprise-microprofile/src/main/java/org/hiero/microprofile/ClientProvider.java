package org.hiero.microprofile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.NftClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.implementation.AccountClientImpl;
import org.hiero.base.implementation.AccountRepositoryImpl;
import org.hiero.base.implementation.BlockRepositoryImpl;
import org.hiero.base.implementation.ContractRepositoryImpl;
import org.hiero.base.implementation.FileClientImpl;
import org.hiero.base.implementation.FungibleTokenClientImpl;
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
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.jspecify.annotations.NonNull;

public class ClientProvider {

  @Inject @ConfigProperties private HieroOperatorConfiguration configuration;

  @Inject @ConfigProperties private HieroNetworkConfiguration networkConfiguration;

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
    return new ProtocolLayerClientImpl(hieroContext);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FileClient createFileClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    return new FileClientImpl(protocolLayerClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  SmartContractClient createSmartContractClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final FileClient fileClient) {
    return new SmartContractClientImpl(protocolLayerClient, fileClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftClient createNftClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    return new NftClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FungibleTokenClient createFungibleTokenClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    return new FungibleTokenClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountClient createAccountClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    return new AccountClientImpl(protocolLayerClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractVerificationClient createContractVerificationClient(
      @NonNull final HieroConfig hieroConfig) {
    return new ContractVerificationClientImpl(hieroConfig);
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
    return new MirrorNodeClientImpl(restClient, jsonConverter);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  BlockRepository createBlockRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new BlockRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountRepository createAccountRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new AccountRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NetworkRepository createNetworkRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new NetworkRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftRepository createNftRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new NftRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TransactionRepository createTransactionRepository(
      @NonNull final MirrorNodeClient mirrorNodeClient) {
    return new TransactionRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TokenRepository createTokenRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new TokenRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractRepository createContractRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new ContractRepositoryImpl(mirrorNodeClient);
  }
}
