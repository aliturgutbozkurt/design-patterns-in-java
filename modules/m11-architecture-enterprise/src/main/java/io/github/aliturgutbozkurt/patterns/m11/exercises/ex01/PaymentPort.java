package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

/** GIVEN — do not modify. Outbound port: charges a customer; {@code true} = approved, {@code false} = declined. */
@FunctionalInterface
public interface PaymentPort {

    boolean charge(String customer, Money amount);
}
