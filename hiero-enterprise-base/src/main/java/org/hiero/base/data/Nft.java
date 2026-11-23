package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a non-fungible token (NFT).
 *
 * <p>The {@code owner} field may be {@code null} when the NFT has been burned. The Hedera mirror
 * node returns {@code "account_id": null} for burned NFTs — they still exist as on-chain records
 * but no longer have an owner.
 *
 * @param tokenId the ID of the token type
 * @param serial the serial number of the NFT
 * @param owner the account that owns the NFT, or {@code null} if the NFT has been burned
 * @param metadata the metadata of the NFT
 */
public record Nft(
    @NonNull TokenId tokenId, long serial, @Nullable AccountId owner, @NonNull byte[] metadata) {

  public Nft {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(metadata, "metadata must not be null");
    if (serial < 0) {
      throw new IllegalArgumentException("serial must be greater than or equal to 0");
    }
  }
}
