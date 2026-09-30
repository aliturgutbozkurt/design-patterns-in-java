package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

import java.util.Objects;

/**
 * The creator. Its business logic — planning a delivery — is written once against {@link Transport}; subclasses
 * only override the factory method {@link #createTransport()}.
 *
 * @see "m02 lesson, section Factory Method"
 */
public abstract class Logistics {

    private final String mode;

    protected Logistics(String mode) {
        this.mode = Objects.requireNonNull(mode, "mode");
    }

    /** The factory method. */
    protected abstract Transport createTransport();

    public final String planDelivery(Cargo cargo) {
        Transport transport = createTransport();
        return "%s: %s by %s, %d km, cost %s, %d day(s)".formatted(mode, cargo.description(), transport.name(),
                cargo.distanceKm(), transport.cost(cargo).setScale(2).toPlainString(), transport.days(cargo));
    }
}
