package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.jspecify.annotations.NonNull;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonObject>, AutoCloseable {

  private final Client client;

  private final String target;

  public MirrorNodeRestClientImpl(@NonNull final String target) {
    Objects.requireNonNull(target, "target must not be null");
    this.client = ClientBuilder.newClient();
    this.target = target;
  }

  @Override
  public @NonNull JsonObject doGetCall(@NonNull final String path) throws HieroException {
    Objects.requireNonNull(path, "path must not be null");
    try (final Response response =
        client.target(target).path(path).request(MediaType.APPLICATION_JSON).get()) {
      final int status = response.getStatus();
      if (status == 404) {
        return JsonObject.EMPTY_JSON_OBJECT;
      }
      if (status >= 400) {
        throw new HieroException(
            "Mirror node returned error " + status + " for path '" + path + "'");
      }
      if (!response.hasEntity()) {
        return JsonObject.EMPTY_JSON_OBJECT;
      }
      return response.readEntity(JsonObject.class);
    } catch (final ProcessingException e) {
      throw new HieroException("Error calling mirror node for path '" + path + "'", e);
    }
  }

  public String getTarget() {
    return target;
  }

  /**
   * Returns the shared JAX-RS client so pagination can reuse the same connection pool instead of
   * allocating a new client per page.
   */
  @NonNull
  public Client getClient() {
    return client;
  }

  @Override
  public void close() {
    client.close();
  }
}
