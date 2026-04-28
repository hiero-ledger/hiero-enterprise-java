package org.hiero.microprofile.implementation;

import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

public class RestBasedPage<T> implements Page<T> {

  private static final int HTTP_NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();

  private final WebTarget rootTarget;
  private final Function<JsonObject, List<T>> dataExtractionFunction;
  private final List<T> data;
  private final String rootPath;
  private final String currentPath;
  private final String nextPath;
  private final int number;

  public RestBasedPage(
      @NonNull WebTarget rootTarget,
      @NonNull Function<JsonObject, @NonNull List<T>> dataExtractionFunction,
      @NonNull String path) {
    this(rootTarget, dataExtractionFunction, path, path, 0);
  }

  public RestBasedPage(
      @NonNull WebTarget rootTarget,
      @NonNull Function<JsonObject, List<T>> dataExtractionFunction,
      @NonNull String path,
      @NonNull String rootPath,
      int number) {
    this.rootTarget = Objects.requireNonNull(rootTarget, "rootTarget must not be null");
    this.dataExtractionFunction =
        Objects.requireNonNull(dataExtractionFunction, "dataExtractionFunction must not be null");
    this.rootPath = Objects.requireNonNull(rootPath, "rootPath must not be null");
    this.currentPath = Objects.requireNonNull(path, "path must not be null");
    this.number = number;

    final WebTarget requestTarget = MirrorNodeRestClientImpl.buildTarget(rootTarget, path);
    try (final Response response = requestTarget.request(MediaType.APPLICATION_JSON).get()) {
      final int status = response.getStatus();
      if (status >= 400 && status != HTTP_NOT_FOUND) {
        throw new IllegalStateException(
            "Mirror node returned error " + status + " for path '" + path + "'");
      }
      final JsonObject jsonObject;
      if (status == HTTP_NOT_FOUND || !response.hasEntity()) {
        jsonObject = JsonObject.EMPTY_JSON_OBJECT;
      } else {
        jsonObject = response.readEntity(JsonObject.class);
      }
      this.data = Collections.unmodifiableList(dataExtractionFunction.apply(jsonObject));
      this.nextPath = getNextPath(jsonObject);
    } catch (final ProcessingException e) {
      throw new IllegalStateException("Error calling mirror node for path '" + path + "'", e);
    }
  }

  private static String getNextPath(final JsonObject jsonObject) {
    if (!jsonObject.containsKey("links")) {
      return null;
    }
    final JsonObject linksObject = jsonObject.getJsonObject("links");
    if (linksObject == null || !linksObject.containsKey("next")) {
      return null;
    }
    try {
      if (linksObject.isNull("next")) {
        return null;
      }
      return linksObject.getString("next");
    } catch (final Exception e) {
      throw new IllegalArgumentException("Error parsing next link '" + linksObject + "'", e);
    }
  }

  @Override
  public int getPageIndex() {
    return number;
  }

  @Override
  public int getSize() {
    return data.size();
  }

  @Override
  public List<T> getData() {
    return data;
  }

  @Override
  public boolean hasNext() {
    return nextPath != null;
  }

  @Override
  public Page<T> next() {
    if (nextPath == null) {
      throw new IllegalStateException("No next Page");
    }
    return new RestBasedPage<T>(rootTarget, dataExtractionFunction, nextPath, rootPath, number + 1);
  }

  @Override
  public Page<T> first() {
    return new RestBasedPage<T>(rootTarget, dataExtractionFunction, rootPath);
  }

  @Override
  public boolean isFirst() {
    return Objects.equals(rootPath, currentPath);
  }
}
