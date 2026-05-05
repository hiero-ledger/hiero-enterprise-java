package org.hiero.spring.sample.dto.nft;

/**
 * Request DTO for transferring an NFT instance.
 */
public record NftTransferRequest(
    String fromAccountId,
    String fromPrivateKey,
    String toAccountId,
    long serialNumber
) {
}
