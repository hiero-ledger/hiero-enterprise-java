package org.hiero.base.sample;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.hiero.base.HieroContext;
import org.hiero.base.config.NetworkSettings;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;

public class CliHieroContext implements HieroContext {

  private final Account operationalAccount;
  private final Client client;

  public CliHieroContext(final String accountIdStr, final String privateKeyStr, final String network) {
    final AccountId accountId = AccountId.fromString(accountIdStr);
    final PrivateKey privateKey = PrivateKey.fromString(privateKeyStr);
    final PublicKey publicKey = privateKey.getPublicKey();
    operationalAccount = new Account(accountId, publicKey, privateKey);

    final NetworkSettings networkSettings =
        NetworkSettings.forIdentifier(network)
            .orElseThrow(
                () -> new IllegalStateException("Unknown network: " + network));

    final Map<String, AccountId> nodes = new HashMap<>();
    networkSettings
        .getConsensusNodes()
        .forEach(n -> nodes.put(n.getAddress(), n.getAccountId()));

    client = Client.forNetwork(nodes);
    if (!networkSettings.getMirrorNodeAddresses().isEmpty()) {
      try {
        client.setMirrorNetwork(networkSettings.getMirrorNodeAddresses().stream().toList());
      } catch (InterruptedException e) {
        throw new RuntimeException("Error configuring Mirror Node", e);
      }
    }
    client.setOperator(accountId, privateKey);
  }

  @Override
  public @NonNull Account getOperatorAccount() {
    return operationalAccount;
  }

  @Override
  public @NonNull Client getClient() {
    return client;
  }
}
