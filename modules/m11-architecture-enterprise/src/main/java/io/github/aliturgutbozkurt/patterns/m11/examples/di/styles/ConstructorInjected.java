package io.github.aliturgutbozkurt.patterns.m11.examples.di.styles;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Constructor injection, the default: every dependency is visible in the signature, required and {@code final} —
 * the object is complete the moment it exists.
 *
 * @see "m11 lesson, section Dependency Injection — injection styles"
 */
public final class ConstructorInjected implements InvoiceNumberer {

    private final Clock clock;
    private final Supplier<Long> sequence;

    public ConstructorInjected(Clock clock, Supplier<Long> sequence) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sequence = Objects.requireNonNull(sequence, "sequence");
    }

    @Override
    public String next() {
        return InvoiceNumberer.format(LocalDate.now(clock), sequence.get());
    }
}
