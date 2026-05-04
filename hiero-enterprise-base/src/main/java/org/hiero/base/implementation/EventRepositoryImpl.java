package org.hiero.base.implementation;

import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.hiero.base.events.EventFilter;
import org.hiero.base.mirrornode.EventRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

/**
 * Implementation of EventRepository that uses MirrorNodeClient to query contract logs.
 */
public class EventRepositoryImpl implements EventRepository {

  private final MirrorNodeClient mirrorNodeClient;

  public EventRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<ContractLog> findLogs(@NonNull EventFilter filter) throws HieroException {
    return mirrorNodeClient.queryContractLogs(filter);
  }
}
