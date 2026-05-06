package org.hiero.spring.sample.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hiero.base.data.NetworkSupplies;

/**
 * Response DTO for Network Supplies.
 */
@Schema(name = "Network: Supplies", description = "Response DTO containing Hbar supply information.")
public record NetworkSuppliesResponse(
    @Schema(description = "The released supply of Hbar.")
    String releasedSupply,
    @Schema(description = "The total supply of Hbar.")
    String totalSupply
) {
  public static NetworkSuppliesResponse fromDomain(NetworkSupplies supplies) {
    return new NetworkSuppliesResponse(supplies.releasedSupply(), supplies.totalSupply());
  }
}
