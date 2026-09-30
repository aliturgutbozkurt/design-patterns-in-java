package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.math.BigDecimal;

/**
 * Charges customers and returns a receipt id.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
@FunctionalInterface
public interface PaymentGateway {

    String charge(String customer, BigDecimal amount);
}
