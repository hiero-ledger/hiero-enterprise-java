package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.HieroTransactionRecord;
import org.springframework.web.bind.annotation.*;

@RestController
public class HieroEndpoint {

  private final AccountClient client;

  public HieroEndpoint(final AccountClient client) {
    this.client = Objects.requireNonNull(client, "client must not be null");
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

  @GetMapping("/info/{id}")
  public AccountInfo getAccountInfo(@PathVariable String id) {
    try {
      return client.getAccountInfo(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account info", e);
    }
  }

  @GetMapping("/records/{id}")
  public List<HieroTransactionRecord> getAccountRecords(@PathVariable String id) {
    try {
      return client.getAccountRecords(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account records", e);
    }
  }

  @PostMapping("/update/{id}")
  public String updateMemo(@PathVariable String id, @RequestParam String memo) {
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
