package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.values;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * A promotion period from {@code start} to {@code endInclusive}. Built from {@code java.time} values, which are the
 * JDK's own immutable value types, so the range is immutable too.
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public record DateRange(LocalDate start, LocalDate endInclusive) {

    public DateRange {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(endInclusive, "endInclusive");
        if (endInclusive.isBefore(start)) {
            throw new IllegalArgumentException("endInclusive " + endInclusive + " is before start " + start);
        }
    }

    /** Number of days, counting both ends. */
    public long days() {
        return ChronoUnit.DAYS.between(start, endInclusive) + 1;
    }

    /** True if the two ranges share at least one day (touching ranges overlap). */
    public boolean overlaps(DateRange other) {
        return !endInclusive.isBefore(other.start) && !other.endInclusive.isBefore(start);
    }

    public DateRange withEnd(LocalDate newEnd) {
        return new DateRange(start, newEnd);
    }

    public DateRange shiftedBy(Period period) {
        return new DateRange(start.plus(period), endInclusive.plus(period));
    }

    @Override
    public String toString() {
        return start + ".." + endInclusive;
    }
}
