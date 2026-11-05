package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BalanceRepository;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class BalanceRepositoryTest {
  @Autowired private AccountClient accountClient;
  @Autowired private BalanceRepository balanceRepository;
  @Autowired private HieroTestUtils hieroTestUtils;

  @Test
  void testFindAll() throws HieroException {
    final Page<AccountBalance> result = balanceRepository.findAll();
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.getData().isEmpty());
  }

  @Test
  void testFindByAccountId() throws HieroException {
    final Account account = accountClient.createAccount();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<AccountBalance> result = balanceRepository.findByAccount(account.accountId());
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void testFindByNonExistingAccountId() throws HieroException {
    final AccountId accountId = AccountId.fromString("0.0.9999999999");
    final Optional<AccountBalance> result = balanceRepository.findByAccount(accountId);
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindByAccountIdAsNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> balanceRepository.findByAccount((AccountId) null));
  }

  @Test
  void testGetSnapshot() throws HieroException {
    final Optional<BalanceSnapshot> result = balanceRepository.getSnapshot();
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }
}
