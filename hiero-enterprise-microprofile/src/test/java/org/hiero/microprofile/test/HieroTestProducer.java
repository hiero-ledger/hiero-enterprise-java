package org.hiero.microprofile.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.test.HieroTestUtils;

@ApplicationScoped
public class HieroTestProducer {

  @Produces
  @Dependent
  public HieroTestUtils createHieroTestUtils(
      MirrorNodeClient mirrorNodeClient, ProtocolLayerClient protocolLayerClient) {
    return new HieroTestUtils(mirrorNodeClient, protocolLayerClient);
  }
}
