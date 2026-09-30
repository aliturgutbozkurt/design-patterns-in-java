package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

import java.math.BigDecimal;

/**
 * 0.40 per km plus a 500.00 port fee; 500 km per day plus 2 days in port.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class Ship implements Transport {

    @Override
    public String name() {
        return "Ship";
    }

    @Override
    public BigDecimal cost(Cargo cargo) {
        return new BigDecimal("0.40").multiply(BigDecimal.valueOf(cargo.distanceKm())).add(new BigDecimal("500.00"));
    }

    @Override
    public int days(Cargo cargo) {
        return (cargo.distanceKm() + 499) / 500 + 2;
    }
}
