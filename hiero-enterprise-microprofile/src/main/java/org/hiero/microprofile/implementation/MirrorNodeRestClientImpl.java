package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.jspecify.annotations.NonNull;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonObject>, AutoCloseable {

  private static final int HTTP_NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();

  private final Client client;

  private final WebTarget rootTarget;

  public MirrorNodeRestClientImpl(@NonNull final String target) {
    Objects.requireNonNull(target, "target must not be null");
    this.client = ClientBuilder.newClient();
    this.rootTarget = client.target(target);
  }

  @Override
  public @NonNull JsonObject doGetCall(@NonNull final String path) throws HieroException {
    Objects.requireNonNull(path, "path must not be null");
    final WebTarget requestTarget = buildTarget(rootTarget, path);
    try (final Response response = requestTarget.request(MediaType.APPLICATION_JSON).get()) {
      final int status = response.getStatus();
      if (status == HTTP_NOT_FOUND) {
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

  @NonNull
  public WebTarget getRootTarget() {
    return rootTarget;
  }

  @Override
  public void close() {
    client.close();
  }

  /**
   * Builds a {@link WebTarget} for the given path, parsing any query string that follows a single
   * {@code ?} into {@link WebTarget#queryParam(String, Object...)} calls.
   */
  static WebTarget buildTarget(@NonNull final WebTarget base, @NonNull final String path) {
    final String[] parts = path.split("\\?", 2);
    WebTarget target = base.path(parts[0]);
    if (parts.length == 2 && !parts[1].isEmpty()) {
      for (final String param : parts[1].split("&")) {
        if (param.isEmpty()) {
          continue;
        }
        final String[] kv = param.split("=", 2);
        final String value = kv.length == 2 ? kv[1] : "";
        target = target.queryParam(kv[0], value);
      }
    }
    return target;
  }
}
