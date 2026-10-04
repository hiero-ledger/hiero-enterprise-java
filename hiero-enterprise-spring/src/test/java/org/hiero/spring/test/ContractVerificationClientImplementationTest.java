package org.hiero.spring.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationState;
import org.hiero.spring.implementation.ContractVerificationClientImplementation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

public class ContractVerificationClientImplementationTest {
  private final String BASE_URL = "https://sourcify.dev/server/v2";
  private final long CHAIN_ID = 259L;

  private MockRestServiceServer server;
  private ContractVerificationClientImplementation client;

  @BeforeEach
  void setUp() {
    final RestClient.Builder builder = RestClient.builder();
    final HieroConfig config = mock(HieroConfig.class);
    when(config.chainId()).thenReturn(Optional.of(CHAIN_ID));

    server = MockRestServiceServer.bindTo(builder).build();
    client = new ContractVerificationClientImplementation(config, builder);
  }

  // Test Constructor

  @Test
  void testConstructorThrowsExceptionForNullParams() {
    final HieroConfig config = mock(HieroConfig.class);
    final RestClient.Builder builder = RestClient.builder();

    Assertions.assertAll(
        () ->
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new ContractVerificationClientImplementation(null)),
        () ->
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new ContractVerificationClientImplementation(null, builder)),
        () ->
            Assertions.assertThrows(
                NullPointerException.class,
                () -> new ContractVerificationClientImplementation(config, null)));
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

    expectGet(
        contractUrl(contractId),
        """
        {
          "match": null
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.verify(contractId, "Hello", files));
    server.verify();
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

    expectGet(
        contractUrl(contractId),
        """
        {
          "match": "exact_match"
        }
      """);

    Assertions.assertThrows(
        IllegalStateException.class, () -> client.verify(contractId, "Hello", files));
    server.verify();
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

    expectGet(
        contractUrl(contractId),
        """
        {
          "match": null
        }
      """);

    expectPost(
        verifyMetadataUrl(contractId),
        """
        {
          "verificationId": "verification-123"
        }
      """);

    expectGet(
        verificationJobUrl("verification-123"),
        """
        {
          "isJobCompleted": false
        }
      """);

    expectGet(
        verificationJobUrl("verification-123"),
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
    server.verify();
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

    expectGet(
        contractUrl(contractId),
        """
        {
          "match": null
        }
      """);

    expectPost(
        verifyMetadataUrl(contractId),
        """
        {
          "verificationId": "verification-123"
        }
      """);

    // Poll response
    expectGet(
        verificationJobUrl("verification-123"),
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
    server.verify();
  }

  // Test Check Verification State

  @Test
  void testCheckVerificationThrowsExceptionNullContractId() {
    Assertions.assertThrows(NullPointerException.class, () -> client.checkVerification(null));
  }

  @Test
  void testCheckVerificationReturnsFullForExactMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        """
        {
          "match": "exact_match"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.FULL, result);
    server.verify();
  }

  @Test
  void testCheckVerificationReturnsPartialForMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        """
        {
          "match": "match"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.PARTIAL, result);
    server.verify();
  }

  @Test
  void testCheckVerificationReturnsNoneForNullMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        """
        {
          "match": null
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    server.verify();
  }

  @Test
  void testCheckVerificationReturnsNoneForUnknownMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        """
        {
          "match": "unknown"
        }
      """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    server.verify();
  }

  @Test
  void testCheckVerificationReturnsNoneForNotFound() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        HttpStatus.NOT_FOUND,
        """
       {
          "match": null,
          "creationMatch": null,
          "runtimeMatch": null
       }
    """);

    final ContractVerificationState result = client.checkVerification(contractId);

    Assertions.assertEquals(ContractVerificationState.NONE, result);
    server.verify();
  }

  @Test
  void testCheckVerificationFor4xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        HttpStatus.BAD_REQUEST,
        """
        {
           "customCode": "unsupported_chain",
           "message": "The chain with chainId 9429413 is not supported",
           "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.checkVerification(contractId));
    server.verify();
  }

  @Test
  void testCheckVerificationFor5xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrl(contractId),
        HttpStatus.INTERNAL_SERVER_ERROR,
        """
        {
          "customCode": "internal_error",
          "message": "Something went wrong",
          "errorId": "1ac6b91a-0605-4459-93dc-18f210a70192"
        }
      """);

    Assertions.assertThrows(HieroException.class, () -> client.checkVerification(contractId));
    server.verify();
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
    expectGet(
        contractUrlWithFiles(contractId),
        """
        {
          "match": null
        }
        """);
    Assertions.assertThrows(
        HieroException.class,
        () -> client.checkVerification(contractId, "Hello.sol", "contract Hello {}"));
    server.verify();
  }

  @Test
  void testCheckFileContentReturnFalseForBlankResponse() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    expectGet(contractUrlWithFiles(contractId), "");
    final boolean result = client.checkVerification(contractId, "Hello.sol", "contract Hello {}");
    Assertions.assertFalse(result);
    server.verify();
  }

  @Test
  void testCheckFileContentReturnTrueIfContentMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    expectGet(
        contractUrlWithFiles(contractId),
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
    server.verify();
  }

  @Test
  void testCheckFileContentReturnFalseIfContentUnMatch() throws HieroException {
    final ContractId contractId = ContractId.fromString("0.0.101");
    expectGet(
        contractUrlWithFiles(contractId),
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
    server.verify();
  }

  @Test
  void testCheckFileContentFor4xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrlWithFiles(contractId),
        HttpStatus.BAD_REQUEST,
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
    server.verify();
  }

  @Test
  void testCheckFileContentFor5xxResponse() {
    final ContractId contractId = ContractId.fromString("0.0.123");
    expectGet(
        contractUrlWithFiles(contractId),
        HttpStatus.INTERNAL_SERVER_ERROR,
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
    server.verify();
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

  private void expectGet(final String expectedUrl, final String response) {
    expectGet(expectedUrl, HttpStatus.OK, response);
  }

  private void expectGet(final String expectedUrl, final HttpStatus status, final String response) {
    server
        .expect(requestTo(expectedUrl))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(status).contentType(MediaType.APPLICATION_JSON).body(response));
  }

  private void expectPost(final String expectedUrl, final String response) {
    expectPost(expectedUrl, HttpStatus.OK, response);
  }

  private void expectPost(
      final String expectedUrl, final HttpStatus status, final String response) {
    server
        .expect(requestTo(expectedUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(status).contentType(MediaType.APPLICATION_JSON).body(response));
  }
}
