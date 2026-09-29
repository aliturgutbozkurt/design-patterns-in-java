package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

/**
 * Creates ships.
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class SeaLogistics extends Logistics {

    public SeaLogistics() {
        super("Sea");
    }

    @Override
    protected Transport createTransport() {
        return new Ship();
    }
}
