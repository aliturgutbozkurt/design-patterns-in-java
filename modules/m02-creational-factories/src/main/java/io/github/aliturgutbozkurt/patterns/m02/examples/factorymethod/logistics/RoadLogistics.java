package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

/**
 * Creates trucks.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class RoadLogistics extends Logistics {

    public RoadLogistics() {
        super("Road");
    }

    @Override
    protected Transport createTransport() {
        return new Truck();
    }
}
