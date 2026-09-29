package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import java.util.Optional;
import java.util.Set;

/**
 * Subsystem (in-memory fake): ships to a fixed set of countries and hands out sequential tracking numbers.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public final class ShippingService {

    private final Set<String> countries;
    private int nextTracking = 1001;

    public ShippingService(Set<String> countries) {
        this.countries = Set.copyOf(countries);
    }

    /** Returns a tracking number, or empty when the country is not served. */
    public Optional<String> ship(Address address, String reservationId) {
        if (!countries.contains(address.country())) {
            return Optional.empty();
        }
        return Optional.of("TRK-" + nextTracking++);
    }
}
