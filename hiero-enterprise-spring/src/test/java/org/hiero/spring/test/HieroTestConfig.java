package org.hiero.spring.test;

import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.spring.EnableHiero;
import org.hiero.test.HieroTestUtils;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableHiero
@SpringBootConfiguration
@ComponentScan
public class HieroTestConfig {

  @Bean
  HieroTestUtils hieroTestUtils(
      MirrorNodeClient mirrorNodeClient, ProtocolLayerClient protocolLayerClient) {
    HieroTestUtils testUtils = new HieroTestUtils(mirrorNodeClient, protocolLayerClient);
    return testUtils;
  }
}
