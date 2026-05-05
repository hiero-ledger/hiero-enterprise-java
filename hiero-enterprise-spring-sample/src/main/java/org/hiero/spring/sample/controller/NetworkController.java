package org.hiero.spring.sample.controller;

import java.util.List;
import java.util.Objects;
import org.hiero.base.data.ExchangeRates;
import org.hiero.base.data.NetworkFee;
import org.hiero.base.data.NetworkStake;
import org.hiero.base.data.NetworkSupplies;
import org.hiero.base.mirrornode.NetworkRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero network operations.
 * Provides endpoints for querying network-wide information like exchange rates, fees, and staking.
 */
@RestController
@RequestMapping("/api/v1/hiero/network")
public class NetworkController {

  private final NetworkRepository networkRepository;

  public NetworkController(final NetworkRepository networkRepository) {
    this.networkRepository =
        Objects.requireNonNull(networkRepository, "networkRepository must not be null");
  }

  /**
   * Retrieves the current and next exchange rates.
   */
  @GetMapping("/exchange-rate")
  public ExchangeRates getExchangeRates() {
    try {
      return networkRepository.exchangeRates()
          .orElseThrow(() -> new RuntimeException("Exchange rates not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query exchange rates", e);
    }
  }

  /**
   * Retrieves the network fees.
   */
  @GetMapping("/fee")
  public List<NetworkFee> getFees() {
    try {
      return networkRepository.fees();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network fees", e);
    }
  }

  /**
   * Retrieves network staking information.
   */
  @GetMapping("/stake")
  public NetworkStake getStake() {
    try {
      return networkRepository.stake()
          .orElseThrow(() -> new RuntimeException("Network stake info not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network stake", e);
    }
  }

  /**
   * Retrieves network supply information.
   */
  @GetMapping("/supplies")
  public NetworkSupplies getSupplies() {
    try {
      return networkRepository.supplies()
          .orElseThrow(() -> new RuntimeException("Network supply info not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network supplies", e);
    }
  }
}
