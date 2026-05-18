package org.hiero.spring.test;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.hiero.base.HieroException;
import org.hiero.spring.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MirrorNodeRestClientImplTest {

  @Test
  void wrapsHttpErrorsInHieroException() {
    final RestClient.Builder restClientBuilder =
        RestClient.builder().baseUrl("https://mirror.example");
    final MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
    final MirrorNodeRestClientImpl client = new MirrorNodeRestClientImpl(restClientBuilder);

    server
        .expect(requestTo("https://mirror.example/api/v1/network/fees"))
        .andRespond(
            withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("upstream failed"));

    final HieroException exception =
        Assertions.assertThrows(
            HieroException.class, () -> client.doGetCall("/api/v1/network/fees"));

    Assertions.assertEquals("Mirror Node call failed", exception.getMessage());
    Assertions.assertNotNull(exception.getCause());
    Assertions.assertTrue(hasCauseMessageContaining(exception, "upstream failed"));
    server.verify();
  }

  private boolean hasCauseMessageContaining(final Throwable throwable, final String text) {
    Throwable current = throwable;
    while (current != null) {
      if (current.getMessage() != null && current.getMessage().contains(text)) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
