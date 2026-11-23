package org.hiero.spring.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hiero.base.config.HieroConfig;
import org.hiero.spring.implementation.ContractVerificationClientImplementation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

class ContractVerificationErrorHandlingTest {

  @Test
  void preservesBodyReadFailureCause() throws Exception {
    final ContractVerificationClientImplementation client =
        new ContractVerificationClientImplementation(Mockito.mock(HieroConfig.class));
    final Method handleError =
        ContractVerificationClientImplementation.class.getDeclaredMethod(
            "handleError", HttpRequest.class, ClientHttpResponse.class);
    handleError.setAccessible(true);
    final IOException readFailure = new IOException("stream closed");

    final InvocationTargetException exception =
        Assertions.assertThrows(
            InvocationTargetException.class,
            () ->
                handleError.invoke(
                    client, Mockito.mock(HttpRequest.class), failingResponse(readFailure)));

    final Throwable cause = exception.getCause();
    Assertions.assertInstanceOf(IOException.class, cause);
    Assertions.assertSame(readFailure, cause.getCause());
  }

  private ClientHttpResponse failingResponse(final IOException readFailure) {
    return new ClientHttpResponse() {
      @Override
      public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_GATEWAY;
      }

      @Override
      public String getStatusText() {
        return "Bad Gateway";
      }

      @Override
      public void close() {}

      @Override
      public InputStream getBody() throws IOException {
        throw readFailure;
      }

      @Override
      public HttpHeaders getHeaders() {
        return HttpHeaders.EMPTY;
      }
    };
  }
}
