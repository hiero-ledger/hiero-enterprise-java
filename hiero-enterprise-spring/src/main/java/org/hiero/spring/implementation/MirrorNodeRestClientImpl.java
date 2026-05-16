package org.hiero.spring.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonNode> {

  private static final String NETWORK_API_PREFIX = "/api/v1/network";

  private final ObjectMapper objectMapper;

  private final RestClient restClient;

  private final RestClient networkRestClient;

  public MirrorNodeRestClientImpl(final RestClient.Builder restClientBuilder) {
    this(restClientBuilder, Optional.empty());
  }

  public MirrorNodeRestClientImpl(
      final RestClient.Builder restClientBuilder,
      final Optional<String> mirrorNodeJavaRestBaseUrl) {
    Objects.requireNonNull(restClientBuilder, "restClientBuilder must not be null");
    Objects.requireNonNull(mirrorNodeJavaRestBaseUrl, "mirrorNodeJavaRestBaseUrl must not be null");
    objectMapper = new ObjectMapper();
    restClient = restClientBuilder.build();
    networkRestClient =
        mirrorNodeJavaRestBaseUrl
            .filter(s -> !s.isBlank())
            .map(base -> RestClient.builder().baseUrl(base).build())
            .orElse(null);
  }

  public JsonNode doGetCall(String path) throws HieroException {
    return doGetCall(resolveClient(path), builder -> builder.path(path).build());
  }

  public JsonNode doGetCall(Function<UriBuilder, URI> uriFunction) throws HieroException {
    return doGetCall(restClient, uriFunction);
  }

  private RestClient resolveClient(String path) {
    if (networkRestClient != null && path.startsWith(NETWORK_API_PREFIX)) {
      return networkRestClient;
    }
    return restClient;
  }

  private JsonNode doGetCall(RestClient client, Function<UriBuilder, URI> uriFunction)
      throws HieroException {
    final ResponseEntity<String> responseEntity =
        client
            .get()
            .uri(uriBuilder -> uriFunction.apply(uriBuilder))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                (request, response) -> {
                  if (!HttpStatus.NOT_FOUND.equals(response.getStatusCode())
                      && !HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) {
                    throw new RuntimeException("Client error: " + response.getStatusText());
                  }
                })
            .onStatus(
                HttpStatusCode::is5xxServerError,
                (request, response) -> {
                  throw new RuntimeException("Server error: " + response.getStatusText());
                })
            .toEntity(String.class);
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
}
