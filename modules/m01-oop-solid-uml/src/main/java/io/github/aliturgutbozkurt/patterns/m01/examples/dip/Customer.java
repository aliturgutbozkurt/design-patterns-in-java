package io.github.aliturgutbozkurt.patterns.m01.examples.dip;

import java.util.Objects;

/**
 * A customer with contact details; shared by the before and after versions.
 *
 * @see "m01 lesson, section DIP"
 */
public record Customer(String name, String email, String phone, Channel preferred) {

    public Customer {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(phone, "phone");
        Objects.requireNonNull(preferred, "preferred");
    }
}
