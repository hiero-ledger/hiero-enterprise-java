package org.hiero.spring.sample.dto.network;

import org.hiero.base.data.NetworkSupplies;

/**
 * Response DTO for Network Supplies.
 */
public record NetworkSuppliesResponse(
    String releasedSupply,
    String totalSupply
) {
  public static NetworkSuppliesResponse fromDomain(NetworkSupplies supplies) {
    return new NetworkSuppliesResponse(supplies.releasedSupply(), supplies.totalSupply());
  }
}
