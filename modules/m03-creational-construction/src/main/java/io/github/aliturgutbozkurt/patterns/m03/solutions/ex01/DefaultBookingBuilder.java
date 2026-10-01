package io.github.aliturgutbozkurt.patterns.m03.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex01.Booking;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex01.BookingBuilder;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex01.CabinClass;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Reference solution for assignment 01: setters only record values; {@link #build()} checks every rule, reports all
 * problems in one exception, and copies the extras so the booking is immutable and independent of the builder.
 *
 * @see "m03 lesson, section Builder"
 */
public class DefaultBookingBuilder implements BookingBuilder {

    private String traveller;
    private String from;
    private String to;
    private LocalDate departure;
    private LocalDate returnDate;
    private int passengers = 1;
    private CabinClass cabin = CabinClass.ECONOMY;
    private final Set<String> extras = new LinkedHashSet<>();

    @Override
    public BookingBuilder traveller(String name) {
        traveller = Objects.requireNonNull(name, "name");
        return this;
    }

    @Override
    public BookingBuilder from(String airport) {
        from = Objects.requireNonNull(airport, "airport");
        return this;
    }

    @Override
    public BookingBuilder to(String airport) {
        to = Objects.requireNonNull(airport, "airport");
        return this;
    }

    @Override
    public BookingBuilder departure(LocalDate date) {
        departure = Objects.requireNonNull(date, "date");
        return this;
    }

    @Override
    public BookingBuilder returnDate(LocalDate date) {
        returnDate = Objects.requireNonNull(date, "date");
        return this;
    }

    @Override
    public BookingBuilder passengers(int count) {
        passengers = count;
        return this;
    }

    @Override
    public BookingBuilder cabin(CabinClass cabin) {
        this.cabin = Objects.requireNonNull(cabin, "cabin");
        return this;
    }

    @Override
    public BookingBuilder extra(String extra) {
        extras.add(Objects.requireNonNull(extra, "extra"));
        return this;
    }

    @Override
    public Booking build() {
        List<String> problems = new ArrayList<>();
        if (traveller == null) {
            problems.add("missing traveller");
        }
        if (from == null) {
            problems.add("missing from");
        }
        if (to == null) {
            problems.add("missing to");
        }
        if (departure == null) {
            problems.add("missing departure");
        }
        if (from != null && to != null && from.equalsIgnoreCase(to)) {
            problems.add("from and to must differ");
        }
        if (departure != null && returnDate != null && !returnDate.isAfter(departure)) {
            problems.add("return date must be after departure");
        }
        if (passengers < 1 || passengers > 9) {
            problems.add("passengers must be 1-9, got " + passengers);
        }
        if (!problems.isEmpty()) {
            throw new IllegalStateException("invalid booking: " + String.join("; ", problems));
        }
        return new Booking(traveller, from, to, departure, returnDate, passengers, cabin, Set.copyOf(extras));
    }
}
