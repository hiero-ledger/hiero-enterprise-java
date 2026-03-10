import org.hiero.base.config.NetworkSettingsProvider;
import org.hiero.test.implementation.SoloActionNetworkSettingsProvider;

open module org.hiero.test {
  exports org.hiero.test;

  provides NetworkSettingsProvider with
      SoloActionNetworkSettingsProvider;

  requires transitive org.hiero.base;
  requires org.jspecify;
  requires com.google.auto.service;
  requires org.slf4j;
}
