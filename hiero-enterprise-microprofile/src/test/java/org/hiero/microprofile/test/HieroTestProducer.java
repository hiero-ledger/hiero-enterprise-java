package org.hiero.microprofile.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.test.HieroTestUtils;

@ApplicationScoped
public class HieroTestProducer {

  @Produces
  @ApplicationScoped
  public HieroTestUtils createHieroTestUtils(
      MirrorNodeClient mirrorNodeClient, ProtocolLayerClient protocolLayerClient) {
    return new HieroTestUtils(mirrorNodeClient, protocolLayerClient);
  }
}
