package org.hiero.base.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.HieroTransactionRecord;
import org.hiero.base.implementation.AccountClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountInfoRequest;
import org.hiero.base.protocol.data.AccountInfoResponse;
import org.hiero.base.protocol.data.AccountRecordsRequest;
import org.hiero.base.protocol.data.AccountRecordsResponse;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.AllowanceApproveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class AdvancedAccountClientTest {

    private ProtocolLayerClient mockClient;
    private AccountClientImpl accountClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(ProtocolLayerClient.class);
        accountClient = new AccountClientImpl(mockClient);
    }

    @Test
    void testGetAccountInfo() throws HieroException {
        AccountId id = AccountId.fromString("0.0.123");
        AccountInfo mockInfo = mock(AccountInfo.class);
        when(mockClient.executeAccountInfoQuery(any(AccountInfoRequest.class)))
            .thenReturn(new AccountInfoResponse(mockInfo));

        AccountInfo result = accountClient.getAccountInfo(id);

        assertEquals(mockInfo, result);
        verify(mockClient).executeAccountInfoQuery(argThat(r -> r.accountId().equals(id)));
    }

    @Test
    void testGetAccountRecords() throws HieroException {
        AccountId id = AccountId.fromString("0.0.123");
        HieroTransactionRecord mockRecord = mock(HieroTransactionRecord.class);
        when(mockClient.executeAccountRecordsQuery(any(AccountRecordsRequest.class)))
            .thenReturn(new AccountRecordsResponse(List.of(mockRecord)));

        List<HieroTransactionRecord> result = accountClient.getAccountRecords(id);

        assertEquals(1, result.size());
        assertEquals(mockRecord, result.get(0));
    }

    @Test
    void testUpdateAccountFluent() throws HieroException {
        AccountId id = AccountId.fromString("0.0.123");
        PrivateKey newKey = PrivateKey.generateED25519();
        String newMemo = "Updated Memo";

        accountClient.updateAccount(id)
            .key(newKey.getPublicKey())
            .memo(newMemo)
            .execute();

        ArgumentCaptor<AccountUpdateRequest> captor = ArgumentCaptor.forClass(AccountUpdateRequest.class);
        verify(mockClient).executeAccountUpdateTransaction(captor.capture());

        AccountUpdateRequest request = captor.getValue();
        assertEquals(id, request.accountId());
        assertEquals(newKey.getPublicKey(), request.key());
        assertEquals(newMemo, request.memo());
    }

    @Test
    void testApproveHbarAllowance() throws HieroException {
        AccountId spenderId = AccountId.fromString("0.0.456");
        Hbar amount = Hbar.from(50);

        accountClient.approveHbarAllowance(spenderId, amount);

        ArgumentCaptor<AllowanceApproveRequest> captor = ArgumentCaptor.forClass(AllowanceApproveRequest.class);
        verify(mockClient).executeAccountAllowanceApproveTransaction(captor.capture());

        AllowanceApproveRequest request = captor.getValue();
        assertEquals(spenderId, request.hbarSpenderId());
        assertEquals(amount, request.hbarAmount());
    }

    @Test
    void testApproveTokenAllowance() throws HieroException {
        TokenId tokenId = TokenId.fromString("0.0.789");
        AccountId spenderId = AccountId.fromString("0.0.456");
        long amount = 1000;

        accountClient.approveTokenAllowance(tokenId, spenderId, amount);

        ArgumentCaptor<AllowanceApproveRequest> captor = ArgumentCaptor.forClass(AllowanceApproveRequest.class);
        verify(mockClient).executeAccountAllowanceApproveTransaction(captor.capture());

        AllowanceApproveRequest request = captor.getValue();
        assertEquals(tokenId, request.tokenId());
        assertEquals(spenderId, request.tokenSpenderId());
        assertEquals(amount, request.tokenAmount());
    }
}
