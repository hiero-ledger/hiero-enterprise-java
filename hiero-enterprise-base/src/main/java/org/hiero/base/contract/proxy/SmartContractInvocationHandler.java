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

        return client.callContractFunction(
            contractId, 
            functionName, 
            params.toArray(new ContractParam[0])
        );
    }
}
