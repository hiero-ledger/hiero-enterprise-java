package org.hiero.spring.sample.dto.nft;

/**
 * Request DTO for creating a new NFT type.
 */
public record NftCreateRequest(String name, String symbol) {
}
