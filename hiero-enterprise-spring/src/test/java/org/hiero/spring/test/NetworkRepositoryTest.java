package org.hiero.spring.test;

import java.util.List;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.mirrornode.NetworkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class NetworkRepositoryTest {
  @Autowired private NetworkRepository networkRepository;

  @Test
  void findExchangeRates() throws HieroException {
    Optional<ExchangeRates> result = networkRepository.exchangeRates();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void findNetworkFees() throws HieroException {
    List<NetworkFee> result = networkRepository.fees();

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  @Disabled
  void findNetworkStake() throws HieroException {
    Optional<NetworkStake> result = networkRepository.stake();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  @Disabled
  void findNetworkSupplies() throws HieroException {
    Optional<NetworkSupplies> result = networkRepository.supplies();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }
}
