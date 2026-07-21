package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.data.Node;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with a Hiero network. This interface provides methods to get
 * information related to Network.
 */
public interface NetworkRepository {
  /**
   * Return the ExchangeRates for network.
   *
   * @return {@link Optional} containing the ExchangeRates or null
   * @throws HieroException if the search fails
   */
  @NonNull Optional<ExchangeRates> exchangeRates() throws HieroException;

  /**
   * Return the List of NetworkFee for network.
   *
   * @return {@link List} containing NetworkFee or empty list
   * @throws HieroException if the search fails
   */
  @NonNull List<NetworkFee> fees() throws HieroException;

  /**
   * Return the NetworkStake for network.
   *
   * @return {@link Optional} containing NetworkStake or null
   * @throws HieroException if the search fails
   */
  @NonNull Optional<NetworkStake> stake() throws HieroException;

  /**
   * Return the NetworkSupplies for network.
   *
   * @return {@link Optional} containing NetworkSupplies or null
   * @throws HieroException if the search fails
   */
  @NonNull Optional<NetworkSupplies> supplies() throws HieroException;

  /**
   * Returns the network address book nodes.
   *
   * @return a {@link Page} containing the network nodes
   * @throws HieroException if the search fails
   */
  @NonNull Page<Node> findNodes() throws HieroException;

  /**
   * Returns the network node for the given node ID.
   *
   * @param nodeId the node account ID
   * @return the matching {@link Node}, if present
   * @throws HieroException if the search fails
   */
  @NonNull Optional<Node> findNodeById(@NonNull AccountId nodeId) throws HieroException;
}
