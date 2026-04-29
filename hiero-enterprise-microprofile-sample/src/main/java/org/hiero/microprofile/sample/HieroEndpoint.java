package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.AccountId;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.HieroTransactionRecord;
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
      return "Account " + account.accountId() + " created!";
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
  @Path("/info/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public AccountInfo getAccountInfo(@PathParam("id") String id) {
    try {
      return client.getAccountInfo(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account info", e);
    }
  }

  @GET
  @Path("/records/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<HieroTransactionRecord> getAccountRecords(@PathParam("id") String id) {
    try {
      return client.getAccountRecords(AccountId.fromString(id));
    } catch (final Exception e) {
      throw new RuntimeException("Error retrieving account records", e);
    }
  }

  @POST
  @Path("/update/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public String updateMemo(@PathParam("id") String id, @QueryParam("memo") String memo) {
    try {
      client.updateAccount(AccountId.fromString(id))
          .memo(memo)
          .execute();
      return "Account updated!";
    } catch (final Exception e) {
      throw new RuntimeException("Error updating account", e);
    }
  }
}
