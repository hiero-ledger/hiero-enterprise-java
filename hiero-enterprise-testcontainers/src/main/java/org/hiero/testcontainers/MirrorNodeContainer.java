package org.hiero.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers implementation for Hiero Mirror Node. This container provides Mirror Node REST and
 * gRPC services. Note: This implementation currently uses the hedera-local-node image as it
 * provides a pre-configured mirror node environment.
 */
public class MirrorNodeContainer extends GenericContainer<MirrorNodeContainer> {

  public static final String DEFAULT_IMAGE_NAME = "hashgraph/hedera-local-node";
  public static final String DEFAULT_TAG = "latest";

  private static final int REST_PORT = 5551;
  private static final int GRPC_PORT = 5600;

  public MirrorNodeContainer() {
    this(DockerImageName.parse(DEFAULT_IMAGE_NAME).withTag(DEFAULT_TAG));
  }

  public MirrorNodeContainer(String image) {
    this(DockerImageName.parse(image));
  }

  public MirrorNodeContainer(DockerImageName dockerImageName) {
    super(dockerImageName);
    withExposedPorts(REST_PORT, GRPC_PORT);
    waitingFor(Wait.forHttp("/health").forPort(REST_PORT));
  }

  /**
   * Returns the mirror node REST endpoint.
   *
   * @return the REST endpoint URL
   */
  public String getRestEndpoint() {
    return "http://" + getHost() + ":" + getMappedPort(REST_PORT);
  }

  /**
   * Returns the mirror node gRPC endpoint.
   *
   * @return the gRPC endpoint (host:port)
   */
  public String getGrpcEndpoint() {
    return getHost() + ":" + getMappedPort(GRPC_PORT);
  }
}
