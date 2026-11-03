package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.spring.implementation.HieroAutoConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClient;

class HieroAutoConfigurationTest {

  private final List<URI> interceptedRequests = new ArrayList<>();

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(HieroAutoConfiguration.class))
          .withPropertyValues(
              "spring.hiero.accountId=0.0.1001",
              "spring.hiero.privateKey=" + PrivateKey.generateED25519(),
              "spring.hiero.network.name=hedera-testnet",
              "spring.hiero.network.mirror-node-java-rest=http://localhost:8084");

  @Test
  void mirrorNodeClientAppliesRestClientCustomizers() {
    customizedContextRunner()
        .run(
            context -> {
              context
                  .getBean(MirrorNodeClient.class)
                  .queryAccount(AccountId.fromString("0.0.1001"));

              Assertions.assertEquals(
                  List.of(
                      URI.create(
                          "https://testnet.mirrornode.hedera.com:443/api/v1/accounts/0.0.1001")),
                  interceptedRequests);
            });
  }

  @Test
  void mirrorNodeJavaRestClientAppliesRestClientCustomizers() {
    customizedContextRunner()
        .run(
            context -> {
              context.getBean(MirrorNodeClient.class).queryExchangeRates();

              Assertions.assertEquals(
                  List.of(URI.create("http://localhost:8084/api/v1/network/exchangerate")),
                  interceptedRequests);
            });
  }

  @Test
  void contractVerificationClientAppliesRestClientCustomizers() {
    final ContractId contractId = ContractId.fromString("0.0.1001");
    customizedContextRunner()
        .run(
            context -> {
              context.getBean(ContractVerificationClient.class).checkVerification(contractId);

              Assertions.assertEquals(
                  List.of(
                      URI.create(
                          "https://sourcify.dev/server/v2/contract/296/0x"
                              + contractId.toEvmAddress())),
                  interceptedRequests);
            });
  }

  @Test
  void createsClientsWithoutRestClientAutoConfiguration() {
    contextRunner.run(
        context -> {
          Assertions.assertNull(context.getStartupFailure());
          Assertions.assertNotNull(context.getBean(MirrorNodeClient.class));
          Assertions.assertNotNull(context.getBean(ContractVerificationClient.class));
        });
  }

  private WebApplicationContextRunner customizedContextRunner() {
    return contextRunner
        .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
        .withBean(RestClientCustomizer.class, () -> this::addRecordingInterceptor);
  }

  private void addRecordingInterceptor(final RestClient.Builder builder) {
    builder.requestInterceptor(
        (request, body, execution) -> {
          interceptedRequests.add(request.getURI());
          final MockClientHttpResponse response =
              new MockClientHttpResponse("{}".getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
          response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
          return response;
        });
  }
}
