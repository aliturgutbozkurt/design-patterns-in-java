package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import java.util.Objects;

/**
 * One past order of a customer.
 *
 * @see "m09 lesson, section Optional and Result — Optional as a return type"
 */
public record OrderSummary(String orderId, CustomerId customer, long totalCents) {

    public OrderSummary {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(customer, "customer");
    }

    @Override
    public String toString() {
        return orderId;
    }
}
