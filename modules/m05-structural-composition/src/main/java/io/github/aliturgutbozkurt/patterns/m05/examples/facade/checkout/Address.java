package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import java.util.Objects;

/**
 * Where an order is shipped; {@code country} is an ISO code such as {@code TR}.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public record Address(String recipient, String city, String country) {

    public Address {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(country, "country");
    }
}
