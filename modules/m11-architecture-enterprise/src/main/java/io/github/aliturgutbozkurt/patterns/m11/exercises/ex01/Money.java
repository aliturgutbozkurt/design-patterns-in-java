package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

/** GIVEN — do not modify. A non-negative amount in cents. */
public record Money(long cents) {

    public static final Money ZERO = new Money(0);

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));
    }

    public Money times(int factor) {
        return new Money(Math.multiplyExact(cents, factor));
    }
}
