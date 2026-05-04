package org.hiero.spring.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HookClient;
import org.hiero.base.NftClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
import org.hiero.base.HieroContext;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.SinglePage;
import org.hiero.base.events.ContractEvent;
import org.hiero.base.events.ContractEventListener;
import org.hiero.base.mirrornode.EventRepository;
import org.hiero.spring.events.HieroEventSubscriber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = HieroTestConfig.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.hiero.accountId=0.0.123",
    "spring.hiero.privateKey=302e020100300506032b657004220420c236508c429395a8180b1230f436d389adc5afaa9145456783b57b2045c6cc37",
    "spring.hiero.network.name=testnet",
    "spring.hiero.network.mirrorNode=https://testnet.mirrornode.hedera.com",
    "spring.hiero.mirrorNodeSupported=true",
    "spring.hiero.eventsEnabled=true"
})
public class EventObservationIntegrationTest {

  @Autowired
  private HieroEventSubscriber eventSubscriber;

  @MockBean
  private EventRepository eventRepository;

  @MockBean
  private HieroContext hieroContext;

  @MockBean
  private MirrorNodeClient mirrorNodeClient;

  @MockBean
  private ProtocolLayerClient protocolLayerClient;

  @MockBean
  private FileClient fileClient;

  @MockBean
  private SmartContractClient smartContractClient;

  @MockBean
  private AccountClient accountClient;

  @MockBean
  private NftClient nftClient;

  @MockBean
  private FungibleTokenClient fungibleTokenClient;

  @MockBean
  private TopicClient topicClient;

  @MockBean
  private HookClient hookClient;

  @Test
  public void testEventObservation() throws HieroException, InterruptedException {
    ContractId contractId = ContractId.fromString("0.0.1234");
    
    // Stub hieroContext
    com.hedera.hashgraph.sdk.AccountId accId = com.hedera.hashgraph.sdk.AccountId.fromString("0.0.123");
    com.hedera.hashgraph.sdk.PrivateKey pKey = com.hedera.hashgraph.sdk.PrivateKey.generateED25519();
    Account testAccount = Account.of(accId, pKey);
    when(hieroContext.getOperatorAccount()).thenReturn(testAccount);
    
    // Mock Mirror Node response
    ContractLog logEntry = new ContractLog(
        contractId,
        "0xaddress",
        "0000000000000000000000000000000000000000000000000000000000000020",
        List.of("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8"),
        Instant.now(),
        "0xbloom",
        "0xblockhash",
        100L,
        "0xabc123",
        0,
        0,
        null
    );
    
    when(eventRepository.findLogs(any())).thenReturn(new SinglePage<>(List.of(logEntry)));

    CountDownLatch latch = new CountDownLatch(1);
    ContractEventListener listener = event -> {
      if (event.contractId().equals(contractId)) {
        latch.countDown();
      }
    };

    // Subscribe
    eventSubscriber.subscribe(contractId, listener);

    // Verify event is received (polling happens in background)
    boolean received = latch.await(10, TimeUnit.SECONDS);
    assert received : "Event should have been received";
  }
}
