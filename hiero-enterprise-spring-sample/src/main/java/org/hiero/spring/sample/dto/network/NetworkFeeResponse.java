package org.hiero.spring.sample.dto.network;

import org.hiero.base.data.NetworkFee;

/**
 * Response DTO for Network Fees.
 */
public record NetworkFeeResponse(
    long gas,
    String transactionType
) {
  public static NetworkFeeResponse fromDomain(NetworkFee fee) {
    return new NetworkFeeResponse(fee.gas(), fee.transactionType());
  }
}
