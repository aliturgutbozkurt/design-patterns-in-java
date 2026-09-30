package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

import java.math.BigDecimal;

/**
 * The product: a way of moving cargo.
 *
 * @see "m02 lesson, section Factory Method"
 */
public interface Transport {

    String name();

    BigDecimal cost(Cargo cargo);

    int days(Cargo cargo);
}
