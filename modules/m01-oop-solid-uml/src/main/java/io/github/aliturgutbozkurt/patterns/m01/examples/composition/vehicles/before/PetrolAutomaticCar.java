package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before;

/**
 * Petrol engine + automatic gearbox, fixed by inheritance.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class PetrolAutomaticCar extends Vehicle {

    @Override
    protected String engineName() {
        return "Petrol";
    }

    @Override
    protected String gearboxName() {
        return "automatic";
    }

    @Override
    public int rangeKm() {
        return (int) Math.round(50.0 * 100 / 6.25);
    }
}
