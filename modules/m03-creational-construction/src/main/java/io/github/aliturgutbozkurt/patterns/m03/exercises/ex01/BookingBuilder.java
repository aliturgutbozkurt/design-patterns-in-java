package io.github.aliturgutbozkurt.patterns.m03.exercises.ex01;

import java.time.LocalDate;

/** GIVEN — do not modify. Every setter returns the builder; {@code null} arguments throw {@code NullPointerException}. */
public interface BookingBuilder {

    BookingBuilder traveller(String name);

    BookingBuilder from(String airport);

    BookingBuilder to(String airport);

    BookingBuilder departure(LocalDate date);

    BookingBuilder returnDate(LocalDate date);

    BookingBuilder passengers(int count);

    BookingBuilder cabin(CabinClass cabin);

    BookingBuilder extra(String extra);

    /** @throws IllegalStateException listing every violated rule */
    Booking build();
}
