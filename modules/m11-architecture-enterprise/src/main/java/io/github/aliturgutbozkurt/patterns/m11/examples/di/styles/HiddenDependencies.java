package io.github.aliturgutbozkurt.patterns.m11.examples.di.styles;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Hidden dependencies: the class creates its own clock and counter, so nothing in its signature tells a caller (or a
 * test) what it needs — and neither can be replaced. The counter is an instance field on purpose: the course allows
 * mutable static state only in {@code antipatterns.globalstate.before}.
 *
 * @see "m11 lesson, section Dependency Injection — injection styles"
 */
public final class HiddenDependencies implements InvoiceNumberer {

    private final Clock clock = Clock.systemDefaultZone(); // hidden: today's date, every time
    private long counter;                                  // hidden: cannot start at a known value

    @Override
    public String next() {
        return InvoiceNumberer.format(LocalDate.now(clock), ++counter);
    }
}
