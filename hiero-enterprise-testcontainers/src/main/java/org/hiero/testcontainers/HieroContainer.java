package org.hiero.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/** Testcontainers implementation for Hiero (Hedera) Local Node. */
public class HieroContainer extends GenericContainer<HieroContainer> {

  public static final String DEFAULT_IMAGE_NAME = "hashgraph/hedera-local-node";
  public static final String DEFAULT_TAG = "latest";

  private static final int CONSENSUS_GRPC_PORT = 50211;
  private static final int MIRROR_NODE_REST_PORT = 5551;
  private static final int MIRROR_NODE_GRPC_PORT = 5600;

  public HieroContainer() {
    this(DockerImageName.parse(DEFAULT_IMAGE_NAME).withTag(DEFAULT_TAG));
  }

  public HieroContainer(String image) {
    this(DockerImageName.parse(image));
  }

  public HieroContainer(DockerImageName dockerImageName) {
    super(dockerImageName);
    withExposedPorts(CONSENSUS_GRPC_PORT, MIRROR_NODE_REST_PORT, MIRROR_NODE_GRPC_PORT);
    waitingFor(Wait.forHttp("/health").forPort(MIRROR_NODE_REST_PORT));
  }

  /**
   * Returns the consensus node IP address.
   *
   * @return the IP address
   */
  public String getConsensusIp() {
    return getHost();
  }

  /**
   * Returns the consensus node port.
   *
   * @return the port
   */
  public int getConsensusPort() {
    return getMappedPort(CONSENSUS_GRPC_PORT);
  }

  /**
   * Returns the consensus node account ID. Default for local node is usually 0.0.3.
   *
   * @return the account ID
   */
  public String getConsensusAccount() {
    return "0.0.3";
  }

  /**
   * Returns the mirror node REST endpoint.
   *
   * @return the REST endpoint URL
   */
  public String getMirrorRestEndpoint() {
    return "http://" + getHost() + ":" + getMappedPort(MIRROR_NODE_REST_PORT);
  }

  /**
   * Returns the mirror node gRPC endpoint.
   *
   * @return the gRPC endpoint (host:port)
   */
  public String getMirrorGrpcEndpoint() {
    return getHost() + ":" + getMappedPort(MIRROR_NODE_GRPC_PORT);
  }

  /**
   * Returns the default operator account ID for local node.
   *
   * @return the operator account ID
   */
  public String getOperatorId() {
    return "0.0.2";
  }

  /**
   * Returns the default operator private key for local node.
   *
   * @return the operator private key
   */
  public String getOperatorKey() {
    return "302e020100300506032b65700422042091132178e72057a1d7528025956fe30b0b847f8cdd2fab28edc77343657bc4c4";
  }
}
