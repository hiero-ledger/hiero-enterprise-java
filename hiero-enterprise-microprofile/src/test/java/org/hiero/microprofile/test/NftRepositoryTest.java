package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroContext;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.protocol.data.TransactionType;
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

  @Inject private NftRepository nftRepository;

  @Inject private AccountClient accountClient;

  @Inject private HieroContext hieroContext;

  @Test
  void findTransactionHistory() throws Exception {
    // given
    final String name = "Tokemon cards";
    final String symbol = "TOK";
    final byte[] metadata = "https://example.com/metadata1".getBytes(StandardCharsets.UTF_8);
    final TokenId tokenId = nftClient.createNftType(name, symbol);
    final long serial = nftClient.mintNft(tokenId, metadata);
    final AccountId adminAccountId = hieroContext.getOperatorAccount().accountId();
    final PrivateKey adminAccountPrivateKey = hieroContext.getOperatorAccount().privateKey();
    final Account account = accountClient.createAccount();
    final AccountId newOwner = account.accountId();
    final PrivateKey newOwnerPrivateKey = account.privateKey();
    nftClient.associateNft(tokenId, newOwner, newOwnerPrivateKey);
    nftClient.transferNft(tokenId, serial, adminAccountId, adminAccountPrivateKey, newOwner);
    // TODO: fix sleep
    Thread.sleep(10_000);

    // when
    final Page<NftTransactionTransfer> slice =
        nftRepository.findTransactionHistory(tokenId, serial);

    // then
    Assertions.assertNotNull(slice);
    Assertions.assertTrue(
        slice.getData().stream()
            .anyMatch(
                transfer ->
                    TransactionType.CRYPTO_TRANSFER.equals(transfer.type())
                        && adminAccountId.equals(transfer.senderAccountId())
                        && newOwner.equals(transfer.receiverAccountId())));
  }
}
