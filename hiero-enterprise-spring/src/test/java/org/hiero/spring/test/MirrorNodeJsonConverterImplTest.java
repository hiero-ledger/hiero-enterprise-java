package org.hiero.spring.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.hiero.base.data.Nft;
import org.hiero.spring.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MirrorNodeJsonConverterImpl} NFT parsing.
 *
 * <p>These tests do not require a Hedera network connection — they test JSON parsing
 * logic directly using constructed {@link com.fasterxml.jackson.databind.JsonNode} objects.
 */
public class MirrorNodeJsonConverterImplTest {

  private MirrorNodeJsonConverterImpl converter;
  private ObjectMapper objectMapper;

  private static final String TOKEN_ID = "0.0.55001";
  private static final String ACCOUNT_ID = "0.0.12345";
  private static final long SERIAL = 1L;
  private static final byte[] METADATA = "https://example.com/nft/1".getBytes();
  private static final String METADATA_BASE64 =
      Base64.getEncoder().encodeToString(METADATA);

  @BeforeEach
  void setUp() {
    converter = new MirrorNodeJsonConverterImpl();
    objectMapper = new ObjectMapper();
  }

  // toNft tests

  @Test
  void toNft_withValidAccountId_returnsNftWithOwner() throws Exception {
    // given — a live NFT with a non-null account_id
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("token_id", TOKEN_ID);
    node.put("account_id", ACCOUNT_ID);
    node.put("serial_number", SERIAL);
    node.put("metadata", METADATA_BASE64);

    // when
    final Optional<Nft> result = converter.toNft(node);

    // then
    Assertions.assertTrue(result.isPresent());
    final Nft nft = result.get();
    Assertions.assertEquals(TokenId.fromString(TOKEN_ID), nft.tokenId());
    Assertions.assertEquals(SERIAL, nft.serial());
    Assertions.assertEquals(AccountId.fromString(ACCOUNT_ID), nft.owner());
  }

  @Test
  void toNft_withNullAccountId_returnsNftWithNullOwner() throws Exception {
    // given — a burned NFT: the mirror node returns account_id: null
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("token_id", TOKEN_ID);
    node.putNull("account_id");         // <-- burned NFT
    node.put("serial_number", SERIAL);
    node.put("metadata", METADATA_BASE64);

    // when
    final Optional<Nft> result = converter.toNft(node);

    // then — must not throw; owner must be null
    Assertions.assertTrue(result.isPresent());
    final Nft nft = result.get();
    Assertions.assertEquals(TokenId.fromString(TOKEN_ID), nft.tokenId());
    Assertions.assertEquals(SERIAL, nft.serial());
    Assertions.assertNull(nft.owner(), "owner must be null for a burned NFT");
  }

  @Test
  void toNft_withEmptyNode_returnsEmpty() {
    // given
    final ObjectNode emptyNode = objectMapper.createObjectNode();

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
  void toNfts_withMixedBurnedAndLiveNfts_returnsBothCorrectly() throws Exception {
    // given — a list that contains one live NFT and one burned NFT
    final ObjectNode liveNft = objectMapper.createObjectNode();
    liveNft.put("token_id", TOKEN_ID);
    liveNft.put("account_id", ACCOUNT_ID);
    liveNft.put("serial_number", 1L);
    liveNft.put("metadata", METADATA_BASE64);

    final ObjectNode burnedNft = objectMapper.createObjectNode();
    burnedNft.put("token_id", TOKEN_ID);
    burnedNft.putNull("account_id");    // <-- burned NFT
    burnedNft.put("serial_number", 2L);
    burnedNft.put("metadata", METADATA_BASE64);

    final ObjectNode root = objectMapper.createObjectNode();
    root.putArray("nfts").add(liveNft).add(burnedNft);

    // when
    final List<Nft> result = converter.toNfts(root);

    // then — both NFTs must be returned; burned one has null owner
    Assertions.assertEquals(2, result.size());

    final Nft live = result.stream().filter(n -> n.serial() == 1L).findFirst().orElseThrow();
    Assertions.assertEquals(AccountId.fromString(ACCOUNT_ID), live.owner());

    final Nft burned = result.stream().filter(n -> n.serial() == 2L).findFirst().orElseThrow();
    Assertions.assertNull(burned.owner(), "burned NFT owner must be null");
  }

  @Test
  void toNfts_withEmptyNftsArray_returnsEmptyList() {
    // given
    final ObjectNode root = objectMapper.createObjectNode();
    root.putArray("nfts");

    // when
    final List<Nft> result = converter.toNfts(root);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void toNfts_withNoNftsKey_returnsEmptyList() {
    // given
    final ObjectNode root = objectMapper.createObjectNode();

    // when
    final List<Nft> result = converter.toNfts(root);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
