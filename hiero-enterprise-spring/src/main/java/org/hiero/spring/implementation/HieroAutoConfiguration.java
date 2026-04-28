package org.hiero.spring.implementation;

import java.net.URI;
import java.net.URL;
import java.util.List;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.NftClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
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
import org.hiero.base.implementation.TopicClientImpl;
import org.hiero.base.implementation.TopicRepositoryImpl;
import org.hiero.base.implementation.TransactionRepositoryImpl;
import org.hiero.base.interceptors.ReceiveRecordInterceptor;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.NetworkRepository;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.mirrornode.TokenRepository;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.verification.ContractVerificationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.annotation.ApplicationScope;

@AutoConfiguration
@EnableConfigurationProperties({HieroProperties.class, HieroNetworkProperties.class})
@Import({MicrometerSupportConfig.class})
public class HieroAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(HieroAutoConfiguration.class);

  @Bean
  @ApplicationScope
  HieroConfig hieroConfig(final HieroProperties properties) {
    return new HieroConfigImpl(properties);
  }

  @Bean
  @ApplicationScope
  HieroContext hieroContext(final HieroConfig hieroConfig) {
    return hieroConfig.createHieroContext();
  }

  @Bean
  ProtocolLayerClient protocolLevelClient(
      final HieroContext hieroContext,
      @Autowired(required = false) final ReceiveRecordInterceptor interceptor) {
    ProtocolLayerClientImpl protocolLayerClient = new ProtocolLayerClientImpl(hieroContext);
    if (interceptor != null) {
      protocolLayerClient.setRecordInterceptor(interceptor);
    }
    return protocolLayerClient;
  }

  @Bean
  FileClient fileClient(final ProtocolLayerClient protocolLayerClient) {
    return new FileClientImpl(protocolLayerClient);
  }

  @Bean
  SmartContractClient smartContractClient(
      final ProtocolLayerClient protocolLayerClient, FileClient fileClient) {
    return new SmartContractClientImpl(protocolLayerClient, fileClient);
  }

  @Bean
  AccountClient accountClient(final ProtocolLayerClient protocolLayerClient) {
    return new AccountClientImpl(protocolLayerClient);
  }

  @Bean
  NftClient nftClient(final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new NftClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  FungibleTokenClient tokenClient(
      final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new FungibleTokenClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  TopicClient topicClient(
      final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new TopicClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  MirrorNodeClient mirrorNodeClient(final HieroContext hieroContext) {
    final String mirrorNodeEndpoint;
    final List<String> mirrorNetwork = hieroContext.getClient().getMirrorNetwork();
    if (mirrorNetwork.isEmpty()) {
      throw new IllegalArgumentException("Mirror node endpoint must be set");
    }
    mirrorNodeEndpoint = mirrorNetwork.get(0);
    final String baseUri;
    try {
      URL url = new URI(mirrorNodeEndpoint).toURL();
      final String mirrorNodeEndpointProtocol = url.getProtocol();
      final String mirrorNodeEndpointHost = url.getHost();
      final int mirrorNodeEndpointPort;
      if (mirrorNodeEndpointProtocol == "https" && url.getPort() == -1) {
        mirrorNodeEndpointPort = 443;
      } else if (mirrorNodeEndpointProtocol == "http" && url.getPort() == -1) {
        mirrorNodeEndpointPort = 80;
      } else if (url.getPort() == -1) {
        mirrorNodeEndpointPort = 443;
      } else {
        mirrorNodeEndpointPort = url.getPort();
      }
      baseUri =
          mirrorNodeEndpointProtocol
              + "://"
              + mirrorNodeEndpointHost
              + ":"
              + mirrorNodeEndpointPort;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Error parsing mirrorNodeEndpoint '" + mirrorNodeEndpoint + "'", e);
    }
    RestClient.Builder builder = RestClient.builder().baseUrl(baseUri);
    return new MirrorNodeClientImpl(builder);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  NftRepository nftRepository(final MirrorNodeClient mirrorNodeClient) {
    return new NftRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  ContractRepository contractRepository(final MirrorNodeClient mirrorNodeClient) {
    return new ContractRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  AccountRepository accountRepository(final MirrorNodeClient mirrorNodeClient) {
    return new AccountRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  NetworkRepository networkRepository(final MirrorNodeClient mirrorNodeClient) {
    return new NetworkRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TokenRepository tokenRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TokenRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TransactionRepository transactionRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TransactionRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TopicRepository topicRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TopicRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  ContractVerificationClient contractVerificationClient(final HieroConfig hieroConfig) {
    return new ContractVerificationClientImplementation(hieroConfig);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  BlockRepository blockRepository(final MirrorNodeClient mirrorNodeClient) {
    return new BlockRepositoryImpl(mirrorNodeClient);
  }
}
