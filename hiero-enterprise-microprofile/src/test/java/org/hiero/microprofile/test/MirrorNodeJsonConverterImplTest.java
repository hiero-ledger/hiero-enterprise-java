package org.hiero.microprofile.test;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.util.Optional;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MirrorNodeJsonConverterImplTest {

  @Test
  void parsesTransactionWithNullsNumbersAndEmptyArrays() {
    final String transactionJson =
        """
        {
          "bytes": null,
          "charged_tx_fee": 720725773,
          "consensus_timestamp": "1778601566.435208012",
          "entity_id": "0.0.1186",
          "max_fee": "10000000000",
          "memo_base64": "",
          "name": "TOKENCREATION",
          "nft_transfers": [],
          "node": "0.0.3",
          "nonce": 0,
          "parent_consensus_timestamp": null,
          "result": "SUCCESS",
          "scheduled": false,
          "staking_reward_transfers": [],
          "token_transfers": [],
          "transaction_hash": "sqPRfWjtyEQd/u18Eo1R9ewwie6iQzPRHZkisbW/HuH3N3T73/H/kyoOw3jc4Dyf",
          "transaction_id": "0.0.1003-1778601556-425000284",
          "transfers": [
            {"account": "0.0.3", "amount": 18511558, "is_approval": false},
            {"account": "0.0.98", "amount": 561771373, "is_approval": false},
            {"account": "0.0.800", "amount": 70221421, "is_approval": false},
            {"account": "0.0.801", "amount": 70221421, "is_approval": false},
            {"account": "0.0.1003", "amount": -720725773, "is_approval": false}
          ],
          "valid_duration_seconds": "120",
          "valid_start_timestamp": "1778601556.425000284"
        }
        """;
    final JsonObject jsonObject = Json.createReader(new StringReader(transactionJson)).readObject();

    final Optional<TransactionInfo> result =
        new MirrorNodeJsonConverterImpl().toTransactionInfo(jsonObject);

    Assertions.assertTrue(result.isPresent());
    Assertions.assertNull(result.get().bytes());
    Assertions.assertEquals(720725773L, result.get().chargedTxFee());
    Assertions.assertEquals(1778601566L, result.get().consensusTimestamp().getEpochSecond());
    Assertions.assertEquals(435208012, result.get().consensusTimestamp().getNano());
    Assertions.assertEquals(TransactionType.TOKEN_CREATE, result.get().name());
    Assertions.assertTrue(result.get().nftTransfers().isEmpty());
    Assertions.assertTrue(result.get().tokenTransfers().isEmpty());
    Assertions.assertEquals(5, result.get().transfers().size());
    Assertions.assertEquals(18511558L, result.get().transfers().getFirst().amount());
  }
}
