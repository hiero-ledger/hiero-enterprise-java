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

@Path("/")
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;

  @Inject
  public HieroEndpoint(final AccountClient client, final BlockRepository blockRepository) {
    this.client = client;
    this.blockRepository = blockRepository;
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
}
