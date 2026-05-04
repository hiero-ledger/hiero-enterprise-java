package org.hiero.microprofile.sample;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.ContractLogRepository;
import org.hiero.base.data.ContractLog;
import jakarta.ws.rs.PathParam;

@Path("/")
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final ContractLogRepository contractLogRepository;

  @Inject
  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final ContractLogRepository contractLogRepository) {
    this.client = client;
    this.blockRepository = blockRepository;
    this.contractLogRepository = contractLogRepository;
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
  @Path("/contracts/{id}/logs")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<ContractLog> getContractLogs(@PathParam("id") String id) {
    try {
      return contractLogRepository.findByContractId(id);
    } catch (final Exception e) {
      throw new RuntimeException("Error querying contract logs", e);
    }
  }
}
