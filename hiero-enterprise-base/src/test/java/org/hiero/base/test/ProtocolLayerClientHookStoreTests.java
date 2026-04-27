package org.hiero.base.test;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookEntityId;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.protocol.data.HookStoreRequest;
import org.hiero.base.protocol.data.HookStoreResult;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProtocolLayerClientHookStoreTests {

  @Test
  void testHookStoreUsesSignerKeysAndExecutesAsHookStoreTransaction() throws Exception {
    // given
    final PrivateKey operatorKey = PrivateKey.generateED25519();
    final PrivateKey signerOne = PrivateKey.generateED25519();
    final PrivateKey signerTwo = PrivateKey.generateED25519();
    final TestableProtocolLayerClient client =
        new TestableProtocolLayerClient(
            new SimpleHieroContext(Account.of(new AccountId(0, 0, 1234), operatorKey)));
    final HookStoreRequest request =
        new HookStoreRequest(
            HookStoreRequest.DEFAULT_MAX_TRANSACTION_FEE,
            Duration.ofSeconds(120),
            new HookId(new HookEntityId(new ContractId(0, 0, 9876)), 1L),
            List.of(new EvmHookStorageUpdate.EvmHookStorageSlot(new byte[] {1}, new byte[] {2})),
            List.of(signerOne, signerTwo));

    // when
    final HookStoreResult result = client.executeHookStoreTransaction(request);

    // then
    Assertions.assertArrayEquals(new PrivateKey[] {signerOne, signerTwo}, client.signedKeys);
    Assertions.assertEquals(TransactionType.HOOK_STORE, client.executedType);
    Assertions.assertEquals(Status.SUCCESS, result.status());
    Assertions.assertNotNull(result.transactionId());
  }

  private static final class TestableProtocolLayerClient extends ProtocolLayerClientImpl {
    private PrivateKey[] signedKeys;
    private TransactionType executedType;

    private TestableProtocolLayerClient(@NonNull final HieroContext context) {
      super(context);
    }

    @Override
    protected <T extends Transaction<T>> Transaction<T> sign(
        final Transaction<T> transaction, final PrivateKey... keys) {
      signedKeys = keys;
      return transaction;
    }

    @Override
    protected <T extends Transaction<T>> TransactionReceipt executeTransactionAndWaitOnReceipt(
        @NonNull final T transaction, @NonNull final TransactionType type) throws HieroException {
      executedType = type;
      return createSuccessfulReceipt();
    }

    private static TransactionReceipt createSuccessfulReceipt() {
      try {
        final Constructor<TransactionReceipt> constructor =
            TransactionReceipt.class.getDeclaredConstructor(
                TransactionId.class,
                Status.class,
                com.hedera.hashgraph.sdk.ExchangeRate.class,
                com.hedera.hashgraph.sdk.ExchangeRate.class,
                com.hedera.hashgraph.sdk.AccountId.class,
                com.hedera.hashgraph.sdk.FileId.class,
                com.hedera.hashgraph.sdk.ContractId.class,
                com.hedera.hashgraph.sdk.TopicId.class,
                com.hedera.hashgraph.sdk.TokenId.class,
                Long.class,
                ByteString.class,
                Long.class,
                com.hedera.hashgraph.sdk.ScheduleId.class,
                TransactionId.class,
                List.class,
                long.class,
                List.class,
                List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(
            TransactionId.generate(new AccountId(0, 0, 1234)),
            Status.SUCCESS,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            0L,
            List.of(),
            List.of());
      } catch (final Exception e) {
        throw new RuntimeException("Failed to create successful transaction receipt", e);
      }
    }
  }

  private static final class SimpleHieroContext implements HieroContext {
    private final Account operatorAccount;

    private SimpleHieroContext(@NonNull final Account operatorAccount) {
      this.operatorAccount = operatorAccount;
    }

    @Override
    public @NonNull Account getOperatorAccount() {
      return operatorAccount;
    }

    @Override
    public @NonNull Client getClient() {
      return Client.forTestnet();
    }
  }
}
