package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * State as an {@code enum}: the whole transition table of a PatternShop order is one exhaustive {@code switch}
 * expression. A new constant without a row does not compile.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public enum OrderStatus {
    NEW, PAID, SHIPPED, DELIVERED, CANCELLED;

    /** The statuses this one may move to, in declaration order (read-only). */
    public Set<OrderStatus> next() {
        EnumSet<OrderStatus> next = switch (this) {
            case NEW -> EnumSet.of(PAID, CANCELLED);
            case PAID -> EnumSet.of(SHIPPED, CANCELLED);
            case SHIPPED -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
        return Collections.unmodifiableSet(next);
    }

    public boolean canMoveTo(OrderStatus target) {
        return next().contains(target);
    }

    public boolean isTerminal() {
        return next().isEmpty();
    }
}
