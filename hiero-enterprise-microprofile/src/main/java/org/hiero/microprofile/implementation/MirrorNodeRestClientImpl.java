package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
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
    Response response = client.target(target).path(path).request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 404 || response.getStatus() == 400 || !response.hasEntity()) {
      return JsonObject.EMPTY_JSON_OBJECT;
    }

    if (response.getStatus() >= 400) {
      throw new HieroException("Mirror Node call failed with status " + response.getStatus());
    }

    return response.readEntity(JsonObject.class);
  }

  @Override
  public @NonNull JsonObject doGetCall(
      @NonNull String path, @NonNull Map<String, List<String>> queryParams) throws HieroException {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(this.target).path(path);
    for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
      for (String value : entry.getValue()) {
        target = target.queryParam(entry.getKey(), value);
      }
    }
    Response response = target.request(MediaType.APPLICATION_JSON).get();

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
