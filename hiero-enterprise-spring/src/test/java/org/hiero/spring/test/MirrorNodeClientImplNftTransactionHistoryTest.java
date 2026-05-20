package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.net.URI;
import java.time.Instant;
import java.util.function.Function;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

class MirrorNodeClientImplNftTransactionHistoryTest {

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  void queryNftTransactionHistoryCallsMirrorNodeEndpoint() throws Exception {
    final String expectedPath = "/api/v1/tokens/0.0.222/nfts/1/transactions";
    final RestClient.Builder configuredBuilder = mock(RestClient.Builder.class);
    final RestClient configuredClient = mock(RestClient.class);
    final RestClient.Builder pageBuilder = mock(RestClient.Builder.class);
    final RestClient pageClient = mock(RestClient.class);
    final RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    final RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
    final RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    when(configuredBuilder.build()).thenReturn(configuredClient);
    when(configuredClient.mutate()).thenReturn(pageBuilder);
    when(pageBuilder.clone()).thenReturn(pageBuilder);
    when(pageBuilder.build()).thenReturn(pageClient);
    when(pageClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
    when(headersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toEntity(String.class))
        .thenReturn(ResponseEntity.ok(nftTransactionHistoryJson()));

    final MirrorNodeClientImpl client = new MirrorNodeClientImpl(configuredBuilder);
    final Page<NftTransactionTransfer> page =
        client.queryNftTransactionHistory(TokenId.fromString("0.0.222"), 1);

    final ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor =
        ArgumentCaptor.forClass(Function.class);
    verify(uriSpec).uri(uriCaptor.capture());
    final URI requestUri =
        uriCaptor.getValue().apply(new DefaultUriBuilderFactory("http://mirror-node").builder());
    assertEquals("http://mirror-node" + expectedPath, requestUri.toString());
    assertEquals(1, page.getSize());
    final NftTransactionTransfer transfer = page.getData().get(0);
    assertEquals(Instant.ofEpochSecond(1_618_591_023L, 997_420_021), transfer.consensusTimestamp());
    assertEquals(AccountId.fromString("0.0.11"), transfer.receiverAccountId());
    assertEquals(AccountId.fromString("0.0.10"), transfer.senderAccountId());
    assertEquals("0.0.19789-1618591023-997420021", transfer.transactionId());
    assertEquals(TransactionType.CRYPTO_TRANSFER, transfer.type());
  }

  private static String nftTransactionHistoryJson() {
    return """
        {
          "transactions": [
            {
              "consensus_timestamp": "1618591023.997420021",
              "is_approval": false,
              "nonce": 0,
              "receiver_account_id": "0.0.11",
              "sender_account_id": "0.0.10",
              "transaction_id": "0.0.19789-1618591023-997420021",
              "type": "CRYPTOTRANSFER"
            }
          ],
          "links": {"next": null}
        }
        """;
  }
}
