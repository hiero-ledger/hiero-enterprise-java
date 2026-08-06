package org.hiero.spring.implementation;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "spring.hiero.network")
public class HieroNetworkProperties {

  /**
   * Network name (must be hedera-mainnet, hedera-testnet or hedera-previewnet). Default is
   * hedera-mainnet. Will be ignored if nodes are provided.
   */
  private String name = "hedera-mainnet";

  /**
   * List of nodes to connect to. If provided, network name will be ignored. This can be used to
   * connect to a custom network (like a Solo instance).
   */
  private List<HieroNode> nodes;

  /** Mirror node endpoint to connect to. */
  @NestedConfigurationProperty private MirrorNode mirrorNode = new MirrorNode();

  private Long requestTimeoutInMs;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MirrorNode getMirrorNode() {
    return mirrorNode;
  }

  public void setMirrorNode(MirrorNode mirrorNode) {
    this.mirrorNode = mirrorNode;
  }

  public List<HieroNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<HieroNode> nodes) {
    this.nodes = nodes;
  }

  public Long getRequestTimeoutInMs() {
    return requestTimeoutInMs;
  }

  public void setRequestTimeoutInMs(Long requestTimeoutInMs) {
    this.requestTimeoutInMs = requestTimeoutInMs;
  }

  public static class MirrorNode {
    /** Mirror node RestApi endpoint. */
    private String restUrl;

    /** List of mirror node gRPC endpoints. */
    private List<String> grpcAddresses;

    public String getRestUrl() {
      return restUrl;
    }

    public void setRestUrl(String restUrl) {
      this.restUrl = restUrl;
    }

    public List<String> getGrpcAddresses() {
      return grpcAddresses;
    }

    public void setGrpcAddresses(List<String> grpcAddresses) {
      this.grpcAddresses = grpcAddresses;
    }
  }
}
