package org.hiero.spring.implementation;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.hashgraph.sdk.ContractId;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

public class ContractVerificationClientImplementation implements ContractVerificationClient {

  private static final String CONTRACT_VERIFICATION_URL = "https://sourcify.dev/server";
  private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);
  private static final Duration POLL_TIMEOUT = Duration.ofMinutes(5);

  private record VerifyRequest(Map<String, String> sources, Map<String, Object> metadata) {}

  private final HieroConfig hieroConfig;

  private final ObjectMapper objectMapper;

  private final RestClient restClient;

  public ContractVerificationClientImplementation(@NonNull final HieroConfig hieroConfig) {
    this.hieroConfig = Objects.requireNonNull(hieroConfig, "hieroConfig must not be null");
    objectMapper = new ObjectMapper();
    restClient = RestClient.create();
  }

  @NonNull
  private String getChainId() throws HieroException {
    return hieroConfig
        .chainId()
        .map(id -> Long.toString(id))
        .orElseThrow(() -> new HieroException("Chain ID is not set"));
  }

  private void handleError(
      @NonNull final HttpRequest request, @NonNull final ClientHttpResponse response)
      throws IOException {
    Objects.requireNonNull(response, "response must not be null");
    final String body;
    try {
      body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
    } catch (final Exception e) {
      throw new IOException(
          "Error (" + response.getStatusCode() + "): " + response.getStatusText(), e);
    }

    final String error;
    try {
      final JsonNode rootNode = objectMapper.readTree(body);
      final JsonNode errorNode = rootNode.get("error");
      if (errorNode != null) {
        error = errorNode.asText();
      } else {
        final JsonNode messageNode = rootNode.get("message");
        if (messageNode != null) {
          error = messageNode.asText();
        } else {
          error = body;
        }
      }
    } catch (final Exception e) {
      throw new IOException("Error parsing body as JSON: " + body, e);
    }
    throw new IOException("Error (" + response.getStatusCode() + "): " + error);
  }

  @Override
  public ContractVerificationState verify(
      @NonNull final ContractId contractId,
      @NonNull final String contractName,
      @NonNull final Map<String, String> files)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(contractName, "contractName must not be null");
    Objects.requireNonNull(files, "files must not be null");

    if (!files.containsKey("metadata.json")) {
      throw new IllegalArgumentException("metadata.json must be present in files");
    }

    final ContractVerificationState state = checkVerification(contractId);

    if (state != ContractVerificationState.NONE) {
      throw new IllegalStateException("Contract is already verified");
    }

    try {
      final Map<String, String> sources = new HashMap<>(files);
      final String metadataJson = sources.remove("metadata.json");

      final Map<String, Object> metadata =
          objectMapper.readValue(metadataJson, new TypeReference<Map<String, Object>>() {});
      final VerifyRequest verifyRequest = new VerifyRequest(sources, metadata);

      final String uri =
          CONTRACT_VERIFICATION_URL
              + "/v2/verify/metadata/"
              + getChainId()
              + "/0x"
              + contractId.toEvmAddress();
      final String resultBody =
          restClient
              .post()
              .uri(uri)
              .contentType(APPLICATION_JSON)
              .accept(APPLICATION_JSON)
              .body(verifyRequest)
              .retrieve()
              .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
              .body(String.class);

      final JsonNode rootNode = objectMapper.readTree(resultBody);
      if (!rootNode.hasNonNull("verificationId")) {
        throw new HieroException("Response does not contain verificationId");
      }

      final String verificationId = rootNode.get("verificationId").asText();
      return pollVerificationStatus(verificationId);
    } catch (Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }

  private ContractVerificationState pollVerificationStatus(final @NonNull String verificationId) {
    Objects.requireNonNull(verificationId, "verificationId must not be null");

    final long deadline = System.nanoTime() + POLL_TIMEOUT.toNanos();
    try {
      final String uri = CONTRACT_VERIFICATION_URL + "/v2/verify/" + verificationId;

      while (System.nanoTime() < deadline) {
        final String status =
            restClient
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
                .body(String.class);

        final JsonNode rootNode = objectMapper.readTree(status);

        if (!rootNode.get("isJobCompleted").asBoolean(false)) {
          Thread.sleep(POLL_INTERVAL.toMillis());
          continue;
        }

        if (!rootNode.get("contract").hasNonNull("match")) {
          return ContractVerificationState.NONE;
        }

        final String matchStatus = rootNode.get("contract").get("match").asText(null);

        if (matchStatus.equals("exact_match")) {
          return ContractVerificationState.FULL;
        }

        if (matchStatus.equals("match")) {
          return ContractVerificationState.PARTIAL;
        }

        return ContractVerificationState.NONE;
      }

      throw new HieroException(
          "Timed out waiting for contract verification job: " + verificationId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ContractVerificationState checkVerification(@NonNull final ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");

    final String uri =
        CONTRACT_VERIFICATION_URL
            + "/v2/contract/"
            + getChainId()
            + "/0x"
            + contractId.toEvmAddress()
            + "?fields=sources";

    try {
      final String resultBody =
          restClient
              .get()
              .uri(uri)
              .retrieve()
              .onStatus(status -> status.value() == 404, (request, response) -> {})
              .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
              .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
              .body(String.class);

      if (resultBody == null || resultBody.isBlank()) {
        return ContractVerificationState.NONE;
      }

      final JsonNode rootNode = objectMapper.readTree(resultBody);

      final String matchStatus = rootNode.get("match").asText();
      if (matchStatus.equals("match")) {
        return ContractVerificationState.PARTIAL;
      }

      if (matchStatus.equals("exact_match")) {
        return ContractVerificationState.FULL;
      }

      return ContractVerificationState.NONE;
    } catch (Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }

  @Override
  public boolean checkVerification(
      @NonNull final ContractId contractId,
      @NonNull final String fileName,
      @NonNull final String fileContent)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(fileName, "fileName must not be null");
    Objects.requireNonNull(fileContent, "fileContent must not be null");

    final ContractVerificationState state = checkVerification(contractId);
    if (state != ContractVerificationState.FULL && state != ContractVerificationState.PARTIAL) {
      throw new IllegalStateException("Contract is not verified");
    }

    final String uri =
        CONTRACT_VERIFICATION_URL
            + "/v2/contract/"
            + getChainId()
            + "/0x"
            + contractId.toEvmAddress()
            + "?fields=sources";

    try {
      final String resultBody =
          restClient
              .get()
              .uri(uri)
              .accept(APPLICATION_JSON)
              .retrieve()
              .onStatus(status -> status.value() == 404, (request, response) -> {})
              .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
              .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
              .body(String.class);

      if (resultBody == null || resultBody.isBlank()) {
        return false;
      }

      final JsonNode rootNode = objectMapper.readTree(resultBody);
      final JsonNode contentNode = rootNode.path("sources").path(fileName).path("content");

      return !contentNode.isMissingNode() && fileContent.equals(contentNode.asText());
    } catch (Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }
}
