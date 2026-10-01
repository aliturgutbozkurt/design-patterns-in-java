package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

/** GIVEN — do not modify. Outbound port: announces confirmed orders. */
@FunctionalInterface
public interface EventPublisher {

    void publish(OrderPlaced event);
}
