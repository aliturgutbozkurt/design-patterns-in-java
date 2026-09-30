package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.function.Predicate;

/**
 * Eligibility rules built from small predicates with {@code and}, {@code or} and {@link Predicate#not}.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class Eligibility {

    private Eligibility() {}

    public static Predicate<Customer> member() {
        return Customer::member;
    }

    public static Predicate<Customer> loyal(int minOrders) {
        return customer -> customer.ordersPlaced() >= minOrders;
    }

    public static Predicate<Customer> blocked() {
        return Customer::blocked;
    }

    /** Members or loyal customers (5+ orders), unless blocked. */
    public static Predicate<Customer> freeShipping() {
        return member().or(loyal(5)).and(Predicate.not(blocked()));
    }

    /** Fewer than 2 orders so far, unless blocked. */
    public static Predicate<Customer> newcomer() {
        return loyal(2).negate().and(Predicate.not(blocked()));
    }
}
