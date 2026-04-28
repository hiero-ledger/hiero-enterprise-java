package org.hiero.base.test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.TokenId;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.implementation.NftRepositoryImpl;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.junit.jupiter.api.Test;

class NftRepositoryImplTest {

  @Test
  void findTransactionHistoryDelegatesToMirrorNodeClient() throws Exception {
    final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
    final NftRepositoryImpl repository = new NftRepositoryImpl(mirrorNodeClient);
    final TokenId tokenId = TokenId.fromString("0.0.222");
    final Page<NftTransactionTransfer> page = mock(Page.class);
    when(mirrorNodeClient.queryNftTransactionHistory(tokenId, 1)).thenReturn(page);

    final Page<NftTransactionTransfer> result = repository.findTransactionHistory(tokenId, 1);

    assertSame(page, result);
    verify(mirrorNodeClient).queryNftTransactionHistory(tokenId, 1);
  }
}
