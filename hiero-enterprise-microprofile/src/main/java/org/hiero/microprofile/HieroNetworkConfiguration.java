package org.hiero.microprofile;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hiero.base.config.ConsensusNode;

@ConfigProperties(prefix = "hiero.network")
@Dependent
public class HieroNetworkConfiguration {

  @ConfigProperty(name = "name")
  private Optional<String> name;

  @Inject
  @ConfigProperty(name = "nodes")
  private Optional<String> nodes;

  @ConfigProperty(name = "request-timeout")
  private Optional<Long> requestTimeoutInMs;

  public MirrorNode mirrorNode = new MirrorNode();

  public Optional<Long> getRequestTimeoutInMs() {
    return requestTimeoutInMs;
  }

  public Optional<String> getName() {
    return name;
  }

  public MirrorNode getMirrornode() {
    return mirrorNode;
  }

  public Set<ConsensusNode> getNodes() {
    return nodes
        .map(n -> n.split(","))
        .map(n -> Stream.of(n))
        .orElse(Stream.empty())
        .map(
            n -> {
              // 172.234.134.4:8080:0.0.3
              final String[] split = n.split(":");
              if (split.length != 3) {
                throw new IllegalStateException("Can not parse node for '" + n + "'");
              }
              final String ip = split[0];
              final String port = split[1];
              final String account = split[2];
              return new ConsensusNode(ip, port, account);
            })
        .collect(Collectors.toUnmodifiableSet());
  }

  public static class MirrorNode {
    @ConfigProperty(name = "hiero.network.mirrornode.rest-url")
    Optional<String> restUrl = Optional.empty();

    @ConfigProperty(name = "hiero.network.mirrornode.grpc-addresses")
    public Optional<String> grpcAddresses = Optional.empty();

    public Optional<String> getRestUrl() {
      return restUrl;
    }

    public Set<String> getGrpcAddresses() {
      // eg: testnet:mirrornode:443,testnet:5600
      return grpcAddresses
          .map(n -> n.split(","))
          .map(n -> Stream.of(n))
          .orElse(Stream.empty())
          .collect(Collectors.toUnmodifiableSet());
    }
  }
}
