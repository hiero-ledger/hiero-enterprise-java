package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.HieroTransactionRecord;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;

  public HieroEndpoint(final AccountClient client, final BlockRepository blockRepository) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
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

  @GetMapping("/info/{id}")
  public AccountInfo getAccountInfo(@PathVariable("id") String id) {
    try {
      return client.getAccountInfo(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account info", e);
    }
  }

  @GetMapping("/records/{id}")
  public List<HieroTransactionRecord> getAccountRecords(@PathVariable("id") String id) {
    try {
      return client.getAccountRecords(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account records", e);
    }
  }

  @PostMapping("/update/{id}")
  public String updateMemo(@PathVariable("id") String id, @RequestParam("memo") String memo) {
    try {
      client.updateAccount(AccountId.fromString(id))
          .memo(memo)
          .execute();
      return "Account " + id + " updated with memo: " + memo;
    } catch (final Exception e) {
      throw new RuntimeException("Error updating account", e);
    }
  }
}
