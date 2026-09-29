package org.hiero.microprofile.implementation;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ContractVerificationClientImpl implements ContractVerificationClient {
  private static final String CONTRACT_VERIFICATION_URL = "https://sourcify.dev/server/v2";
  private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);
  private static final Duration POLL_TIMEOUT = Duration.ofMinutes(5);

  private final HieroConfig hieroConfig;

  private final JsonReaderFactory jsonReaderFactory;
  private final Client webClient;

  public ContractVerificationClientImpl(@NonNull final HieroConfig hieroConfig) {
    this.hieroConfig = Objects.requireNonNull(hieroConfig, "hieroConfig must not be null");
    jsonReaderFactory = Json.createReaderFactory(Map.of());
    webClient = ClientBuilder.newBuilder().build();
  }

  private String getChainId() throws HieroException {
    return hieroConfig
        .chainId()
        .map(id -> Long.toString(id))
        .orElseThrow(() -> new HieroException("Chain ID is not set"));
  }

  private void handleError(@NonNull final Response response) throws IOException {
    final String body = response.readEntity(String.class);
    throw new IOException("Error response: " + body);
  }

  @Override
  public @NonNull ContractVerificationState verify(
      @NonNull ContractId contractId,
      @NonNull String contractName,
      @NonNull Map<String, String> files)
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

    final Map<String, String> sourceFiles = new HashMap<>(files);
    final String metadataJson = sourceFiles.remove("metadata.json");

    if (metadataJson == null || metadataJson.isBlank()) {
      throw new IllegalArgumentException("metadata.json must not be empty");
    }

    final JsonObject metadata = parseJsonObject(metadataJson);
    final JsonObjectBuilder sources = Json.createObjectBuilder();
    for (Map.Entry<String, String> entry : sourceFiles.entrySet()) {
      sources.add(entry.getKey(), entry.getValue());
    }

    final JsonObject requestBody =
        Json.createObjectBuilder().add("sources", sources).add("metadata", metadata).build();

    final String uri =
        CONTRACT_VERIFICATION_URL
            + "/verify/metadata/"
            + getChainId()
            + "/0x"
            + contractId.toEvmAddress();

    try (Response response =
        webClient
            .target(uri)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))) {
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }

      final String resultBody = response.readEntity(String.class);
      final JsonObject root = parseJsonObject(resultBody);

      final String verificationId = root.getString("verificationId", null);
      if (verificationId == null) {
        throw new HieroException("Response does not contain verificationId");
      }

      return pollVerificationStatus(verificationId);
    } catch (Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }

  @NonNull
  @Override
  public ContractVerificationState checkVerification(@NonNull ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");

    final String uri =
        CONTRACT_VERIFICATION_URL
            + "/contract/"
            + getChainId()
            + "/0x"
            + contractId.toEvmAddress()
            + "?fields=sources";

    try (Response response = webClient.target(uri).request(MediaType.APPLICATION_JSON).get()) {

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        return ContractVerificationState.NONE;
      }

      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }

      final String resultBody = response.readEntity(String.class);

      if (resultBody == null || resultBody.isBlank()) {
        return ContractVerificationState.NONE;
      }

      final JsonObject root = parseJsonObject(resultBody);
      return resolveVerificationState(root.getString("match", null));
    } catch (Exception e) {
      throw new HieroException("Error checking contract verification", e);
    }
  }

  @Override
  public boolean checkVerification(
      @NonNull ContractId contractId, @NonNull String fileName, @NonNull String fileContent)
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
            + "/contract/"
            + getChainId()
            + "/0x"
            + contractId.toEvmAddress()
            + "?fields=sources";

    try (Response response = webClient.target(uri).request(MediaType.APPLICATION_JSON).get()) {

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        return false;
      }

      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }

      final String resultBody = response.readEntity(String.class);

      if (resultBody == null || resultBody.isBlank()) {
        return false;
      }

      final JsonObject root = parseJsonObject(resultBody);
      final JsonObject sources = root.getJsonObject("sources");
      if (sources == null) {
        return false;
      }

      final JsonObject source = sources.getJsonObject(fileName);
      if (source == null) {
        return false;
      }

      final String content = source.getString("content", null);
      return Objects.equals(content, fileContent);

    } catch (Exception e) {
      throw new HieroException("Error checking verified source file", e);
    }
  }

  private @NonNull ContractVerificationState pollVerificationStatus(
      @NonNull final String verificationId) throws HieroException {
    Objects.requireNonNull(verificationId, "verificationId must not be null");

    final long deadline = System.nanoTime() + POLL_TIMEOUT.toNanos();
    final String uri = CONTRACT_VERIFICATION_URL + "/verify/" + verificationId;

    try {
      while (System.nanoTime() < deadline) {
        try (Response response = webClient.target(uri).request(MediaType.APPLICATION_JSON).get()) {

          if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            handleError(response);
          }

          final String resultBody = response.readEntity(String.class);
          final JsonObject root = parseJsonObject(resultBody);

          if (!root.getBoolean("isJobCompleted", false)) {
            Thread.sleep(POLL_INTERVAL.toMillis());
            continue;
          }

          final JsonObject errorNode = root.getJsonObject("error");
          if (errorNode != null) {
            final String errorCode = errorNode.getString("customCode", "unknown");
            final String errorMessage = errorNode.getString("message", "Unknown error");

            throw new HieroException(
                "Contract verification failed with code: "
                    + errorCode
                    + ", message: "
                    + errorMessage);
          }

          final JsonObject contractNode = root.getJsonObject("contract");
          if (contractNode == null || contractNode.isNull("match")) {
            return ContractVerificationState.NONE;
          }

          return resolveVerificationState(contractNode.getString("match", null));
        }
      }

      throw new HieroException(
          "Timed out waiting for contract verification job: " + verificationId);
    } catch (Exception e) {
      throw new HieroException("Error checking contract verification status: " + verificationId, e);
    }
  }

  private @NonNull ContractVerificationState resolveVerificationState(
      final @Nullable String status) {
    if (status == null) {
      return ContractVerificationState.NONE;
    }

    return switch (status) {
      case "exact_match" -> ContractVerificationState.FULL;
      case "match" -> ContractVerificationState.PARTIAL;
      default -> ContractVerificationState.NONE;
    };
  }

  private @NonNull JsonObject parseJsonObject(final @NonNull String json) {
    Objects.requireNonNull(json, "json must not be null");
    try (JsonReader reader = jsonReaderFactory.createReader(new StringReader(json))) {
      return reader.readObject();
    }
  }
}
