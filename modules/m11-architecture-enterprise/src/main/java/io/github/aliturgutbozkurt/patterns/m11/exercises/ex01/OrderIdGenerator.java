package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

/** GIVEN — do not modify. Outbound port: hands out a new order id on every call. */
@FunctionalInterface
public interface OrderIdGenerator {

    OrderId next();
}
