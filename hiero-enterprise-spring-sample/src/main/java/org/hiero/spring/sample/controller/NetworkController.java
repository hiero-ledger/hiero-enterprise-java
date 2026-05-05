package org.hiero.spring.sample.controller;

import java.util.List;
import java.util.Objects;
import org.hiero.base.mirrornode.NetworkRepository;
import org.hiero.spring.sample.dto.network.ExchangeRatesResponse;
import org.hiero.spring.sample.dto.network.NetworkFeeResponse;
import org.hiero.spring.sample.dto.network.NetworkStakeResponse;
import org.hiero.spring.sample.dto.network.NetworkSuppliesResponse;
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
  public ExchangeRatesResponse getExchangeRates() {
    try {
      return networkRepository.exchangeRates()
          .map(ExchangeRatesResponse::fromDomain)
          .orElseThrow(() -> new RuntimeException("Exchange rates not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query exchange rates", e);
    }
  }

  /**
   * Retrieves the network fees.
   */
  @GetMapping("/fee")
  public List<NetworkFeeResponse> getFees() {
    try {
      return networkRepository.fees().stream()
          .map(NetworkFeeResponse::fromDomain)
          .toList();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network fees", e);
    }
  }

  /**
   * Retrieves network staking information.
   */
  @GetMapping("/stake")
  public NetworkStakeResponse getStake() {
    try {
      return networkRepository.stake()
          .map(NetworkStakeResponse::fromDomain)
          .orElseThrow(() -> new RuntimeException("Network stake info not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network stake", e);
    }
  }

  /**
   * Retrieves network supply information.
   */
  @GetMapping("/supplies")
  public NetworkSuppliesResponse getSupplies() {
    try {
      return networkRepository.supplies()
          .map(NetworkSuppliesResponse::fromDomain)
          .orElseThrow(() -> new RuntimeException("Network supply info not available"));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query network supplies", e);
    }
  }
}
