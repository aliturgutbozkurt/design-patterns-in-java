package io.github.aliturgutbozkurt.patterns.m11.examples.di.styles;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Setter injection: the object exists before its dependencies do, so every caller must remember to call both setters
 * first (temporal coupling) — a half-built object fails only at run time.
 *
 * @see "m11 lesson, section Dependency Injection — injection styles"
 */
public final class SetterInjected implements InvoiceNumberer {

    private Clock clock;
    private Supplier<Long> sequence;

    public void setClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void setSequence(Supplier<Long> sequence) {
        this.sequence = Objects.requireNonNull(sequence, "sequence");
    }

    @Override
    public String next() {
        if (clock == null) {
            throw new IllegalStateException("clock not set");
        }
        if (sequence == null) {
            throw new IllegalStateException("sequence not set");
        }
        return InvoiceNumberer.format(LocalDate.now(clock), sequence.get());
    }
}
