package org.hiero.base.contract.proxy;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.hiero.base.data.ContractParam;

/**
 * Utility to map Java types to Solidity ContractParams.
 */
public class SmartContractTypeMapper {

    private static final Map<Class<?>, Function<Object, ContractParam<?>>> MAPPERS = new HashMap<>();

    static {
        MAPPERS.put(String.class, v -> ContractParam.string((String) v));
        MAPPERS.put(Boolean.class, v -> ContractParam.bool((Boolean) v));
        MAPPERS.put(boolean.class, v -> ContractParam.bool((Boolean) v));
        MAPPERS.put(Integer.class, v -> ContractParam.int32((Integer) v));
        MAPPERS.put(int.class, v -> ContractParam.int32((Integer) v));
        MAPPERS.put(Long.class, v -> ContractParam.int64((Long) v));
        MAPPERS.put(long.class, v -> ContractParam.int64((Long) v));
        MAPPERS.put(BigInteger.class, v -> ContractParam.int256((BigInteger) v));
        MAPPERS.put(AccountId.class, v -> ContractParam.address((AccountId) v));
        MAPPERS.put(ContractId.class, v -> ContractParam.address((ContractId) v));
        MAPPERS.put(byte[].class, v -> ContractParam.bytes((byte[]) v));
    }

    /**
     * Maps a Java object to a ContractParam based on its type.
     *
     * @param value the Java object
     * @return the corresponding ContractParam
     * @throws IllegalArgumentException if the type is not supported
     */
    public static ContractParam<?> map(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot map null value");
        }
        Function<Object, ContractParam<?>> mapper = MAPPERS.get(value.getClass());
        if (mapper != null) {
            return mapper.apply(value);
        }
        throw new IllegalArgumentException("Unsupported type for ContractParam: " + value.getClass().getName());
    }
}
