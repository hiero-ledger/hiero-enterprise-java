package org.hiero.spring.sample.dto.network;

import java.time.Instant;
import org.hiero.base.data.ExchangeRates;

/**
 * Response DTO for Network Exchange Rates.
 */
public record ExchangeRatesResponse(
    RateInfo currentRate,
    RateInfo nextRate
) {
  public record RateInfo(
      int centEquivalent,
      int hbarEquivalent,
      Instant expirationTime
  ) {}

  public static ExchangeRatesResponse fromDomain(ExchangeRates rates) {
    return new ExchangeRatesResponse(
        new RateInfo(rates.currentRate().centEquivalent(), rates.currentRate().hbarEquivalent(), rates.currentRate().expirationTime()),
        new RateInfo(rates.nextRate().centEquivalent(), rates.nextRate().hbarEquivalent(), rates.nextRate().expirationTime())
    );
  }
}
