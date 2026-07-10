package org.hiero.base.test;

import com.hedera.hashgraph.sdk.TokenId;
import org.hiero.base.data.Nft;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NftTest {

  private static final TokenId TOKEN_ID = TokenId.fromString("0.0.12345");
  private static final byte[] METADATA = "https://example.com/nft/1".getBytes();

  @Test
  void testNftWithValidOwner() {
    // given
    final var ownerId = com.hedera.hashgraph.sdk.AccountId.fromString("0.0.98765");

    // when
    final Nft nft = new Nft(TOKEN_ID, 1L, ownerId, METADATA);

    // then
    Assertions.assertEquals(TOKEN_ID, nft.tokenId());
    Assertions.assertEquals(1L, nft.serial());
    Assertions.assertEquals(ownerId, nft.owner());
    Assertions.assertArrayEquals(METADATA, nft.metadata());
  }

  @Test
  void testNftWithNullOwnerForBurnedNft() {
    // Burned NFTs have no owner — the mirror node returns account_id: null for them.
    // The Nft record must accept a null owner to accurately represent burned NFTs.

    // when
    final Nft nft = new Nft(TOKEN_ID, 2L, null, METADATA);

    // then
    Assertions.assertEquals(TOKEN_ID, nft.tokenId());
    Assertions.assertEquals(2L, nft.serial());
    Assertions.assertNull(nft.owner());
    Assertions.assertArrayEquals(METADATA, nft.metadata());
  }

  @Test
  void testNftThrowsForNullTokenId() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new Nft(null, 1L, null, METADATA));
  }

  @Test
  void testNftThrowsForNullMetadata() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new Nft(TOKEN_ID, 1L, null, null));
  }

  @Test
  void testNftThrowsForNegativeSerial() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new Nft(TOKEN_ID, -1L, null, METADATA));
  }
}
