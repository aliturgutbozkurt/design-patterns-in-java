package io.github.aliturgutbozkurt.patterns.m03.exercises.ex01;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * GIVEN — do not modify. A plain data carrier: validation, defaults and defensive copies are the builder's job.
 * {@code returnDate} is {@code null} for a one-way trip; use {@link #returnTrip()} to read it.
 */
public record Booking(String traveller, String from, String to, LocalDate departure, LocalDate returnDate,
                      int passengers, CabinClass cabin, Set<String> extras) {

    public Optional<LocalDate> returnTrip() {
        return Optional.ofNullable(returnDate);
    }
}
