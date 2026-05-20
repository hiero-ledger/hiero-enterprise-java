package org.hiero.microprofile;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.TokenId;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.NftRepository;
import org.junit.jupiter.api.Test;

class NftRepositoryNftTransactionHistoryTest {

  @Test
  void nftRepositoryFindTransactionHistoryUsesConfiguredMirrorNodeClient() throws Exception {
    final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
    final TokenId tokenId = TokenId.fromString("0.0.222");
    final Page<NftTransactionTransfer> page = mock(Page.class);
    when(mirrorNodeClient.queryNftTransactionHistory(tokenId, 1)).thenReturn(page);

    final NftRepository repository = new ClientProvider().createNftRepository(mirrorNodeClient);
    final Page<NftTransactionTransfer> result = repository.findTransactionHistory(tokenId, 1);

    assertSame(page, result);
    verify(mirrorNodeClient).queryNftTransactionHistory(tokenId, 1);
  }
}
