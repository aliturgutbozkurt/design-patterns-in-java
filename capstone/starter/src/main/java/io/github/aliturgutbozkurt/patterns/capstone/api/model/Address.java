package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.util.Objects;

/**
 * GIVEN — do not modify. A shipping address. Fields may be blank (a checkout then reports {@code missing address});
 * {@code null} is a programming error.
 *
 * @param recipient  who receives the parcel
 * @param street     street and number
 * @param city       city
 * @param postalCode postal code, passed to the warehouse when shipping
 * @see "capstone brief, Business rules — Checkout"
 */
public record Address(String recipient, String street, String city, String postalCode) {

    public Address {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(street, "street");
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(postalCode, "postalCode");
    }

    /** An address is complete when no field is blank. */
    public boolean isComplete() {
        return !recipient.isBlank() && !street.isBlank() && !city.isBlank() && !postalCode.isBlank();
    }
}
