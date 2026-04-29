package org.hiero.base;

import com.hedera.hashgraph.sdk.Key;
import java.time.Duration;
import org.jspecify.annotations.NonNull;

/**
 * A fluent builder for updating a Hiero account.
 */
public interface AccountUpdateBuilder {

    /**
     * Sets the new key for the account.
     *
     * @param key the new key
     * @return this builder
     */
    @NonNull AccountUpdateBuilder key(@NonNull Key key);

    /**
     * Sets the new memo for the account.
     *
     * @param memo the new memo
     * @return this builder
     */
    @NonNull AccountUpdateBuilder memo(@NonNull String memo);

    /**
     * Sets the new auto-renew period for the account.
     *
     * @param duration the new duration
     * @return this builder
     */
    @NonNull AccountUpdateBuilder autoRenewPeriod(@NonNull Duration duration);

    /**
     * Sets whether a receiver signature is required for the account.
     *
     * @param required true if a signature is required
     * @return this builder
     */
    @NonNull AccountUpdateBuilder receiverSignatureRequired(boolean required);

    /**
     * Sets the maximum number of automatic token associations for the account.
     *
     * @param max the maximum number
     * @return this builder
     */
    @NonNull AccountUpdateBuilder maxAutomaticTokenAssociations(int max);

    /**
     * Executes the update transaction on the Hiero network.
     *
     * @throws HieroException if the update could not be performed
     */
    void execute() throws HieroException;
}
