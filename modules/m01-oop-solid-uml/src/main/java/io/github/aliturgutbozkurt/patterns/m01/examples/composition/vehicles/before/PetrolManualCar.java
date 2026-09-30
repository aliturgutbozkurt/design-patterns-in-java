package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before;

/**
 * Petrol engine + manual gearbox, fixed by inheritance.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class PetrolManualCar extends Vehicle {

    @Override
    protected String engineName() {
        return "Petrol";
    }

    @Override
    protected String gearboxName() {
        return "manual";
    }

    @Override
    public int rangeKm() {
        return (int) Math.round(50.0 * 100 / 6.25);
    }
}
