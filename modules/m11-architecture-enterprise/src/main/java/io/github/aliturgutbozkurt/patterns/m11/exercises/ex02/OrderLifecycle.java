package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.function.Consumer;

/**
 * GIVEN — do not modify. The order lifecycle as a use case: every command loads, changes the aggregate (which
 * records events), commits, and only then dispatches the events to the subscribers.
 */
public interface OrderLifecycle {

    /** Places a new order; {@code totalCents ≤ 0} → {@link IllegalArgumentException}. */
    OrderId place(long totalCents);

    void pay(OrderId id);

    void ship(OrderId id);

    void cancel(OrderId id, String reason);

    /** The committed status; unknown id → {@link java.util.NoSuchElementException}. */
    OrderStatus status(OrderId id);

    /** Delivers events of {@code type} (or a subtype) to {@code handler}, in subscription order. */
    <E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler);
}
