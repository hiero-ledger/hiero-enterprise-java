package org.hiero.spring.sample.dto;

/**
 * Request to create a new Hiero account.
 *
 * @param initialBalance The initial balance in Hbar (optional, defaults to 0).
 */
public record AccountCreateRequest(Long initialBalance) {}
