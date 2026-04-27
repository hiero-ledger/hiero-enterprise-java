package org.hiero.base.test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.List;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.data.SinglePage;
import org.hiero.base.implementation.ContractRepositoryImpl;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.junit.jupiter.api.Test;

class ContractRepositoryImplTest {

  @Test
  void methodsRejectNullContractId() {
    final ContractRepository repository = new ContractRepositoryImpl(mock(MirrorNodeClient.class));

    assertThrows(NullPointerException.class, () -> repository.findResultsById((ContractId) null));
    assertThrows(NullPointerException.class, () -> repository.findLogsById((ContractId) null));
    assertThrows(NullPointerException.class, () -> repository.findResultsById((String) null));
    assertThrows(NullPointerException.class, () -> repository.findLogsById((String) null));
  }

  @Test
  void findResultsByIdDelegatesToMirrorNodeClient() throws Exception {
    final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
    final ContractRepository repository = new ContractRepositoryImpl(mirrorNodeClient);
    final ContractId contractId = ContractId.fromString("0.0.5005");
    final Page<ContractResult> page = new SinglePage<>(List.of());
    when(mirrorNodeClient.queryContractResults(contractId)).thenReturn(page);

    final Page<ContractResult> result = repository.findResultsById(contractId);

    assertSame(page, result);
    verify(mirrorNodeClient).queryContractResults(contractId);
  }

  @Test
  void findLogsByIdDelegatesToMirrorNodeClient() throws Exception {
    final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
    final ContractRepository repository = new ContractRepositoryImpl(mirrorNodeClient);
    final ContractId contractId = ContractId.fromString("0.0.5005");
    final Page<ContractLog> page = new SinglePage<>(List.of());
    when(mirrorNodeClient.queryContractLogs(contractId)).thenReturn(page);

    final Page<ContractLog> result = repository.findLogsById(contractId);

    assertSame(page, result);
    verify(mirrorNodeClient).queryContractLogs(contractId);
  }
}
