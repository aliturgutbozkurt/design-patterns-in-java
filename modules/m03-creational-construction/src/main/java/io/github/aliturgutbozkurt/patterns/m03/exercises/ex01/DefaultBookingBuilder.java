package io.github.aliturgutbozkurt.patterns.m03.exercises.ex01;

import java.time.LocalDate;

/** Assignment 01 — your builder. See assignments/01-booking-builder.en.md (Türkçe: 01-booking-builder.tr.md). */
public class DefaultBookingBuilder implements BookingBuilder {

    // TODO(ex01): fields with defaults — one-way, 1 passenger, ECONOMY, no extras.

    @Override
    public BookingBuilder traveller(String name) {
        throw new UnsupportedOperationException("TODO(ex01): implement traveller(String)");
    }

    @Override
    public BookingBuilder from(String airport) {
        throw new UnsupportedOperationException("TODO(ex01): implement from(String)");
    }

    @Override
    public BookingBuilder to(String airport) {
        throw new UnsupportedOperationException("TODO(ex01): implement to(String)");
    }

    @Override
    public BookingBuilder departure(LocalDate date) {
        throw new UnsupportedOperationException("TODO(ex01): implement departure(LocalDate)");
    }

    @Override
    public BookingBuilder returnDate(LocalDate date) {
        throw new UnsupportedOperationException("TODO(ex01): implement returnDate(LocalDate)");
    }

    @Override
    public BookingBuilder passengers(int count) {
        throw new UnsupportedOperationException("TODO(ex01): implement passengers(int)");
    }

    @Override
    public BookingBuilder cabin(CabinClass cabin) {
        throw new UnsupportedOperationException("TODO(ex01): implement cabin(CabinClass)");
    }

    @Override
    public BookingBuilder extra(String extra) {
        throw new UnsupportedOperationException("TODO(ex01): implement extra(String)");
    }

    @Override
    public Booking build() {
        // TODO(ex01): collect every problem, throw one IllegalStateException listing them all, copy the extras.
        throw new UnsupportedOperationException("TODO(ex01): implement build()");
    }
}
