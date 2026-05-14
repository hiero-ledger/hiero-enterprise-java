package org.hiero.base.test.contract;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import java.math.BigInteger;
import org.hiero.base.data.ContractParam;
import org.hiero.base.contract.proxy.SmartContractTypeMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmartContractTypeMapperTest {

    @Test
    void testMapString() {
        ContractParam<?> param = SmartContractTypeMapper.map("hello");
        assertEquals("string", param.nativeType());
        assertEquals("hello", param.value());
    }

    @Test
    void testMapBoolean() {
        ContractParam<?> param = SmartContractTypeMapper.map(true);
        assertEquals("bool", param.nativeType());
        assertEquals(true, param.value());
    }

    @Test
    void testMapInteger() {
        ContractParam<?> param = SmartContractTypeMapper.map(123);
        assertEquals("int32", param.nativeType());
        assertEquals(123L, ((Long)param.value()).intValue());
    }

    @Test
    void testMapLong() {
        ContractParam<?> param = SmartContractTypeMapper.map(123L);
        assertEquals("int64", param.nativeType());
        assertEquals(123L, param.value());
    }

    @Test
    void testMapBigInteger() {
        ContractParam<?> param = SmartContractTypeMapper.map(BigInteger.valueOf(123456789));
        assertEquals("int256", param.nativeType());
        assertEquals(BigInteger.valueOf(123456789), param.value());
    }

    @Test
    void testMapAccountId() {
        AccountId id = AccountId.fromString("0.0.123");
        ContractParam<?> param = SmartContractTypeMapper.map(id);
        assertEquals("address", param.nativeType());
        assertEquals(id.toSolidityAddress(), param.value());
    }

    @Test
    void testMapContractId() {
        ContractId id = ContractId.fromString("0.0.456");
        ContractParam<?> param = SmartContractTypeMapper.map(id);
        assertEquals("address", param.nativeType());
        assertEquals(id.toSolidityAddress(), param.value());
    }

    @Test
    void testMapNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SmartContractTypeMapper.map(null));
    }

    @Test
    void testMapUnsupportedTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> SmartContractTypeMapper.map(new Object()));
    }
}
