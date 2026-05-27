package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.ContractLogRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

/** Implementation of ContractLogRepository that uses MirrorNodeClient to query contract logs. */
public class ContractLogRepositoryImpl implements ContractLogRepository {

  private final MirrorNodeClient mirrorNodeClient;

  /**
   * Creates a new ContractLogRepositoryImpl with the given MirrorNodeClient.
   *
   * @param mirrorNodeClient the mirror node client to use for queries
   */
  public ContractLogRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<ContractLog> findByContractId(@NonNull final ContractId contractId)
      throws HieroException {
    return mirrorNodeClient.queryContractLogs(contractId);
  }
}
