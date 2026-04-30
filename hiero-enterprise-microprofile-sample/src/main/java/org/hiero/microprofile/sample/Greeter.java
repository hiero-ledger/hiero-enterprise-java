package org.hiero.microprofile.sample;

import org.hiero.base.contract.SmartContract;
import org.hiero.base.contract.SmartContractFunction;

/**
 * A type-safe proxy interface for the HelloWorld smart contract.
 */
@SmartContract
public interface Greeter {

    /**
     * Calls the greet() function of the smart contract.
     * @return the greeting message
     */
    @SmartContractFunction("greet")
    String greet();
}
