package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with a Hiero network. This interface provides methods for searching for
 * Accounts.
 */
public interface AccountRepository {
  /**
   * Return the AccountInfo of a given accountId.
   *
   * @param accountId id of the account
   * @return {@link Optional} containing the found AccountInfo or null
   * @throws HieroException if the search fails
   */
  @NonNull Optional<AccountInfo> findById(@NonNull AccountId accountId) throws HieroException;

  /**
   * Return the AccountInfo of a given accountId.
   *
   * @param accountId id of the account
   * @return {@link Optional} containing the found AccountInfo or null
   * @throws HieroException if the search fails
   */
  @NonNull
  default Optional<AccountInfo> findById(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findById(AccountId.fromString(accountId));
  }

  /**
   * Return HBAR allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of HBAR allowances
   * @throws HieroException if the search fails
   */
  @NonNull Page<CryptoAllowance> findCryptoAllowances(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Return HBAR allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of HBAR allowances
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<CryptoAllowance> findCryptoAllowances(@NonNull String accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findCryptoAllowances(AccountId.fromString(accountId));
  }

  /**
   * Return fungible token allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of fungible token allowances
   * @throws HieroException if the search fails
   */
  @NonNull Page<TokenAllowance> findTokenAllowances(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Return fungible token allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of fungible token allowances
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<TokenAllowance> findTokenAllowances(@NonNull String accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findTokenAllowances(AccountId.fromString(accountId));
  }

  /**
   * Return non-fungible token allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of non-fungible token allowances
   * @throws HieroException if the search fails
   */
  @NonNull Page<NftAllowance> findNftAllowances(@NonNull AccountId accountId) throws HieroException;

  /**
   * Return non-fungible token allowances granted by the given account.
   *
   * @param accountId id of the account
   * @return page of non-fungible token allowances
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<NftAllowance> findNftAllowances(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findNftAllowances(AccountId.fromString(accountId));
  }

  /**
   * Return past staking reward payouts for the given account.
   *
   * @param accountId id of the account
   * @return page of staking reward payouts
   * @throws HieroException if the search fails
   */
  @NonNull Page<StakingReward> findStakingRewards(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Return past staking reward payouts for the given account.
   *
   * @param accountId id of the account
   * @return page of staking reward payouts
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<StakingReward> findStakingRewards(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findStakingRewards(AccountId.fromString(accountId));
  }

  /**
   * Return outstanding token airdrops sent by the given account.
   *
   * @param accountId id of the account
   * @return page of token airdrops
   * @throws HieroException if the search fails
   */
  @NonNull Page<TokenAirdrop> findOutstandingAirdrops(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Return outstanding token airdrops sent by the given account.
   *
   * @param accountId id of the account
   * @return page of token airdrops
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<TokenAirdrop> findOutstandingAirdrops(@NonNull String accountId)
      throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findOutstandingAirdrops(AccountId.fromString(accountId));
  }

  /**
   * Return pending token airdrops received by the given account.
   *
   * @param accountId id of the account
   * @return page of token airdrops
   * @throws HieroException if the search fails
   */
  @NonNull Page<TokenAirdrop> findPendingAirdrops(@NonNull AccountId accountId)
      throws HieroException;

  /**
   * Return pending token airdrops received by the given account.
   *
   * @param accountId id of the account
   * @return page of token airdrops
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<TokenAirdrop> findPendingAirdrops(@NonNull String accountId) throws HieroException {
    Objects.requireNonNull(accountId, "accountId must not be null");
    return findPendingAirdrops(AccountId.fromString(accountId));
  }
}
