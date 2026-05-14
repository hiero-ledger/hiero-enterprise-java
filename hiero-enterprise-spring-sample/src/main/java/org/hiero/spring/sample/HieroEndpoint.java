package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final ContractRepository contractRepository;

  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final ContractRepository contractRepository) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
    this.contractRepository =
        Objects.requireNonNull(contractRepository, "contractRepository must not be null");
  }

  @GetMapping("/")
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account " + account.accountId() + " created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GetMapping("/blocks")
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GetMapping("/contracts/{contractId}/logs")
  public Map<String, Object> getContractLogs(@PathVariable("contractId") final String contractId) {
    try {
      final Page<ContractLog> page =
          contractRepository.findLogsById(ContractId.fromString(contractId));
      return Map.of(
          "contractId", contractId,
          "pageIndex", page.getPageIndex(),
          "size", page.getSize(),
          "hasNext", page.hasNext(),
          "data", page.getData());
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract logs for " + contractId, e);
    }
  }

  @GetMapping("/contracts/{contractId}/results")
  public Map<String, Object> getContractResults(
      @PathVariable("contractId") final String contractId) {
    try {
      final Page<ContractResult> page =
          contractRepository.findResultsById(ContractId.fromString(contractId));
      return Map.of(
          "contractId", contractId,
          "pageIndex", page.getPageIndex(),
          "size", page.getSize(),
          "hasNext", page.hasNext(),
          "data", page.getData());
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract results for " + contractId, e);
    }
  }
}
