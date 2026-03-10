package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class AccountRepositoryTest {

  @Autowired private AccountRepository accountRepository;

  @Autowired private HieroTestUtils hieroTestUtils;

  @Autowired private AccountClient accountClient;

  @Test
  void findById() throws Exception {
    // given
    final Account account = accountClient.createAccount();
    final AccountId newOwner = account.accountId();
    hieroTestUtils.waitForMirrorNodeRecords();

    // when
    final Optional<AccountInfo> result = accountRepository.findById(newOwner);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }
}
