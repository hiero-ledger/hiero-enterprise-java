package org.hiero.testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class HieroContainerTest {

  @Test
  void testContainerDefaults() {
    HieroContainer container = new HieroContainer();
    assertEquals("hashgraph/hedera-local-node:latest", container.getDockerImageName());
    assertEquals("0.0.3", container.getConsensusAccount());
    assertEquals("0.0.2", container.getOperatorId());
    assertNotNull(container.getOperatorKey());
  }

  @Test
  void testMirrorNodeContainerDefaults() {
    MirrorNodeContainer container = new MirrorNodeContainer();
    assertEquals("hashgraph/hedera-local-node:latest", container.getDockerImageName());
  }
}
