package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. A bid in cents, e.g. {@code new Bid("ada", 10_000)} is 100.00. */
public record Bid(String bidder, long amountCents) {

    public Bid {
        Objects.requireNonNull(bidder, "bidder");
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amountCents must be positive: " + amountCents);
        }
    }
}
