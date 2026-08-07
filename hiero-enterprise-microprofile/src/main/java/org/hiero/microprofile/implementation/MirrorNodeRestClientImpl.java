package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.jspecify.annotations.NonNull;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonObject> {

  private static final String NETWORK_API_PREFIX = "/api/v1/network";

  private final String target;

  private final String networkTarget;

  public MirrorNodeRestClientImpl(String target) {
    this(target, Optional.empty());
  }

  public MirrorNodeRestClientImpl(String target, Optional<String> mirrorNodeJavaRestBaseUrl) {
    Objects.requireNonNull(target, "target must not be null");
    Objects.requireNonNull(mirrorNodeJavaRestBaseUrl, "mirrorNodeJavaRestBaseUrl must not be null");
    this.target = target;
    this.networkTarget = mirrorNodeJavaRestBaseUrl.filter(s -> !s.isBlank()).orElse(null);
  }

  @Override
  public @NonNull JsonObject doGetCall(@NonNull String path) throws HieroException {
    Client client = ClientBuilder.newClient();
    Response response =
        client.target(resolveTarget(path)).path(path).request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 404 || response.getStatus() == 400 || !response.hasEntity()) {
      return JsonObject.EMPTY_JSON_OBJECT;
    }

    if (response.getStatus() >= 400) {
      throw new HieroException("Mirror Node call failed with status " + response.getStatus());
    }

    return response.readEntity(JsonObject.class);
  }

  private String resolveTarget(String path) {
    if (networkTarget != null && path.startsWith(NETWORK_API_PREFIX)) {
      return networkTarget;
    }
    return target;
  }

  public String getTarget() {
    return target;
  }
}
