package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.jspecify.annotations.NonNull;

/** Implementation of ScheduleRepository that uses MirrorNodeClient to query schedule data. */
public class ScheduleRepositoryImpl implements ScheduleRepository {

  private final MirrorNodeClient mirrorNodeClient;

  /**
   * Creates a new ScheduleRepositoryImpl with the given MirrorNodeClient.
   *
   * @param mirrorNodeClient the mirror node client to use for queries
   */
  public ScheduleRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<Schedule> findAll() throws HieroException {
    return mirrorNodeClient.querySchedules();
  }

  @NonNull
  @Override
  public Optional<Schedule> findById(@NonNull final ScheduleId scheduleId) throws HieroException {
    return mirrorNodeClient.queryScheduleById(scheduleId);
  }

  @NonNull
  @Override
  public Page<Schedule> findByAccountId(@NonNull final AccountId accountId) throws HieroException {
    return mirrorNodeClient.querySchedulesByAccount(accountId);
  }
}
