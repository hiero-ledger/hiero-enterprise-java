package org.hiero.spring.test;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.spring.implementation.MirrorNodeClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MirrorNodeClientImplScheduleTest {

  private static final String BASE_URL = "https://mirror.example";

  private MockRestServiceServer server;

  private MirrorNodeClientImpl mirrorNodeClient;

  @BeforeEach
  void setUp() {
    final RestClient.Builder restClientBuilder = RestClient.builder().baseUrl(BASE_URL);
    server = MockRestServiceServer.bindTo(restClientBuilder).build();
    mirrorNodeClient = new MirrorNodeClientImpl(restClientBuilder);
  }

  @Test
  void querySchedulesFollowsNextLink() throws Exception {
    server
        .expect(requestTo(BASE_URL + "/api/v1/schedules"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(loadJson("schedule-list.json"), MediaType.APPLICATION_JSON));
    server
        .expect(requestTo(BASE_URL + "/api/v1/schedules?limit=3&schedule.id=gt:0.0.11297125"))
        .andRespond(
            withSuccess(
                "{\"schedules\": [], \"links\": {\"next\": null}}", MediaType.APPLICATION_JSON));

    final Page<Schedule> page = mirrorNodeClient.querySchedules();

    Assertions.assertEquals(
        List.of(
            ScheduleId.fromString("0.0.11295061"),
            ScheduleId.fromString("0.0.11296689"),
            ScheduleId.fromString("0.0.11297125")),
        page.getData().stream().map(Schedule::scheduleId).toList());
    Assertions.assertTrue(page.hasNext());

    final Page<Schedule> nextPage = page.next();

    Assertions.assertEquals(1, nextPage.getPageIndex());
    Assertions.assertTrue(nextPage.getData().isEmpty());
    Assertions.assertFalse(nextPage.hasNext());
    server.verify();
  }

  @Test
  void querySchedulesByAccountFiltersByAccountId() throws Exception {
    server
        .expect(requestTo(BASE_URL + "/api/v1/schedules?account.id=eq:0.0.11212783"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(loadJson("schedule-list.json"), MediaType.APPLICATION_JSON));

    final Page<Schedule> page =
        mirrorNodeClient.querySchedulesByAccount(AccountId.fromString("0.0.11212783"));

    Assertions.assertEquals(3, page.getSize());
    Assertions.assertEquals(
        AccountId.fromString("0.0.11212783"), page.getData().get(0).creatorAccountId());
    server.verify();
  }

  @Test
  void queryScheduleByIdReturnsSchedule() throws Exception {
    server
        .expect(requestTo(BASE_URL + "/api/v1/schedules/0.0.11296896"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(loadJson("schedule.json"), MediaType.APPLICATION_JSON));

    final Optional<Schedule> schedule =
        mirrorNodeClient.queryScheduleById(ScheduleId.fromString("0.0.11296896"));

    Assertions.assertTrue(schedule.isPresent());
    Assertions.assertEquals(ScheduleId.fromString("0.0.11296896"), schedule.get().scheduleId());
    Assertions.assertEquals(3, schedule.get().signatures().size());
    server.verify();
  }

  @Test
  void queryScheduleByIdReturnsEmptyOptionalForUnknownSchedule() throws Exception {
    server
        .expect(requestTo(BASE_URL + "/api/v1/schedules/0.0.1"))
        .andRespond(
            withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"_status\": {\"messages\": [{\"message\": \"Not found\"}]}}"));

    final Optional<Schedule> schedule = mirrorNodeClient.queryScheduleById("0.0.1");

    Assertions.assertTrue(schedule.isEmpty());
    server.verify();
  }

  @Test
  void rejectsNullParameters() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> mirrorNodeClient.querySchedulesByAccount((AccountId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.querySchedulesByAccount((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.queryScheduleById((ScheduleId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> mirrorNodeClient.queryScheduleById((String) null));
    server.verify();
  }

  private String loadJson(final String filename) throws IOException {
    final String path = "/json/" + filename;
    try (InputStream stream = MirrorNodeClientImplScheduleTest.class.getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Json fixture not found on classpath: " + path);
      }
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
