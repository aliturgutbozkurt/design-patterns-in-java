package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

import java.math.BigDecimal;

/**
 * 1.20 per km; 800 km per day.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class Truck implements Transport {

    @Override
    public String name() {
        return "Truck";
    }

    @Override
    public BigDecimal cost(Cargo cargo) {
        return new BigDecimal("1.20").multiply(BigDecimal.valueOf(cargo.distanceKm()));
    }

    @Override
    public int days(Cargo cargo) {
        return (cargo.distanceKm() + 799) / 800;
    }
}
