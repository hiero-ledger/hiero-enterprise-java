package org.hiero.spring.sample;

import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractLogRepository;
import org.hiero.base.data.ContractLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final ContractLogRepository contractLogRepository;

  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final ContractLogRepository contractLogRepository) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.blockRepository = Objects.requireNonNull(blockRepository, "blockRepository must not be null");
    this.contractLogRepository = Objects.requireNonNull(contractLogRepository, "contractLogRepository must not be null");
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

  @GetMapping("/contracts/{id}/logs")
  public Page<ContractLog> getContractLogs(@PathVariable String id) {
    try {
      return contractLogRepository.findByContractId(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract logs", e);
    }
  }
}
