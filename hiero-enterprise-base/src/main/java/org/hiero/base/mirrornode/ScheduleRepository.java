package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with scheduled transactions on a Hiero network. This interface provides
 * methods for searching schedules.
 */
public interface ScheduleRepository {

  /**
   * Return all schedules.
   *
   * @return first page of schedules
   * @throws HieroException if the search fails
   */
  @NonNull Page<Schedule> findAll() throws HieroException;

  /**
   * Return a schedule by its schedule ID.
   *
   * @param scheduleId id of the schedule
   * @return {@link Optional} containing the found schedule
   * @throws HieroException if the search fails
   */
  @NonNull Optional<Schedule> findById(@NonNull ScheduleId scheduleId) throws HieroException;

  /**
   * Return a schedule by its schedule ID.
   *
   * @param scheduleId id of the schedule
   * @return {@link Optional} containing the found schedule
   * @throws HieroException if the search fails
   */
  @NonNull
  default Optional<Schedule> findById(@NonNull String scheduleId) throws HieroException {
    Objects.requireNonNull(scheduleId, "scheduleId must not be null");
    return findById(ScheduleId.fromString(scheduleId));
  }

  /**
   * Return schedules involving an account.
   *
   * @param accountId id of the account
   * @return first page of schedules
   * @throws HieroException if the search fails
   */
  @NonNull Page<Schedule> findByAccountId(@NonNull AccountId accountId) throws HieroException;

  /**
   * Return schedules involving an account.
   *
   * @param accountId id of the account
   * @return first page of schedules
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<Schedule> findByAccountId(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findByAccountId(AccountId.fromString(accountId));
  }
}
