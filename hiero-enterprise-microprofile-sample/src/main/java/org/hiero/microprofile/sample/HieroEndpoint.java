package org.hiero.microprofile.sample;

import com.hedera.hashgraph.sdk.TokenId;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
import org.hiero.base.data.ContractCallResult;

@Path("/")
public class HieroEndpoint {

  private final TopicClient topicClient;
  private final FungibleTokenClient tokenClient;
  private final SmartContractClient smartContractClient;

  @Inject
  public HieroEndpoint(
      final TopicClient topicClient,
      final FungibleTokenClient tokenClient,
      final SmartContractClient smartContractClient) {
    this.topicClient = topicClient;
    this.tokenClient = tokenClient;
    this.smartContractClient = smartContractClient;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String health() {
    return "Hiero MicroProfile sample is running";
  }

  @POST
  @Path("/topics")
  @Produces(MediaType.APPLICATION_JSON)
  public Response createTopic(final TopicCreateRequest request) {
    try {
      final String memo =
          request.memo() == null || request.memo().isBlank() ? "sample-topic" : request.memo();
      return Response.ok(new TopicCreateResponse(topicClient.createTopic(memo).toString())).build();
    } catch (final Exception e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/tokens/transfer")
  @Produces(MediaType.APPLICATION_JSON)
  public Response transferToken(final TokenTransferRequest request) {
    try {
      tokenClient.transferToken(
          TokenId.fromString(request.tokenId()), request.toAccountId(), request.amount());
      return Response.ok(new TokenTransferResponse("Token transfer submitted")).build();
    } catch (final Exception e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/contracts/call")
  @Produces(MediaType.APPLICATION_JSON)
  public Response callContract(final ContractCallRequest request) {
    try {
      final ContractCallResult result =
          smartContractClient.callContractFunction(request.contractId(), request.functionName());
      return Response.ok(new ContractCallResponse(result.gasUsed(), result.cost().toString()))
          .build();
    } catch (final Exception e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  public record TopicCreateRequest(String memo) {}

  public record TopicCreateResponse(String topicId) {}

  public record TokenTransferRequest(String tokenId, String toAccountId, long amount) {}

  public record TokenTransferResponse(String status) {}

  public record ContractCallRequest(String contractId, String functionName) {}

  public record ContractCallResponse(long gasUsed, String cost) {}
}
