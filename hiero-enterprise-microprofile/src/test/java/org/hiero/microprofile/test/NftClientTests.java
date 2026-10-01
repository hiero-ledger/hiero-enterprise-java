package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.TokenId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class NftClientTests {

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private NftClient nftClient;

  @Inject private AccountClient accountClient;

  @Test
  void rejectNft() throws Exception {
    // given
    final Account treasuryAccount = accountClient.createAccount(1);
    final TokenId tokenId = nftClient.createNftType("Test NFT", "TST", treasuryAccount);
    final Account userAccount = accountClient.createAccount(1);
    final byte[] metadata = "https://example.com/metadata".getBytes(StandardCharsets.UTF_8);
    nftClient.associateNft(tokenId, userAccount);
    final long serial = nftClient.mintNft(tokenId, treasuryAccount.privateKey(), metadata);
    nftClient.transferNft(tokenId, serial, treasuryAccount, userAccount.accountId());

    // then
    Assertions.assertDoesNotThrow(() -> nftClient.rejectNft(tokenId, serial, userAccount));
    Assertions.assertDoesNotThrow(
        () -> nftClient.transferNft(tokenId, serial, treasuryAccount, userAccount.accountId()));
  }

  @Test
  void rejectAirdroppedNfts() throws Exception {
    // given
    final Account treasuryAccount = accountClient.createAccount(1);
    final TokenId tokenId = nftClient.createNftType("Test NFT", "TST", treasuryAccount);
    final Account userAccount = accountClient.createAccount(1);
    final byte[] metadata1 = "https://example.com/metadata1".getBytes(StandardCharsets.UTF_8);
    final byte[] metadata2 = "https://example.com/metadata2".getBytes(StandardCharsets.UTF_8);
    nftClient.associateNft(tokenId, userAccount);
    final List<Long> serials =
        nftClient.mintNfts(tokenId, treasuryAccount.privateKey(), metadata1, metadata2);
    nftClient.airdropNfts(tokenId, serials, treasuryAccount, userAccount.accountId());

    // then
    Assertions.assertDoesNotThrow(() -> nftClient.rejectNfts(tokenId, serials, userAccount));
  }

  @Test
  void rejectNftThrowsExceptionIfNotOwner() throws Exception {
    // given
    final Account treasuryAccount = accountClient.createAccount(1);
    final TokenId tokenId = nftClient.createNftType("Test NFT", "TST", treasuryAccount);
    final Account userAccount = accountClient.createAccount(1);
    final byte[] metadata = "https://example.com/metadata".getBytes(StandardCharsets.UTF_8);
    nftClient.associateNft(tokenId, userAccount);
    final long serial = nftClient.mintNft(tokenId, treasuryAccount.privateKey(), metadata);

    // then
    Assertions.assertThrows(
        HieroException.class, () -> nftClient.rejectNft(tokenId, serial, userAccount));
    Assertions.assertThrows(
        HieroException.class, () -> nftClient.rejectNfts(tokenId, List.of(serial), userAccount));
  }

  @Test
  void rejectNftNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> nftClient.rejectNft(null, 1L, (Account) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> nftClient.rejectNft(null, 1L, null, null));
    Assertions.assertThrows(
        NullPointerException.class, () -> nftClient.rejectNfts(null, null, (Account) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> nftClient.rejectNfts(null, null, null, null));
  }
}
