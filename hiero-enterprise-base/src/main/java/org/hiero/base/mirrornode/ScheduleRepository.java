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
   * Return all schedules that were created by the given account.
   *
   * @param accountId id of the account that created the schedules
   * @return first page of schedules
   * @throws HieroException if the search fails
   */
  @NonNull Page<Schedule> findByAccount(@NonNull AccountId accountId) throws HieroException;

  /**
   * Return all schedules that were created by the given account.
   *
   * @param accountId id of the account that created the schedules
   * @return first page of schedules
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<Schedule> findByAccount(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findByAccount(AccountId.fromString(accountId));
  }

  /**
   * Return the schedule with the given schedule ID.
   *
   * @param scheduleId id of the schedule
   * @return {@link Optional} containing the found schedule or an empty {@link Optional}
   * @throws HieroException if the search fails
   */
  @NonNull Optional<Schedule> findById(@NonNull ScheduleId scheduleId) throws HieroException;

  /**
   * Return the schedule with the given schedule ID.
   *
   * @param scheduleId id of the schedule
   * @return {@link Optional} containing the found schedule or an empty {@link Optional}
   * @throws HieroException if the search fails
   */
  @NonNull
  default Optional<Schedule> findById(@NonNull String scheduleId) throws HieroException {
    Objects.requireNonNull(scheduleId, "scheduleId must not be null");
    return findById(ScheduleId.fromString(scheduleId));
  }
}
