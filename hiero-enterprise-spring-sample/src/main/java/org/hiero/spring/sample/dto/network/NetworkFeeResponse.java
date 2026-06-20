package org.hiero.spring.sample.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hiero.base.data.NetworkFee;

/** Response DTO for Network Fees. */
@Schema(
    name = "Network: Fee",
    description = "Response DTO containing network transaction fee information.")
public record NetworkFeeResponse(
    @Schema(description = "The gas cost associated with the transaction.") long gas,
    @Schema(description = "The type of transaction.") String transactionType) {
  public static NetworkFeeResponse fromDomain(NetworkFee fee) {
    return new NetworkFeeResponse(fee.gas(), fee.transactionType());
  }
}
