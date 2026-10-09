package org.hiero.microprofile.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationState;
import org.hiero.microprofile.implementation.ContractVerificationClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ContractVerificationClientImplTest {
  private final String BASE_URL = "https://sourcify.dev/server/v2";
  private final long CHAIN_ID = 259L;

  private HieroConfig hieroConfig;
  private Client webclient;

  private ContractVerificationClientImpl client;

  @BeforeEach
  void setup() {
    hieroConfig = Mockito.mock(HieroConfig.class);
    webclient = Mockito.mock(Client.class);
    when(hieroConfig.chainId()).thenReturn(Optional.of(CHAIN_ID));

    client = new ContractVerificationClientImpl(hieroConfig, webclient);
  }

  // Test Constructor

  @Test
  void testConstructorThrowsExceptionForNullParams() {
    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                NullPointerException.class, () -> new ContractVerificationClientImpl(null)),
        () ->
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new ContractVerificationClientImpl(null, webclient)),
        () ->
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new ContractVerificationClientImpl(hieroConfig, null)));
  }

  // Test verify Contract

  @Test
  void testVerifyThrowsExceptionWhenContractIdNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> client.verify(null, "Hello", Map.of()));
  }

  @Test
  void testVerifyThrowsExceptionWhenContractNameNull() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    Assertions.assertThrows(
        NullPointerException.class, () -> client.verify(contractId, null, Map.of()));
  }

  @Test
  void testVerifyThrowsExceptionWhenFilesNull() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    Assertions.assertThrows(
        NullPointerException.class, () -> client.verify(contractId, "Hello", null));
  }

  @Test
  void testVerifyThrowsExceptionMissingMetadata() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Map<String, String> files = Map.of("Hello.sol", "contract Hello {}");
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.verify(contractId, "Hello", files));
  }

  @Test
  void testVerifyThrowsExceptionEmptyMetadata() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Map<String, String> files = Map.of("Hello.sol", "contract Hello {}", "metadata.json", "");

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.verify(contractId, "Hello", files));
  }

  @Test
  void testVerifyThrowsExceptionNullMetadata() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Map<String, String> files = new HashMap<>();
    files.put("metadata.json", null);

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.verify(contractId, "Hello", files));
  }

  @Test
  void testVerifyThrowsExceptionInvalidMetadata() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Map<String, String> files =
        Map.of("Hello.sol", "contract Hello {}", "metadata.json", "no-json-metadata");

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.verify(contractId, "Hello", files));
  }

  @Test
  void testVerifyThrowsExceptionIfContractAlreadyVerified() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String metadata =
        """
        {
          "language": "Solidity",
          "settings": {}
        }
        """;
    final Map<String, String> files =
        Map.of("Hello.sol", "contract Hello {}", "metadata.json", metadata);

    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": "exact_match"
        }
      """);

    Assertions.assertThrows(
        IllegalStateException.class, () -> client.verify(contractId, "Hello", files));

    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testVerifyPollsUntilJobCompletes() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String metadata =
        """
        {
          "language": "Solidity",
          "settings": {}
        }
        """;

    final Map<String, String> files =
        Map.of("metadata.json", metadata, "Hello.sol", "contract Hello {}");

    final Mocks mocks1 =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": null
        }
      """);

    final Mocks mocks2 =
        mockPost(
            verifyMetadataUrl(contractId),
            Response.Status.OK,
            """
        {
          "verificationId": "verification-123"
        }
      """);

    // job still running
    mockGet(
        verificationJobUrl("verification-123"),
        Response.Status.OK,
        """
        {
          "isJobCompleted": false
        }
      """);

    final Mocks mocks3 =
        mockGet(
            verificationJobUrl("verification-123"),
            Response.Status.OK,
            """
        {
          "isJobCompleted": true,
          "contract": {
            "match": "exact_match"
          }
        }
      """);

    final ContractVerificationState result = client.verify(contractId, "Hello", files);

    Assertions.assertEquals(ContractVerificationState.FULL, result);
    verifyMocks(mocks1, contractUrl(contractId), "get");
    verifyMocks(mocks2, verifyMetadataUrl(contractId), "post");
    verifyMocks(mocks3, verificationJobUrl("verification-123"), "get");
  }

  @Test
  void verifyThrowsWhenVerificationJobFails() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String metadata =
        """
        {
          "language": "Solidity",
          "settings": {}
        }
        """;

    final Map<String, String> files =
        Map.of("metadata.json", metadata, "Hello.sol", "contract Hello {}");

    final Mocks mocks1 =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": null
        }
      """);

    final Mocks mocks2 =
        mockPost(
            verifyMetadataUrl(contractId),
            Response.Status.OK,
            """
        {
          "verificationId": "verification-123"
        }
      """);

    // Poll response
    final Mocks mocks3 =
        mockGet(
            verificationJobUrl("verification-123"),
            Response.Status.OK,
            """
        {
          "isJobCompleted": true,
          "error": {
            "customCode": "INVALID_METADATA",
            "message": "Metadata is invalid"
          }
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.verify(contractId, "Hello", files));
    verifyMocks(mocks1, contractUrl(contractId), "get");
    verifyMocks(mocks2, verifyMetadataUrl(contractId), "post");
    verifyMocks(mocks3, verificationJobUrl("verification-123"), "get");
  }

  // Test Check Verification State

  @Test
  void testCheckVerificationThrowsExceptionNullContractId() {
    Assertions.assertThrows(NullPointerException.class, () -> client.checkVerification(null));
  }

  @Test
  void testCheckVerificationReturnsFullForExactMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": "exact_match"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.FULL, result);
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationReturnsPartialForMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": "match"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.PARTIAL, result);
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationReturnsNoneForNullMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": null
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationReturnsNoneForUnknownMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.OK,
            """
        {
          "match": "unknown"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationReturnsNoneForNotFound() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.NOT_FOUND,
            """
       {
          "match": null
       }
    """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationFor4xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.BAD_REQUEST,
            """
        {
           "customCode": "unsupported_chain",
           "message": "The chain with chainId 9429413 is not supported",
           "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.checkVerification(contractId));
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  @Test
  void testCheckVerificationFor5xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrl(contractId),
            Response.Status.INTERNAL_SERVER_ERROR,
            """
        {
          "customCode": "internal_error",
          "message": "Something went wrong",
          "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.checkVerification(contractId));
    verifyMocks(mocks, contractUrl(contractId), "get");
  }

  // Test Check Verification with File Content

  @Test
  void testCheckFileContentThrowsExceptionNullContractId() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> client.checkVerification(null, "Hello.sol", "contract Hello {}"));
  }

  @Test
  void testCheckFileContentThrowsExceptionNullFileName() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    Assertions.assertThrows(
        NullPointerException.class,
        () -> client.checkVerification(contractId, null, "contract Hello {}"));
  }

  @Test
  void testCheckFileContentThrowsExceptionNullFileContent() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    Assertions.assertThrows(
        NullPointerException.class, () -> client.checkVerification(contractId, "Hello.sol", null));
  }

  @Test
  void testCheckFileContentExceptionIfContractIsNotVerified() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Mocks mocks =
        mockGet(
            contractUrlWithFiles(contractId),
            Response.Status.OK,
            """
        {
          "match": null
        }
        """);
    Assertions.assertThrows(
        HieroException.class,
        () -> client.checkVerification(contractId, "Hello.sol", "contract Hello {}"));
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  @Test
  void testCheckFileContentReturnFalseForBlankResponse() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Mocks mocks = mockGet(contractUrlWithFiles(contractId), Response.Status.OK, "");
    final boolean result = client.checkVerification(contractId, "Hello.sol", "contract Hello {}");
    Assertions.assertFalse(result);
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  @Test
  void testCheckFileContentReturnTrueIfContentMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Mocks mocks =
        mockGet(
            contractUrlWithFiles(contractId),
            Response.Status.OK,
            """
       {
        "match": "exact_match",
        "sources": {
          "contracts/Hello.sol": {"content": "contract Hello {}"}
        }
       }
    """);
    final boolean result = client.checkVerification(contractId, "Hello.sol", "contract Hello {}");
    Assertions.assertTrue(result);
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  @Test
  void testCheckFileContentReturnFalseIfContentUnMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final Mocks mocks =
        mockGet(
            contractUrlWithFiles(contractId),
            Response.Status.OK,
            """
       {
        "match": "exact_match",
        "sources": {
          "contracts/Hello.sol": {"content": "contract Hello {}"}
        }
       }
    """);
    final boolean result =
        client.checkVerification(contractId, "Hello.sol", "contract MyContract {}");
    Assertions.assertFalse(result);
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  @Test
  void testCheckFileContentFor4xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrlWithFiles(contractId),
            Response.Status.BAD_REQUEST,
            """
        {
           "customCode": "unsupported_chain",
           "message": "The chain with chainId 9429413 is not supported",
           "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(
        HieroException.class,
        () -> client.checkVerification(contractId, "Hello.sol", "contract Hello {}"));
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  @Test
  void testCheckFileContentFor5xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    final Mocks mocks =
        mockGet(
            contractUrlWithFiles(contractId),
            Response.Status.INTERNAL_SERVER_ERROR,
            """
        {
          "customCode": "internal_error",
          "message": "Something went wrong",
          "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(
        HieroException.class,
        () -> client.checkVerification(contractId, "Hello.sol", "contract Hello {}"));
    verifyMocks(mocks, contractUrlWithFiles(contractId), "get");
  }

  // Helpers

  private String contractUrl(final ContractId contractId) {
    return BASE_URL + "/contract/" + CHAIN_ID + "/0x" + contractId.toEvmAddress();
  }

  private String contractUrlWithFiles(final ContractId contractId) {
    return BASE_URL
        + "/contract/"
        + CHAIN_ID
        + "/0x"
        + contractId.toEvmAddress()
        + "?fields=sources";
  }

  private String verifyMetadataUrl(final ContractId contractId) {
    return BASE_URL + "/verify/metadata/" + CHAIN_ID + "/0x" + contractId.toEvmAddress();
  }

  private String verificationJobUrl(final String verificationId) {
    return BASE_URL + "/verify/" + verificationId;
  }

  private record Mocks(WebTarget target, Invocation.Builder request, Response response) {}

  private Mocks mockGet(final String url, final Response.Status status, final String responseBody) {
    final WebTarget target = mock(WebTarget.class);
    final Invocation.Builder request = mock(Invocation.Builder.class);
    final Response response = mock(Response.class);

    when(webclient.target(url)).thenReturn(target);
    when(target.request(MediaType.APPLICATION_JSON)).thenReturn(request);
    when(request.get()).thenReturn(response);
    when(response.getStatusInfo()).thenReturn(status);
    when(response.readEntity(String.class)).thenReturn(responseBody);

    return new Mocks(target, request, response);
  }

  private Mocks mockPost(
      final String url, final Response.Status status, final String responseBody) {
    final WebTarget target = mock(WebTarget.class);
    final Invocation.Builder request = mock(Invocation.Builder.class);
    final Response response = mock(Response.class);

    when(webclient.target(url)).thenReturn(target);
    when(target.request(MediaType.APPLICATION_JSON)).thenReturn(request);
    when(request.post(any(Entity.class))).thenReturn(response);
    when(response.getStatusInfo()).thenReturn(status);
    when(response.readEntity(String.class)).thenReturn(responseBody);

    return new Mocks(target, request, response);
  }

  private void verifyMocks(
      final Mocks mocks, final String expectedUrl, final String expectedMethod) {

    verify(webclient).target(expectedUrl);
    verify(mocks.target()).request(MediaType.APPLICATION_JSON);

    // response.getStatusInfo() may be called multiple times.
    // response.readEntity(String.class) may be skipped when the contract
    // verification status is not found.

    switch (expectedMethod.toUpperCase()) {
      case "POST" -> verify(mocks.request()).post(any(Entity.class));
      case "GET" -> verify(mocks.request()).get();
      default -> throw new IllegalArgumentException("Unsupported HTTP method: " + expectedMethod);
    }
  }
}
