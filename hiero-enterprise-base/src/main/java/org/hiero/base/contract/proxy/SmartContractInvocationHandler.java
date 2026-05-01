package org.hiero.base.contract.proxy;

import com.hedera.hashgraph.sdk.ContractId;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.hiero.base.SmartContractClient;
import org.hiero.base.contract.SmartContractFunction;
import org.hiero.base.data.ContractParam;

/**
 * Invocation handler for Hiero Smart Contract proxies.
 */
public class SmartContractInvocationHandler implements InvocationHandler {

    private final SmartContractClient client;
    private final ContractId contractId;

    public SmartContractInvocationHandler(SmartContractClient client, ContractId contractId) {
        this.client = client;
        this.contractId = contractId;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Handle methods from Object class (equals, hashCode, toString)
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        SmartContractFunction annotation = method.getAnnotation(SmartContractFunction.class);
        String functionName = (annotation != null && !annotation.value().isEmpty()) 
            ? annotation.value() 
            : method.getName();

        List<ContractParam<?>> params = new ArrayList<>();
        if (args != null) {
            for (Object arg : args) {
                params.add(SmartContractTypeMapper.map(arg));
            }
        }

        var result = client.callContractFunction(
            contractId, 
            functionName, 
            params.toArray(new ContractParam[0])
        );

        // Extract the correct return type based on the method's declared return type
        Class<?> returnType = method.getReturnType();
        if (returnType == String.class) {
            return result.getString(0);
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return result.getBool(0);
        } else if (returnType == int.class || returnType == Integer.class) {
            return result.getInt32(0);
        } else if (returnType == long.class || returnType == Long.class) {
            return result.getInt64(0);
        } else if (returnType == byte.class || returnType == Byte.class) {
            return result.getInt8(0);
        } else if (returnType == java.math.BigInteger.class) {
            return result.getInt256(0);
        } else if (returnType == void.class || returnType == Void.class) {
            return null;
        }
        // For any other type, return the raw result
        return result;
    }
}
