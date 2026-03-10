package org.hiero.base.test.config;

import java.util.Set;
import org.hiero.base.config.NetworkSettings;
import org.hiero.base.config.NetworkSettingsProvider;

public class SoloActionNetworkSettingsProvider implements NetworkSettingsProvider {

  @Override
  public String getName() {
    return "Provider for Hiero Solo Action";
  }

  @Override
  public Set<NetworkSettings> createNetworkSettings() {
    return Set.of(new SoloActionNetworkSettings());
  }
}
