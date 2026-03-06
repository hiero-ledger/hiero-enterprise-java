package org.hiero.base.protocol;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import org.hiero.base.protocol.data.TransactionType;

public interface TransactionListener {

  void transactionSubmitted(TransactionType transactionType, TransactionId transactionId);

  void transactionHandled(
      TransactionType transactionType, TransactionId transactionId, Status transactionStatus);
}
