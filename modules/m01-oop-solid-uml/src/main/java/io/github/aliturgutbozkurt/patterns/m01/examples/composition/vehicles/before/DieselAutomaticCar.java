package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before;

/**
 * Diesel engine + automatic gearbox, fixed by inheritance.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class DieselAutomaticCar extends Vehicle {

    @Override
    protected String engineName() {
        return "Diesel";
    }

    @Override
    protected String gearboxName() {
        return "automatic";
    }

    @Override
    public int rangeKm() {
        return (int) Math.round(55.0 * 100 / 5.0);
    }
}
