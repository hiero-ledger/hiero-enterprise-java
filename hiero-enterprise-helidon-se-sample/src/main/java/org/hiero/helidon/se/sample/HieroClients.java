package org.hiero.helidon.se.sample;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.config.implementation.NetworkSettingsBasedHieroConfig;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.FileClientImpl;
import org.hiero.base.implementation.FungibleTokenClientImpl;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.implementation.SmartContractClientImpl;
import org.hiero.base.implementation.TopicClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;

final class HieroClients {

  private final TopicClient topicClient;
  private final FungibleTokenClient tokenClient;
  private final SmartContractClient smartContractClient;

  private HieroClients(
      final TopicClient topicClient,
      final FungibleTokenClient tokenClient,
      final SmartContractClient smartContractClient) {
    this.topicClient = Objects.requireNonNull(topicClient, "topicClient must not be null");
    this.tokenClient = Objects.requireNonNull(tokenClient, "tokenClient must not be null");
    this.smartContractClient =
        Objects.requireNonNull(smartContractClient, "smartContractClient must not be null");
  }

  static HieroClients fromEnv() {
    final Map<String, String> env = System.getenv();
    final String accountId = requireEnv(env, "HEDERA_ACCOUNT_ID");
    final String privateKeyValue = requireEnv(env, "HEDERA_PRIVATE_KEY");
    final String network = env.getOrDefault("HEDERA_NETWORK", "hedera-testnet");

    final PrivateKey privateKey = PrivateKey.fromString(privateKeyValue);
    final Account operatorAccount = Account.of(AccountId.fromString(accountId), privateKey);

    final HieroConfig config = new NetworkSettingsBasedHieroConfig(operatorAccount, network);
    final HieroContext context = config.createHieroContext();
    final ProtocolLayerClient protocolLayerClient = new ProtocolLayerClientImpl(context);
    final FileClient fileClient = new FileClientImpl(protocolLayerClient);

    return new HieroClients(
        new TopicClientImpl(protocolLayerClient, context.getOperatorAccount()),
        new FungibleTokenClientImpl(protocolLayerClient, context.getOperatorAccount()),
        new SmartContractClientImpl(protocolLayerClient, fileClient));
  }

  private static String requireEnv(final Map<String, String> env, final String key) {
    final String value = env.get(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing environment variable: " + key);
    }
    return value;
  }

  TopicClient topicClient() {
    return topicClient;
  }

  FungibleTokenClient tokenClient() {
    return tokenClient;
  }

  SmartContractClient smartContractClient() {
    return smartContractClient;
  }
}
