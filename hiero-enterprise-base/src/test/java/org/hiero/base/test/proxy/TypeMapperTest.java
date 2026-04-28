package org.hiero.base.test.proxy;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import java.math.BigInteger;
import org.hiero.base.data.ContractParam;
import org.hiero.base.proxy.TypeMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypeMapperTest {

    @Test
    void testMapString() {
        ContractParam<?> param = TypeMapper.map("hello");
        assertEquals("string", param.nativeType());
        assertEquals("hello", param.value());
    }

    @Test
    void testMapBoolean() {
        ContractParam<?> param = TypeMapper.map(true);
        assertEquals("bool", param.nativeType());
        assertEquals(true, param.value());
    }

    @Test
    void testMapInteger() {
        ContractParam<?> param = TypeMapper.map(123);
        assertEquals("int32", param.nativeType());
        assertEquals(123L, ((Long)param.value()).intValue());
    }

    @Test
    void testMapLong() {
        ContractParam<?> param = TypeMapper.map(123L);
        assertEquals("int64", param.nativeType());
        assertEquals(123L, param.value());
    }

    @Test
    void testMapBigInteger() {
        ContractParam<?> param = TypeMapper.map(BigInteger.valueOf(123456789));
        assertEquals("int256", param.nativeType());
        assertEquals(BigInteger.valueOf(123456789), param.value());
    }

    @Test
    void testMapAccountId() {
        AccountId id = AccountId.fromString("0.0.123");
        ContractParam<?> param = TypeMapper.map(id);
        assertEquals("address", param.nativeType());
        assertEquals(id.toSolidityAddress(), param.value());
    }

    @Test
    void testMapContractId() {
        ContractId id = ContractId.fromString("0.0.456");
        ContractParam<?> param = TypeMapper.map(id);
        assertEquals("address", param.nativeType());
        assertEquals(id.toSolidityAddress(), param.value());
    }

    @Test
    void testMapNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> TypeMapper.map(null));
    }

    @Test
    void testMapUnsupportedTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> TypeMapper.map(new Object()));
    }
}
