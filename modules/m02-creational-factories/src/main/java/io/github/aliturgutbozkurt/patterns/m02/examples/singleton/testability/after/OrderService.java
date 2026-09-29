package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after;

import java.util.Objects;

/**
 * The dependency is visible in the constructor and replaceable in tests.
 *
 * @see "m02 lesson, section Singleton — why it is often an anti-pattern"
 */
public final class OrderService {

    private final IdSource ids;

    public OrderService(IdSource ids) {
        this.ids = Objects.requireNonNull(ids, "ids");
    }

    public String placeOrder() {
        return "ORD-" + ids.nextId();
    }
}
