package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.hiero.base.data.Nft;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MirrorNodeJsonConverterImpl} NFT parsing.
 *
 * <p>These tests do not require a Hedera network connection — they test JSON parsing
 * logic directly using constructed {@link jakarta.json.JsonObject} objects.
 */
public class MirrorNodeJsonConverterImplTest {

  private MirrorNodeJsonConverterImpl converter;

  private static final String TOKEN_ID = "0.0.55001";
  private static final String ACCOUNT_ID = "0.0.12345";
  private static final long SERIAL = 1L;
  private static final byte[] METADATA = "https://example.com/nft/1".getBytes();
  private static final String METADATA_BASE64 =
      Base64.getEncoder().encodeToString(METADATA);
  // The microprofile converter stores metadata as the raw Base64 string bytes (not decoded),
  // consistent with how it handles metadata throughout the module.
  private static final byte[] EXPECTED_METADATA_BYTES = METADATA_BASE64.getBytes();

  @BeforeEach
  void setUp() {
    converter = new MirrorNodeJsonConverterImpl();
  }

  // toNft tests

  @Test
  void toNft_withValidAccountId_returnsNftWithOwner() {
    // given — a live NFT with a non-null account_id
    final JsonObject node = Json.createObjectBuilder()
        .add("token_id", TOKEN_ID)
        .add("account_id", ACCOUNT_ID)
        .add("serial_number", SERIAL)
        .add("metadata", METADATA_BASE64)
        .build();

    // when
    final Optional<Nft> result = converter.toNft(node);

    // then
    Assertions.assertTrue(result.isPresent());
    final Nft nft = result.get();
    Assertions.assertEquals(TokenId.fromString(TOKEN_ID), nft.tokenId());
    Assertions.assertEquals(SERIAL, nft.serial());
    Assertions.assertEquals(AccountId.fromString(ACCOUNT_ID), nft.owner());
    Assertions.assertArrayEquals(EXPECTED_METADATA_BYTES, nft.metadata());
  }

  @Test
  void toNft_withNullAccountId_returnsNftWithNullOwner() {
    // given — a burned NFT: the mirror node returns account_id: null
    final JsonObject node = Json.createObjectBuilder()
        .add("token_id", TOKEN_ID)
        .addNull("account_id")          // <-- burned NFT
        .add("serial_number", SERIAL)
        .add("metadata", METADATA_BASE64)
        .build();

    // when
    final Optional<Nft> result = converter.toNft(node);

    // then — must not throw; owner must be null
    Assertions.assertTrue(result.isPresent());
    final Nft nft = result.get();
    Assertions.assertEquals(TokenId.fromString(TOKEN_ID), nft.tokenId());
    Assertions.assertEquals(SERIAL, nft.serial());
    Assertions.assertNull(nft.owner(), "owner must be null for a burned NFT");
    Assertions.assertArrayEquals(EXPECTED_METADATA_BYTES, nft.metadata());
  }

  @Test
  void toNft_withEmptyNode_returnsEmpty() {
    // given
    final JsonObject emptyNode = Json.createObjectBuilder().build();

    // when
    final Optional<Nft> result = converter.toNft(emptyNode);

    // then
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  void toNft_withNullNode_throwsNullPointerException() {
    Assertions.assertThrows(NullPointerException.class, () -> converter.toNft(null));
  }

  // toNfts tests

  @Test
  void toNfts_withMixedBurnedAndLiveNfts_returnsBothCorrectly() {
    // given — a list that contains one live NFT and one burned NFT
    final JsonObject liveNft = Json.createObjectBuilder()
        .add("token_id", TOKEN_ID)
        .add("account_id", ACCOUNT_ID)
        .add("serial_number", 1L)
        .add("metadata", METADATA_BASE64)
        .build();

    final JsonObject burnedNft = Json.createObjectBuilder()
        .add("token_id", TOKEN_ID)
        .addNull("account_id")          // <-- burned NFT
        .add("serial_number", 2L)
        .add("metadata", METADATA_BASE64)
        .build();

    final JsonArray nftsArray = Json.createArrayBuilder()
        .add(liveNft)
        .add(burnedNft)
        .build();

    final JsonObject root = Json.createObjectBuilder()
        .add("nfts", nftsArray)
        .build();

    // when
    final List<Nft> result = converter.toNfts(root);

    // then — both NFTs must be returned; burned one has null owner
    Assertions.assertEquals(2, result.size());

    final Nft live = result.stream().filter(n -> n.serial() == 1L).findFirst().orElseThrow();
    Assertions.assertEquals(AccountId.fromString(ACCOUNT_ID), live.owner());
    Assertions.assertArrayEquals(EXPECTED_METADATA_BYTES, live.metadata());

    final Nft burned = result.stream().filter(n -> n.serial() == 2L).findFirst().orElseThrow();
    Assertions.assertNull(burned.owner(), "burned NFT owner must be null");
    Assertions.assertArrayEquals(EXPECTED_METADATA_BYTES, burned.metadata());
  }

  @Test
  void toNfts_withEmptyNftsArray_returnsEmptyList() {
    // given
    final JsonObject root = Json.createObjectBuilder()
        .add("nfts", Json.createArrayBuilder().build())
        .build();

    // when
    final List<Nft> result = converter.toNfts(root);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void toNfts_withNoNftsKey_returnsEmptyList() {
    // given
    final JsonObject root = Json.createObjectBuilder().build();

    // when
    final List<Nft> result = converter.toNfts(root);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
