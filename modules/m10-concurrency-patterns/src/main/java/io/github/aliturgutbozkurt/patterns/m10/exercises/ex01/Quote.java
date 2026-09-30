package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. A price offered by one provider, in cents ({@code 12_50} is 12.50). */
public record Quote(String provider, long priceCents) {

    public Quote {
        Objects.requireNonNull(provider, "provider");
        if (priceCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + priceCents);
        }
    }
}
