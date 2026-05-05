package org.hiero.microprofile.implementation;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.jspecify.annotations.NonNull;

public class ContractVerificationClientImpl implements ContractVerificationClient {

  private static final String CONTRACT_VERIFICATION_URL = "https://server-verify.hashscan.io";

  private final HieroConfig hieroConfig;

  private final JsonParserFactory jsonParserFactory;

  private final Client webClient;

  public ContractVerificationClientImpl(@NonNull final HieroConfig hieroConfig) {
    this.hieroConfig = Objects.requireNonNull(hieroConfig, "hieroConfig must not be null");
    jsonParserFactory = Json.createParserFactory(Map.of());
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

  private ContractVerificationState parseStatus(@NonNull final String status) {
    if (status.equals("perfect")) {
      return ContractVerificationState.FULL;
    } else if (status.equals("false")) {
      return ContractVerificationState.NONE;
    } else {
      throw new RuntimeException("Unknown status: " + status);
    }
  }

  @NonNull
  @Override
  public ContractVerificationState checkVerification(@NonNull final ContractId contractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");

    final String uri =
        CONTRACT_VERIFICATION_URL
            + "/check-by-addresses"
            + "?addresses="
            + contractId.toSolidityAddress()
            + "&chainIds="
            + getChainId();

    try {
      final Response response =
          webClient.target(uri).request(MediaType.APPLICATION_JSON).get();
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }
      final String resultBody = response.readEntity(String.class);
      final JsonParser parser = jsonParserFactory.createParser(new StringReader(resultBody));
      final JsonArray root = parser.getArray();
      final List<JsonObject> results =
          root.stream()
              .filter(v -> v.getValueType() == JsonValue.ValueType.OBJECT)
              .map(JsonValue::asJsonObject)
              .toList();
      if (results.size() != 1) {
        throw new RuntimeException("Expected exactly one result, got " + results.size());
      }
      final String status = results.get(0).getString("status");
      return parseStatus(status);
    } catch (final Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }

  @Override
  public boolean checkVerification(
      @NonNull final ContractId contractId,
      @NonNull final String fileName,
      @NonNull final String fileContent)
      throws HieroException {
    final ContractVerificationState state = checkVerification(contractId);
    if (state != ContractVerificationState.FULL) {
      throw new IllegalStateException("Contract is not verified");
    }

    final String uri =
        CONTRACT_VERIFICATION_URL + "/files/" + getChainId() + "/" + contractId.toSolidityAddress();

    try {
      final Response response =
          webClient.target(uri).request().header("accept", "application/json").get();
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }
      final String resultBody = response.readEntity(String.class);
      final JsonParser parser = jsonParserFactory.createParser(new StringReader(resultBody));
      final JsonArray root = parser.getArray();
      final List<JsonObject> results =
          root.stream()
              .filter(v -> v.getValueType() == JsonValue.ValueType.OBJECT)
              .map(JsonValue::asJsonObject)
              .filter(obj -> obj.getString("name").equals(fileName))
              .toList();
      if (results.size() != 1) {
        throw new RuntimeException("Expected exactly one result, got " + results.size());
      }
      final String content = results.get(0).getString("content");
      return Objects.equals(content, fileContent);
    } catch (final Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }

  @NonNull
  @Override
  public ContractVerificationState verify(
      @NonNull final ContractId contractId,
      @NonNull final String contractName,
      @NonNull final Map<String, String> files)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(contractName, "contractName must not be null");
    Objects.requireNonNull(files, "files must not be null");

    final ContractVerificationState state = checkVerification(contractId);
    if (state != ContractVerificationState.NONE) {
      throw new IllegalStateException("Contract is already verified");
    }

    final JsonObject requestBody = Json.createObjectBuilder()
        .add("address", contractId.toSolidityAddress())
        .add("chain", getChainId())
        .add("creatorTxHash", "")
        .add("chosenContract", "")
        .add("files", Json.createObjectBuilder(files).build())
        .build();

    try {
      final Response response =
          webClient
              .target(CONTRACT_VERIFICATION_URL + "/verify")
              .request(MediaType.APPLICATION_JSON)
              .post(Entity.json(requestBody.toString()));
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        handleError(response);
      }
      final String resultBody = response.readEntity(String.class);
      final JsonParser parser = jsonParserFactory.createParser(new StringReader(resultBody));
      final JsonObject root = parser.getObject();
      final JsonArray resultArray = root.getJsonArray("result");
      if (resultArray == null || resultArray.size() != 1) {
        throw new RuntimeException("Expected exactly one result");
      }
      final String status = resultArray.getJsonObject(0).getString("status");
      return parseStatus(status);
    } catch (final Exception e) {
      throw new HieroException("Error verification step", e);
    }
  }
}
