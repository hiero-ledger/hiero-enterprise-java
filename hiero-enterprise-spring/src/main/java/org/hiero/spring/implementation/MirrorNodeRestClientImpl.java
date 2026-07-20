package org.hiero.spring.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonNode> {

  private final ObjectMapper objectMapper;

  private final RestClient restClient;

  public MirrorNodeRestClientImpl(final RestClient.Builder restClientBuilder) {
    Objects.requireNonNull(restClientBuilder, "restClientBuilder must not be null");
    objectMapper = new ObjectMapper();
    restClient = restClientBuilder.build();
  }

  public JsonNode doGetCall(String path) throws HieroException {
    return doGetCall(builder -> builder.path(path).build());
  }

  public JsonNode doGetCall(Function<UriBuilder, URI> uriFunction) throws HieroException {
    final ResponseEntity<String> responseEntity;
    try {
      responseEntity =
          restClient
              .get()
              .uri(uriBuilder -> uriFunction.apply(uriBuilder))
              .accept(MediaType.APPLICATION_JSON)
              .retrieve()
              .onStatus(
                  HttpStatusCode::is4xxClientError,
                  (request, response) -> {
                    if (!HttpStatus.NOT_FOUND.equals(response.getStatusCode())
                        && !HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) {
                      handleError(request, response);
                    }
                  })
              .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
              .toEntity(String.class);
    } catch (RestClientException e) {
      throw new HieroException("Mirror Node call failed", e);
    }
    final String body = responseEntity.getBody();
    try {
      if (HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode())
          || HttpStatus.BAD_REQUEST.equals(responseEntity.getStatusCode())) {
        return objectMapper.readTree("{}");
      }
      if (body == null || body.isBlank()) {
        return objectMapper.readTree("{}");
      }
      return objectMapper.readTree(body);
    } catch (JsonProcessingException e) {
      throw new HieroException("Error parsing body as JSON: " + body, e);
    }
  }

  private void handleError(final HttpRequest request, final ClientHttpResponse response)
      throws IOException {
    final String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
    throw new IOException(
        "Mirror Node call failed with status "
            + response.getStatusCode()
            + " for request '"
            + request.getURI()
            + "': "
            + body);
  }
}
