package org.hiero.base.test.proxy;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import java.math.BigInteger;
import org.hiero.base.SmartContractClient;
import org.hiero.base.annotations.ContractFunction;
import org.hiero.base.annotations.HieroContract;
import org.hiero.base.data.ContractCallResult;
import org.hiero.base.data.ContractParam;
import org.hiero.base.proxy.ContractProxyFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ContractProxyFactoryTest {

    @HieroContract(address = "0.0.12345")
    interface TestContract {
        @ContractFunction
        ContractCallResult transfer(AccountId recipient, BigInteger amount);

        @ContractFunction("balanceOf")
        ContractCallResult getBalance(AccountId account);

        void noAnnotationMethod(String arg);
    }

    @Test
    void testProxyCallTranslatesToClientCall() throws Exception {
        SmartContractClient mockClient = mock(SmartContractClient.class);
        ContractId contractId = ContractId.fromString("0.0.12345");
        AccountId recipient = AccountId.fromString("0.0.999");
        BigInteger amount = BigInteger.valueOf(1000);
        
        ContractProxyFactory factory = new ContractProxyFactory(mockClient);
        TestContract proxy = factory.createProxy(TestContract.class);

        proxy.transfer(recipient, amount);

        ArgumentCaptor<ContractParam<?>[]> paramsCaptor = ArgumentCaptor.forClass(ContractParam[].class);
        verify(mockClient).callContractFunction(eq(contractId), eq("transfer"), paramsCaptor.capture());

        ContractParam<?>[] capturedParams = paramsCaptor.getValue();
        assertEquals(2, capturedParams.length);
        assertEquals("address", capturedParams[0].nativeType());
        assertEquals(recipient.toSolidityAddress(), capturedParams[0].value());
        assertEquals("int256", capturedParams[1].nativeType());
        assertEquals(amount, capturedParams[1].value());
    }

    @Test
    void testProxyCallWithCustomFunctionName() throws Exception {
        SmartContractClient mockClient = mock(SmartContractClient.class);
        ContractId contractId = ContractId.fromString("0.0.12345");
        AccountId account = AccountId.fromString("0.0.888");
        
        ContractProxyFactory factory = new ContractProxyFactory(mockClient);
        TestContract proxy = factory.createProxy(TestContract.class);

        proxy.getBalance(account);

        verify(mockClient).callContractFunction(eq(contractId), eq("balanceOf"), any(ContractParam[].class));
    }

    @Test
    void testProxyCallWithoutAnnotationUsesMethodName() throws Exception {
        SmartContractClient mockClient = mock(SmartContractClient.class);
        ContractId contractId = ContractId.fromString("0.0.12345");
        
        ContractProxyFactory factory = new ContractProxyFactory(mockClient);
        TestContract proxy = factory.createProxy(TestContract.class);

        proxy.noAnnotationMethod("test");

        verify(mockClient).callContractFunction(eq(contractId), eq("noAnnotationMethod"), any(ContractParam[].class));
    }

    @Test
    void testCreateProxyWithoutAnnotationThrowsException() {
        interface NoAnnotation {}
        SmartContractClient mockClient = mock(SmartContractClient.class);
        ContractProxyFactory factory = new ContractProxyFactory(mockClient);

        assertThrows(IllegalArgumentException.class, () -> factory.createProxy(NoAnnotation.class));
    }
}
