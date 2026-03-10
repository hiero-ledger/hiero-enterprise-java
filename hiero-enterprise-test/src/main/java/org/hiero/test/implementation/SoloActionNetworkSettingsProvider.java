package org.hiero.test.implementation;

import com.google.auto.service.AutoService;
import java.util.Set;
import org.hiero.base.config.NetworkSettings;
import org.hiero.base.config.NetworkSettingsProvider;
import org.hiero.test.SoloActionNetworkSettings;

@AutoService(NetworkSettingsProvider.class)
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
