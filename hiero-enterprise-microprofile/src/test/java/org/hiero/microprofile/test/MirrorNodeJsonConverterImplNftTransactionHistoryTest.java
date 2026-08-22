package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hedera.hashgraph.sdk.AccountId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.time.Instant;
import java.util.List;
import org.hiero.base.data.NftTransactionTransfer;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Test;

class MirrorNodeJsonConverterImplNftTransactionHistoryTest {

  private final MirrorNodeJsonConverterImpl converter = new MirrorNodeJsonConverterImpl();

  @Test
  void toNftTransactionTransfersReturnsEmptyWhenKeyMissing() {
    final JsonObject jsonObject = parse("{\"links\":{\"next\":null}}");

    final List<NftTransactionTransfer> result = converter.toNftTransactionTransfers(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toNftTransactionTransfersReturnsEmptyForEmptyArray() {
    final JsonObject jsonObject = parse("{\"transactions\":[],\"links\":{\"next\":null}}");

    final List<NftTransactionTransfer> result = converter.toNftTransactionTransfers(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toNftTransactionTransfersParsesSingleEntry() {
    final JsonObject jsonObject = parse(nftTransactionHistoryJson());

    final List<NftTransactionTransfer> result = converter.toNftTransactionTransfers(jsonObject);

    assertEquals(1, result.size());
    final NftTransactionTransfer transfer = result.get(0);
    assertEquals(Instant.ofEpochSecond(1_618_591_023L, 997_420_021), transfer.consensusTimestamp());
    assertEquals(false, transfer.isApproval());
    assertEquals(0, transfer.nonce());
    assertEquals(AccountId.fromString("0.0.11"), transfer.receiverAccountId());
    assertEquals(AccountId.fromString("0.0.10"), transfer.senderAccountId());
    assertEquals("0.0.19789-1618591023-997420021", transfer.transactionId());
    assertEquals(TransactionType.CRYPTO_TRANSFER, transfer.type());
  }

  private static JsonObject parse(final String json) {
    return Json.createReader(new StringReader(json)).readObject();
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
