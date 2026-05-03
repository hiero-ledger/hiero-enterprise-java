package org.hiero.microprofile.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.hiero.base.data.Schedule;
import org.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import org.junit.jupiter.api.Test;

class MirrorNodeJsonConverterImplScheduleTest {

  private final MirrorNodeJsonConverterImpl converter = new MirrorNodeJsonConverterImpl();

  @Test
  void toSchedulesReturnsEmptyWhenKeyMissing() {
    final JsonObject jsonObject = parse("{\"links\":{\"next\":null}}");

    final List<Schedule> result = converter.toSchedules(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toSchedulesReturnsEmptyForEmptyArray() {
    final JsonObject jsonObject = parse("{\"schedules\":[],\"links\":{\"next\":null}}");

    final List<Schedule> result = converter.toSchedules(jsonObject);

    assertTrue(result.isEmpty());
  }

  @Test
  void toSchedulesParsesSingleEntry() {
    final JsonObject jsonObject = parse(schedulesJson());

    final List<Schedule> result = converter.toSchedules(jsonObject);

    assertEquals(1, result.size());
    assertSchedule(result.get(0));
  }

  @Test
  void toScheduleParsesSingleEntry() {
    final JsonObject jsonObject = parse(scheduleJson());

    final Schedule schedule = converter.toSchedule(jsonObject).orElseThrow();

    assertSchedule(schedule);
  }

  private static void assertSchedule(final Schedule schedule) {
    assertEquals(ScheduleId.fromString("0.0.7007"), schedule.scheduleId());
    assertNull(schedule.adminKey());
    assertFalse(schedule.deleted());
    assertEquals(Instant.ofEpochSecond(1_586_567_700L, 453_054_000), schedule.consensusTimestamp());
    assertEquals(AccountId.fromString("0.0.1001"), schedule.creatorAccountId());
    assertNull(schedule.executedTimestamp());
    assertEquals(Instant.ofEpochSecond(1_586_567_800L, 1), schedule.expirationTime());
    assertEquals("scheduled transfer", schedule.memo());
    assertEquals(AccountId.fromString("0.0.1002"), schedule.payerAccountId());
    assertArrayEquals("test-body".getBytes(StandardCharsets.UTF_8), schedule.transactionBody());
    assertFalse(schedule.waitForExpiry());
    assertEquals(1, schedule.signatures().size());
    assertEquals(
        Instant.ofEpochSecond(1_586_567_710L, 111_222_333),
        schedule.signatures().get(0).consensusTimestamp());
    assertEquals("0x0102", schedule.signatures().get(0).publicKeyPrefix());
    assertEquals("0x0304", schedule.signatures().get(0).signature());
    assertEquals("ED25519", schedule.signatures().get(0).type());
  }

  private static JsonObject parse(final String json) {
    return Json.createReader(new StringReader(json)).readObject();
  }

  private static String schedulesJson() {
    return """
        {
          "schedules": [
            %s
          ],
          "links": {"next": null}
        }
        """
        .formatted(scheduleJson());
  }

  private static String scheduleJson() {
    return """
        {
          "admin_key": null,
          "deleted": false,
          "consensus_timestamp": "1586567700.453054000",
          "creator_account_id": "0.0.1001",
          "executed_timestamp": null,
          "expiration_time": "1586567800.000000001",
          "memo": "scheduled transfer",
          "payer_account_id": "0.0.1002",
          "schedule_id": "0.0.7007",
          "signatures": [
            {
              "consensus_timestamp": "1586567710.111222333",
              "public_key_prefix": "0x0102",
              "signature": "0x0304",
              "type": "ED25519"
            }
          ],
          "transaction_body": "dGVzdC1ib2R5",
          "wait_for_expiry": false
        }
        """;
  }
}
