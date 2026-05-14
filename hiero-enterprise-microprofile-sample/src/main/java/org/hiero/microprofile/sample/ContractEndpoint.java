package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hiero.base.SmartContractClient;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Path("/api/v1/contracts")
@Produces(MediaType.APPLICATION_JSON)
public class ContractEndpoint {

    @Inject
    SmartContractClient smartContractClient;

    @POST
    @Path("/demo/deploy")
    public Response deployDemo() {
        try {
            // small_contract.bin bytecode — greet() returns "Hello, World!"
            String bytecodeHex = "608060405234801561001057600080fd5b50610118806100206000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c8063b4598503146037578063fe17e8b7146070575b600080fd5b60408051808201909152600d81526c48656c6c6f2c20576f726c642160981b60208201525b604051606791906096565b60405180910390f35b60408051808201909152600b81526a48656c6c6f2c204f48472160a81b6020820152605c565b600060208083528351808285015260005b8181101560c15785810183015185820160400152820160a7565b506000604082860101526040601f19601f830116850101925050509291505056fea264697066735822122092e0b690eaa3de2581f4b757d0675c3bebfaba72b6d17ddb72855d915865693864736f6c63430008110033";

            // CRITICAL: Pass the hex string as UTF-8 bytes (NOT hex-decoded binary).
            // Hedera's ContractCreateTransaction with setBytecodeFileId expects
            // the file to contain the hex-encoded bytecode STRING.
            byte[] hexStringAsBytes = bytecodeHex.getBytes(StandardCharsets.UTF_8);

            ContractId contractId = smartContractClient.createContract(hexStringAsBytes);

            return Response.ok(Map.of(
                "status", "SUCCESS",
                "contractId", contractId.toString(),
                "message", "Demo contract (HelloWorld) deployed successfully via MicroProfile!"
            )).build();
        } catch (Exception e) {
            return Response.serverError().entity("Failed to deploy contract: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/demo/greet/{contractId}")
    public Response greetProxy(@PathParam("contractId") String contractIdStr) {
        try {
            ContractId contractId = ContractId.fromString(contractIdStr);

            Greeter greeter = smartContractClient.createProxy(Greeter.class, contractId);
            String greeting = greeter.greet();

            return Response.ok(Map.of(
                "status", "SUCCESS",
                "contractId", contractIdStr,
                "greeting", greeting,
                "explanation", "This call was made using a Type-Safe Proxy interface in MicroProfile."
            )).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
