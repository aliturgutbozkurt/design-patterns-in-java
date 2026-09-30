package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.Objects;

/**
 * The facts about a customer that the eligibility rules look at.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public record Customer(String name, boolean member, int ordersPlaced, boolean blocked) {

    public Customer {
        Objects.requireNonNull(name, "name");
    }
}
