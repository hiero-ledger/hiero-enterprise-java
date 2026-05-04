package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

/** Implementation of ContractRepository that uses MirrorNodeClient to query contract data. */
public class ContractRepositoryImpl implements ContractRepository {

  private final MirrorNodeClient mirrorNodeClient;

  /**
   * Creates a new ContractRepositoryImpl with the given MirrorNodeClient.
   *
   * @param mirrorNodeClient the mirror node client to use for queries
   */
  public ContractRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<Contract> findAll() throws HieroException {
    return mirrorNodeClient.queryContracts();
  }

  @NonNull
  @Override
  public Optional<Contract> findById(@NonNull final ContractId contractId) throws HieroException {
    return mirrorNodeClient.queryContractById(contractId);
  }

  @NonNull
  @Override
  public Page<ContractResult> findResultsById(@NonNull final ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return mirrorNodeClient.queryContractResults(contractId);
  }

  @NonNull
  @Override
  public Page<ContractLog> findLogsById(@NonNull final ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return mirrorNodeClient.queryContractLogs(contractId);
  }
}
