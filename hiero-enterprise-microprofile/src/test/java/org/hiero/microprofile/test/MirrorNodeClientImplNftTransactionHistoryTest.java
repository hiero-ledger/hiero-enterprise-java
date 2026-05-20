package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.time.Instant;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.data.Page;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MirrorNodeClientImplNftTransactionHistoryTest {

  @Test
  void queryNftTransactionHistoryCallsMirrorNodeEndpoint() throws Exception {
    final String expectedPath = "/api/v1/tokens/0.0.222/nfts/1/transactions";
    final Client httpClient = mock(Client.class);
    final WebTarget rootTarget = mock(WebTarget.class);
    final WebTarget pathTarget = mock(WebTarget.class);
    final Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);
    final Response response = mock(Response.class);
    final JsonObject responseBody = parse(nftTransactionHistoryJson());

    try (MockedStatic<ClientBuilder> clientBuilder = mockStatic(ClientBuilder.class)) {
      clientBuilder.when(ClientBuilder::newClient).thenReturn(httpClient);
      when(httpClient.target("http://mirror-node")).thenReturn(rootTarget);
      when(rootTarget.path(expectedPath)).thenReturn(pathTarget);
      when(pathTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
      when(invocationBuilder.get()).thenReturn(response);
      when(response.readEntity(JsonObject.class)).thenReturn(responseBody);

      final MirrorNodeClientImpl client =
          new MirrorNodeClientImpl(
              new MirrorNodeRestClientImpl("http://mirror-node"),
              new MirrorNodeJsonConverterImpl());

      final Page<NftTransactionTransfer> page =
          client.queryNftTransactionHistory(TokenId.fromString("0.0.222"), 1);

      verify(rootTarget).path(expectedPath);
      assertEquals(1, page.getSize());
      final NftTransactionTransfer transfer = page.getData().get(0);
      assertEquals(
          Instant.ofEpochSecond(1_618_591_023L, 997_420_021), transfer.consensusTimestamp());
      assertEquals(AccountId.fromString("0.0.11"), transfer.receiverAccountId());
      assertEquals(AccountId.fromString("0.0.10"), transfer.senderAccountId());
      assertEquals("0.0.19789-1618591023-997420021", transfer.transactionId());
      assertEquals(TransactionType.CRYPTO_TRANSFER, transfer.type());
    }
  }

  private static JsonObject parse(String body) {
    return Json.createReader(new StringReader(body)).readObject();
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
