package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroContext;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Nft;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class NftRepositoryTest {

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private NftClient nftClient;

  @Inject private AccountClient accountClient;

  @Inject private NftRepository nftRepository;

  @Inject private HieroContext hieroContext;

  @Test
  void findMintedNftsByTypeOwnerAndOwnerAndType() throws Exception {
    final String name = "Tokemon cards";
    final String symbol = "TOK";
    final byte[] metadata1 = "https://example.com/metadata1".getBytes(StandardCharsets.UTF_8);
    final byte[] metadata2 = "https://example.com/metadata2".getBytes(StandardCharsets.UTF_8);
    final TokenId tokenId = nftClient.createNftType(name, symbol);
    final List<Long> serials = nftClient.mintNfts(tokenId, metadata1, metadata2);
    final Account account = accountClient.createAccount();
    final AccountId newOwner = account.accountId();

    nftClient.associateNft(tokenId, account);
    nftClient.transferNft(tokenId, serials.get(0), hieroContext.getOperatorAccount(), newOwner);
    nftClient.transferNft(tokenId, serials.get(1), hieroContext.getOperatorAccount(), newOwner);
    Thread.sleep(10_000);

    final Page<Nft> nftsByType = nftRepository.findByType(tokenId);
    final Page<Nft> nftsByOwner = nftRepository.findByOwner(newOwner);
    final Page<Nft> nftsByOwnerAndType = nftRepository.findByOwnerAndType(newOwner, tokenId);

    assertContainsNft(nftsByType.getData(), tokenId, serials.get(0), newOwner, metadata1);
    assertContainsNft(nftsByType.getData(), tokenId, serials.get(1), newOwner, metadata2);
    assertContainsNft(nftsByOwner.getData(), tokenId, serials.get(0), newOwner, metadata1);
    assertContainsNft(nftsByOwner.getData(), tokenId, serials.get(1), newOwner, metadata2);
    assertContainsNft(nftsByOwnerAndType.getData(), tokenId, serials.get(0), newOwner, metadata1);
    assertContainsNft(nftsByOwnerAndType.getData(), tokenId, serials.get(1), newOwner, metadata2);
  }

  private static void assertContainsNft(
      final List<Nft> nfts,
      final TokenId tokenId,
      final long serial,
      final AccountId owner,
      final byte[] metadata) {
    Assertions.assertTrue(
        nfts.stream()
            .anyMatch(
                nft ->
                    tokenId.equals(nft.tokenId())
                        && serial == nft.serial()
                        && owner.equals(nft.owner())
                        && Arrays.equals(metadata, nft.metadata())),
        "Expected NFT " + tokenId + "/" + serial + " for owner " + owner);
  }
}
