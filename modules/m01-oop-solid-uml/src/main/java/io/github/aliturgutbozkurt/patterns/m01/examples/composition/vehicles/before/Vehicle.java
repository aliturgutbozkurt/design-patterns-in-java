package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before;

/**
 * Class explosion: engine and gearbox are two independent dimensions, but inheritance can only vary one axis, so
 * every combination becomes its own subclass (engines × gearboxes).
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public abstract sealed class Vehicle permits PetrolManualCar, PetrolAutomaticCar, DieselManualCar, DieselAutomaticCar {

    protected abstract String engineName();

    protected abstract String gearboxName();

    public abstract int rangeKm();

    public String describe() {
        return engineName() + " engine, " + gearboxName() + " gearbox, range " + rangeKm() + " km";
    }
}
