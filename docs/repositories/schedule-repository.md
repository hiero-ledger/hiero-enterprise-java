# Schedule Repository

ScheduleRepository provides APIs for querying scheduled transactions from a Hiero Mirror Node, including retrieving all schedules, searching schedules by the account that created them, and searching a schedule by its schedule ID.

---

## Methods

| Method | Description |
|:-------|:------------|
| `findAll()` | Retrieves a paginated list of schedules from the Mirror Node. |
| `findByAccount(AccountId accountId)` | Retrieves all schedules created by an account. |
| `findByAccount(String accountId)` | Retrieves all schedules created by an account using an account ID string. |
| `findById(ScheduleId scheduleId)` | Retrieves a schedule using a `ScheduleId` object. |
| `findById(String scheduleId)` | Retrieves a schedule using a schedule ID string. |

---

## Find All Schedules

```java title="findAll()"
Page<Schedule> schedules =
    scheduleRepository.findAll();
```

---

## Find Schedules By Account

```java title="findByAccount(AccountId accountId)"
AccountId accountId = AccountId.fromString("0.0.1234");

Page<Schedule> schedules =
    scheduleRepository.findByAccount(accountId);
```

---

## Find Schedule By ID

```java title="findById(ScheduleId scheduleId)"
ScheduleId scheduleId =
    ScheduleId.fromString("0.0.5678");

Optional<Schedule> schedule =
    scheduleRepository.findById(scheduleId);
```

A `Schedule` contains the collected signatures, the execution and expiration timestamps, and the scheduled transaction as protobuf encoded `SchedulableTransactionBody` bytes.