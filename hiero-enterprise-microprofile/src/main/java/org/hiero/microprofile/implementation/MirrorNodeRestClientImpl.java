package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.MirrorNodeRestClient;
import org.jspecify.annotations.NonNull;

public class MirrorNodeRestClientImpl implements MirrorNodeRestClient<JsonObject> {

  private final String target;

  public MirrorNodeRestClientImpl(String target) {
    this.target = target;
  }

  @Override
  public @NonNull JsonObject doGetCall(@NonNull String path) throws HieroException {
    Client client = ClientBuilder.newClient();
    final int queryIndex = path.indexOf('?');
    final WebTarget webTarget;
    if (queryIndex < 0) {
      webTarget = client.target(target).path(path);
    } else {
      WebTarget t = client.target(target).path(path.substring(0, queryIndex));
      for (final String param : path.substring(queryIndex + 1).split("&")) {
        if (param.isEmpty()) {
          continue;
        }
        final int eq = param.indexOf('=');
        if (eq < 0) {
          t = t.queryParam(param, "");
        } else {
          t = t.queryParam(param.substring(0, eq), param.substring(eq + 1));
        }
      }
      webTarget = t;
    }
    Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 404 || response.getStatus() == 400 || !response.hasEntity()) {
      return JsonObject.EMPTY_JSON_OBJECT;
    }

    if (response.getStatus() >= 400) {
      throw new HieroException("Mirror Node call failed with status " + response.getStatus());
    }

    return response.readEntity(JsonObject.class);
  }

  public String getTarget() {
    return target;
  }
}
