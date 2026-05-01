package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractRepository;

@Path("/")
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final ContractRepository contractRepository;

  @Inject
  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final ContractRepository contractRepository) {
    this.client = client;
    this.blockRepository = blockRepository;
    this.contractRepository = contractRepository;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GET
  @Path("/blocks")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GET
  @Path("/contracts/{contractId}/logs")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getContractLogs(@PathParam("contractId") final String contractId) {
    try {
      final Page<ContractLog> page =
          contractRepository.findLogsById(ContractId.fromString(contractId));
      return Map.of(
          "contractId", contractId,
          "pageIndex", page.getPageIndex(),
          "size", page.getSize(),
          "hasNext", page.hasNext(),
          "data", page.getData());
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract logs for " + contractId, e);
    }
  }

  @GET
  @Path("/contracts/{contractId}/results")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getContractResults(@PathParam("contractId") final String contractId) {
    try {
      final Page<ContractResult> page =
          contractRepository.findResultsById(ContractId.fromString(contractId));
      return Map.of(
          "contractId", contractId,
          "pageIndex", page.getPageIndex(),
          "size", page.getSize(),
          "hasNext", page.hasNext(),
          "data", page.getData());
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract results for " + contractId, e);
    }
  }
}
