package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/**
 * Repository for querying smart contract logs (events) from a Hiero network. This provides an
 * easy-to-use API for observing events emitted by deployed smart contracts via the Hiero Mirror
 * Node.
 */
public interface ContractLogRepository {

  @NonNull Page<ContractLog> findByContractId(@NonNull ContractId contractId) throws HieroException;

  @NonNull
  default Page<ContractLog> findByContractId(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findByContractId(ContractId.fromString(contractId));
  }
}
