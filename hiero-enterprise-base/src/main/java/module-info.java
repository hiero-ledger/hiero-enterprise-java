import org.hiero.base.config.NetworkSettingsProvider;
import org.hiero.base.config.hedera.HederaNetworkSettingsProvider;

module org.hiero.base {
  exports org.hiero.base;
  exports org.hiero.base.protocol;
  exports org.hiero.base.mirrornode;
  exports org.hiero.base.verification;
  exports org.hiero.base.data;
  exports org.hiero.base.config;
  exports org.hiero.base.implementation to
      org.hiero.base.test;
  exports org.hiero.base.implementation.data to
      org.hiero.base.test;
  exports org.hiero.base.config.implementation;
  exports org.hiero.base.protocol.data;
  exports org.hiero.base.interceptors to
      org.hiero.base.test;

  uses NetworkSettingsProvider;

  provides NetworkSettingsProvider with
      HederaNetworkSettingsProvider;

  requires transitive com.hedera.hashgraph.sdk; // Hedera SDK
  requires org.slf4j;
  requires com.google.protobuf; // TODO: We should not have the need to use it
  requires static org.jspecify;
  requires com.google.auto.service;
}
