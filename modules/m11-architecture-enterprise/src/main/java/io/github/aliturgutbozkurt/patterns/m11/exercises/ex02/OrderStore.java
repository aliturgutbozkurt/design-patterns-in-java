package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.Optional;

/** GIVEN — do not modify. Outbound port: loads and commits order state; {@code commit} may throw. */
public interface OrderStore {

    Optional<OrderSnapshot> load(OrderId id);

    /** Stores {@code snapshot} (replacing the previous state of that order), or throws and stores nothing. */
    void commit(OrderSnapshot snapshot);
}
