package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.microprofile.implementation.MirrorNodeClientImpl;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MirrorNodeClientImplScheduleTest {

  private static final String MOCK_TARGET = "http://mock-target";
  private static final String SCHEDULES_PATH = "/api/v1/schedules";

  private final Client client = mock(Client.class);
  private final WebTarget webTarget = mock(WebTarget.class);
  private final Invocation.Builder requestBuilder = mock(Invocation.Builder.class);
  private final Response response = mock(Response.class);
  private MockedStatic<ClientBuilder> clientBuilderMock;

  @BeforeEach
  void setUp() {
    clientBuilderMock = mockStatic(ClientBuilder.class);
    clientBuilderMock.when(ClientBuilder::newClient).thenReturn(client);
    when(client.target(MOCK_TARGET)).thenReturn(webTarget);
    when(webTarget.path(anyString())).thenReturn(webTarget);
    when(webTarget.queryParam(anyString(), any())).thenReturn(webTarget);
    when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(requestBuilder);
    when(requestBuilder.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(200);
    when(response.hasEntity()).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    if (clientBuilderMock != null) {
      clientBuilderMock.close();
    }
  }

  @Test
  void querySchedulesFollowsNextLink() throws HieroException {
    when(response.readEntity(JsonObject.class))
        .thenReturn(
            loadJson("schedule-list.json"),
            Json.createObjectBuilder()
                .add("schedules", Json.createArrayBuilder().build())
                .add("links", Json.createObjectBuilder().add("next", JsonObject.NULL).build())
                .build());

    final Page<Schedule> page = newClient().querySchedules();

    assertEquals(
        List.of(
            ScheduleId.fromString("0.0.11295061"),
            ScheduleId.fromString("0.0.11296689"),
            ScheduleId.fromString("0.0.11297125")),
        page.getData().stream().map(Schedule::scheduleId).toList());
    verify(webTarget).path(SCHEDULES_PATH);
    verify(webTarget, never()).queryParam(anyString(), any());
    assertTrue(page.hasNext());

    final Page<Schedule> nextPage = page.next();

    assertEquals(1, nextPage.getPageIndex());
    assertTrue(nextPage.getData().isEmpty());
    assertFalse(nextPage.hasNext());
    verify(webTarget).queryParam("limit", "3");
    verify(webTarget).queryParam("schedule.id", "gt:0.0.11297125");
  }

  @Test
  void querySchedulesByAccountFiltersByAccountId() throws HieroException {
    when(response.readEntity(JsonObject.class)).thenReturn(loadJson("schedule-list.json"));

    final Page<Schedule> page =
        newClient().querySchedulesByAccount(AccountId.fromString("0.0.11212783"));

    assertEquals(3, page.getSize());
    assertEquals(AccountId.fromString("0.0.11212783"), page.getData().get(0).creatorAccountId());
    verify(webTarget).path(SCHEDULES_PATH);
    verify(webTarget).queryParam("account.id", "eq:0.0.11212783");
  }

  @Test
  void queryScheduleByIdReturnsSchedule() throws HieroException {
    when(response.readEntity(JsonObject.class)).thenReturn(loadJson("schedule.json"));

    final Optional<Schedule> schedule =
        newClient().queryScheduleById(ScheduleId.fromString("0.0.11296896"));

    assertTrue(schedule.isPresent());
    assertEquals(ScheduleId.fromString("0.0.11296896"), schedule.get().scheduleId());
    assertEquals(3, schedule.get().signatures().size());
    verify(webTarget).path(SCHEDULES_PATH + "/0.0.11296896");
  }

  @Test
  void queryScheduleByIdReturnsEmptyOptionalForUnknownSchedule() throws HieroException {
    when(response.getStatus()).thenReturn(404);

    final Optional<Schedule> schedule = newClient().queryScheduleById("0.0.1");

    assertTrue(schedule.isEmpty());
    verify(webTarget).path(SCHEDULES_PATH + "/0.0.1");
    verify(response, never()).readEntity(JsonObject.class);
  }

  @Test
  void rejectsNullParameters() {
    final MirrorNodeClientImpl mirrorNodeClient = newClient();
    assertThrows(
        NullPointerException.class,
        () -> mirrorNodeClient.querySchedulesByAccount((AccountId) null));
    assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.querySchedulesByAccount((String) null));
    assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.queryScheduleById((ScheduleId) null));
    assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.queryScheduleById((String) null));
    verify(client, never()).target(anyString());
  }

  private MirrorNodeClientImpl newClient() {
    return new MirrorNodeClientImpl(
        new MirrorNodeRestClientImpl(MOCK_TARGET), new MirrorNodeJsonConverterImpl());
  }

  private static JsonObject loadJson(final String filename) {
    final String path = "/json/" + filename;
    try (InputStream stream = MirrorNodeClientImplScheduleTest.class.getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Json fixture not found on classpath: " + path);
      }
      try (JsonReader reader = Json.createReader(stream)) {
        return reader.readObject();
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to read json fixture: " + path, e);
    }
  }
}
