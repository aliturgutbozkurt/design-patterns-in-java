package io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel;

import java.util.Objects;

/**
 * A planned trip, assembled from three results of different types.
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public record Trip(Flight flight, Hotel hotel, Forecast forecast) {

    /**
     * A booked flight.
     *
     * @see "m10 lesson, section Structured Concurrency"
     */
    public record Flight(String number) {}

    /**
     * A booked hotel.
     *
     * @see "m10 lesson, section Structured Concurrency"
     */
    public record Hotel(String name) {}

    /**
     * The weather forecast at the destination.
     *
     * @see "m10 lesson, section Structured Concurrency"
     */
    public record Forecast(String summary) {}

    public Trip {
        Objects.requireNonNull(flight, "flight");
        Objects.requireNonNull(hotel, "hotel");
        Objects.requireNonNull(forecast, "forecast");
    }
}
