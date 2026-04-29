package org.hiero.base.protocol.data;

import java.util.List;
import org.hiero.base.data.HieroTransactionRecord;
import org.jspecify.annotations.NonNull;

/** Response for an account records query. */
public record AccountRecordsResponse(@NonNull List<HieroTransactionRecord> records) {}
