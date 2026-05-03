package org.hiero.spring.sample;

import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.TopicClient;
import org.hiero.base.data.ContractCallResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final TopicClient topicClient;
  private final FungibleTokenClient tokenClient;
  private final SmartContractClient smartContractClient;

  public HieroEndpoint(
      final TopicClient topicClient,
      final FungibleTokenClient tokenClient,
      final SmartContractClient smartContractClient) {
    this.topicClient = Objects.requireNonNull(topicClient, "topicClient must not be null");
    this.tokenClient = Objects.requireNonNull(tokenClient, "tokenClient must not be null");
    this.smartContractClient =
        Objects.requireNonNull(smartContractClient, "smartContractClient must not be null");
  }

  @PostMapping("/topics")
  public TopicCreateResponse createTopic(@RequestBody final TopicCreateRequest request) {
    try {
      final String memo =
          request.memo() == null || request.memo().isBlank() ? "sample-topic" : request.memo();
      return new TopicCreateResponse(topicClient.createTopic(memo).toString());
    } catch (final Exception e) {
      throw new RuntimeException("Error creating topic", e);
    }
  }

  @PostMapping("/tokens/transfer")
  public TokenTransferResponse transferToken(@RequestBody final TokenTransferRequest request) {
    try {
      tokenClient.transferToken(
          TokenId.fromString(request.tokenId()), request.toAccountId(), request.amount());
      return new TokenTransferResponse("Token transfer submitted");
    } catch (final Exception e) {
      throw new RuntimeException("Error transferring token", e);
    }
  }

  @PostMapping("/contracts/call")
  public ContractCallResponse callContract(@RequestBody final ContractCallRequest request) {
    try {
      final ContractCallResult result =
          smartContractClient.callContractFunction(request.contractId(), request.functionName());
      return new ContractCallResponse(result.gasUsed(), result.cost().toString());
    } catch (final Exception e) {
      throw new RuntimeException("Error calling contract function", e);
    }
  }

  public record TopicCreateRequest(String memo) {}

  public record TopicCreateResponse(String topicId) {}

  public record TokenTransferRequest(String tokenId, String toAccountId, long amount) {}

  public record TokenTransferResponse(String status) {}

  public record ContractCallRequest(String contractId, String functionName) {}

  public record ContractCallResponse(long gasUsed, String cost) {}
}
