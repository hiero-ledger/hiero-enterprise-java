package org.hiero.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;

/** Utility class to assist with Spring Boot integration for Hiero Testcontainers. */
public final class HieroTestcontainers {

  private HieroTestcontainers() {}

  /**
   * Registers the necessary properties for a HieroContainer in the given DynamicPropertyRegistry.
   * This allows Spring Boot applications to automatically connect to the container.
   *
   * @param registry the DynamicPropertyRegistry to register properties with
   * @param container the HieroContainer instance
   */
  public static void registerProperties(
      DynamicPropertyRegistry registry, HieroContainer container) {
    registry.add("spring.hiero.network.nodes[0].ip", container::getConsensusIp);
    registry.add("spring.hiero.network.nodes[0].port", container::getConsensusPort);
    registry.add("spring.hiero.network.nodes[0].account", container::getConsensusAccount);
    registry.add("spring.hiero.network.mirror-node", container::getMirrorRestEndpoint);
    registry.add("spring.hiero.accountId", container::getOperatorId);
    registry.add("spring.hiero.privateKey", container::getOperatorKey);
  }

  /**
   * Registers the necessary properties for a MirrorNodeContainer in the given
   * DynamicPropertyRegistry.
   *
   * @param registry the DynamicPropertyRegistry to register properties with
   * @param container the MirrorNodeContainer instance
   */
  public static void registerMirrorNodeProperties(
      DynamicPropertyRegistry registry, MirrorNodeContainer container) {
    registry.add("spring.hiero.network.mirror-node", container::getRestEndpoint);
  }
}
