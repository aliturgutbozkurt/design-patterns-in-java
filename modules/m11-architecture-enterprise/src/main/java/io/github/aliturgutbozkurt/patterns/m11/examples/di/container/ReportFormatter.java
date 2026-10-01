package io.github.aliturgutbozkurt.patterns.m11.examples.di.container;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.stream.Collectors;

/**
 * Formats the report; needs a {@link Clock}, the third level of the sample graph.
 *
 * @see "m11 lesson, section Dependency Injection — how a container works"
 */
public final class ReportFormatter {

    private final Clock clock;

    public ReportFormatter(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /** One line: today's date and every product with its units. */
    public String format(SequencedMap<String, Integer> unitsSold) {
        return "sales " + LocalDate.now(clock) + ": " + unitsSold.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
