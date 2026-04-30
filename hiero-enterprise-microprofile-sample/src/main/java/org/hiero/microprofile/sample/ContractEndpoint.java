package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hiero.base.SmartContractClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Path("/contracts")
@Produces(MediaType.APPLICATION_JSON)
public class ContractEndpoint {

    private final SmartContractClient smartContractClient;

    @Inject
    public ContractEndpoint(SmartContractClient smartContractClient) {
        this.smartContractClient = smartContractClient;
    }

    /**
     * Demonstrates deploying a smart contract in MicroProfile.
     */
    @POST
    @Path("/demo/deploy")
    public Response deployDemo() {
        try {
            // Load bytecode from resources
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("HelloWorld.bin")) {
                if (is == null) {
                    throw new RuntimeException("HelloWorld.bin not found");
                }
                String bytecodeHex = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                byte[] bytecode = hexToBytes(bytecodeHex.trim());

                // Deploy contract
                ContractId contractId = smartContractClient.createContract(bytecode);

                return Response.ok(Map.of(
                    "status", "SUCCESS",
                    "contractId", contractId.toString(),
                    "message", "Demo contract (HelloWorld) deployed successfully via MicroProfile!"
                )).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Demonstrates calling a smart contract function using a Type-Safe Proxy.
     */
    @GET
    @Path("/demo/greet/{contractId}")
    public Response greetDemo(@PathParam("contractId") String contractId) {
        try {
            // 1. Create a type-safe proxy for the Greeter interface
            Greeter greeter = smartContractClient.createProxy(Greeter.class, contractId);

            // 2. Call the contract function just like a regular Java method!
            String message = greeter.greet();

            return Response.ok(Map.of(
                "contractId", contractId,
                "proxyResult", message,
                "framework", "Jakarta EE / MicroProfile"
            )).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
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
