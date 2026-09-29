package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A completed checkout.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public record OrderRecord(String customer, String item, int quantity, BigDecimal total, String receipt,
                          Instant placedAt) {}
