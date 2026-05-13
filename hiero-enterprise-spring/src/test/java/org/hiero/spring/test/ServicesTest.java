package org.hiero.spring.test;

import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.HookClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.verification.ContractVerificationClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class ServicesTest {

  @Autowired private ProtocolLayerClient protocolLayerClient;

  @Autowired private ContractVerificationClient verificationClient;

  @Autowired private AccountClient accountClient;

  @Autowired private HookClient hookClient;

  @Autowired private FileClient fileServiceClient;

  @Autowired private SmartContractClient smartContractServiceClient;

  @Test
  void testServices() throws Exception {
    Assertions.assertNotNull(protocolLayerClient);
    Assertions.assertNotNull(verificationClient);
    Assertions.assertNotNull(accountClient);
    Assertions.assertNotNull(hookClient);
    Assertions.assertNotNull(fileServiceClient);
    Assertions.assertNotNull(smartContractServiceClient);
  }
}
