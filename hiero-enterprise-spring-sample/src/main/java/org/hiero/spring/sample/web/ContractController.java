package org.hiero.spring.sample.web;

import com.hedera.hashgraph.sdk.ContractId;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.ContractCallResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    private final SmartContractClient smartContractClient;

    public ContractController(SmartContractClient smartContractClient) {
        this.smartContractClient = smartContractClient;
    }

    /**
     * Demonstrates deploying a smart contract.
     */
    @PostMapping("/demo/deploy")
    public ResponseEntity<?> deployDemo() {
        try {
            // Load bytecode from resources
            ClassPathResource resource = new ClassPathResource("HelloWorld.bin");
            String bytecodeHex = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            byte[] bytecode = hexToBytes(bytecodeHex.trim());

            // Deploy contract
            ContractId contractId = smartContractClient.createContract(bytecode);
            
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "contractId", contractId.toString(),
                "message", "Demo contract (HelloWorld) deployed successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Demonstrates calling a smart contract function using a Type-Safe Proxy.
     */
    @GetMapping("/demo/greet/{contractId}")
    public ResponseEntity<?> greetDemo(@PathVariable("contractId") String contractId) {
        try {
            // 1. Create a type-safe proxy for the Greeter interface
            Greeter greeter = smartContractClient.createProxy(Greeter.class, contractId);

            // 2. Call the contract function just like a regular Java method!
            // The framework handles the translation to Solidity 'greet()' and maps the result.
            String message = greeter.greet();

            return ResponseEntity.ok(Map.of(
                "contractId", contractId,
                "proxyResult", message,
                "explanation", "This call was made using a Type-Safe Proxy interface."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<?> getInfo(@PathVariable("contractId") String contractId) {
        try {
            return ResponseEntity.ok(Collections.singletonMap("contractId", contractId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
