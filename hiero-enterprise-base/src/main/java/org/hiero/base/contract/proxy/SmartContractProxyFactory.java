package org.hiero.base.contract.proxy;

import com.hedera.hashgraph.sdk.ContractId;
import java.lang.reflect.Proxy;
import java.util.Objects;
import org.hiero.base.SmartContractClient;
import org.hiero.base.contract.SmartContract;

/**
 * Factory for creating Hiero Smart Contract proxies.
 */
public class SmartContractProxyFactory {

    private final SmartContractClient client;

    public SmartContractProxyFactory(SmartContractClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    /**
     * Creates a proxy for the given interface.
     *
     * @param interfaceClass the interface class
     * @param <T> the type of the interface
     * @return a proxy instance
     * @throws IllegalArgumentException if the interface is missing @SmartContract annotation and no address is provided
     */
    public <T> T createProxy(Class<T> interfaceClass) {
        SmartContract annotation = interfaceClass.getAnnotation(SmartContract.class);
        if (annotation == null || annotation.value().isEmpty()) {
            throw new IllegalArgumentException("Interface must be annotated with @SmartContract and have a valid address");
        }
        return createProxy(interfaceClass, ContractId.fromString(annotation.value()));
    }

    /**
     * Creates a proxy for the given interface with a specific contract ID.
     *
     * @param interfaceClass the interface class
     * @param contractId the contract ID
     * @param <T> the type of the interface
     * @return a proxy instance
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> interfaceClass, ContractId contractId) {
        Objects.requireNonNull(interfaceClass, "interfaceClass must not be null");
        Objects.requireNonNull(contractId, "contractId must not be null");
        
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + " must be an interface");
        }

        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new SmartContractInvocationHandler(client, contractId)
        );
    }
}
