package io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * The same plan with a deadline for the whole scope ({@code Configuration.withTimeout}). When it expires the scope
 * is cancelled, the unfinished subtasks are interrupted, and {@code join()} throws an {@link ExecutionException}
 * caused by {@link StructuredTaskScope.CancelledByTimeoutException}. Preview API (JEP 533).
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class TripPlannerWithDeadline {

    private final Lookup<Trip.Flight> flights;
    private final Lookup<Trip.Hotel> hotels;
    private final Lookup<Trip.Forecast> forecasts;
    private final Duration deadline;

    public TripPlannerWithDeadline(Lookup<Trip.Flight> flights, Lookup<Trip.Hotel> hotels,
            Lookup<Trip.Forecast> forecasts, Duration deadline) {
        this.flights = Objects.requireNonNull(flights, "flights");
        this.hotels = Objects.requireNonNull(hotels, "hotels");
        this.forecasts = Objects.requireNonNull(forecasts, "forecasts");
        this.deadline = Objects.requireNonNull(deadline, "deadline");
    }

    public Trip plan(String destination) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow(),
                config -> config.withTimeout(deadline))) {
            Subtask<Trip.Flight> flight = scope.fork(() -> flights.find(destination));
            Subtask<Trip.Hotel> hotel = scope.fork(() -> hotels.find(destination));
            Subtask<Trip.Forecast> forecast = scope.fork(() -> forecasts.find(destination));
            scope.join();                        // also throws when the deadline cancels the scope
            return new Trip(flight.get(), hotel.get(), forecast.get());
        }
    }
}
