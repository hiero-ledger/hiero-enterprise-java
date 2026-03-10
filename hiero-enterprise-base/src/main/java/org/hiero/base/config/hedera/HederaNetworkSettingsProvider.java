package org.hiero.base.config.hedera;

import com.google.auto.service.AutoService;
import java.util.Set;
import org.hiero.base.config.NetworkSettings;
import org.hiero.base.config.NetworkSettingsProvider;

/** Provides network settings for the Hedera networks. */
@AutoService(NetworkSettingsProvider.class)
public final class HederaNetworkSettingsProvider implements NetworkSettingsProvider {

  @Override
  public String getName() {
    return "Hedera";
  }

  @Override
  public Set<NetworkSettings> createNetworkSettings() {
    return Set.of(new HederaMainnetSettings(), new HederaTestnetSettings());
  }
}
