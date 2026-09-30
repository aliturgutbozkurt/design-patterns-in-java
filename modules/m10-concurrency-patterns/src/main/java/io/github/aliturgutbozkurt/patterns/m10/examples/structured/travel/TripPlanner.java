package io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * Structured concurrency (preview in JDK 27, JEP 533): three look-ups run as subtasks of one scope. The scope is a
 * block: no subtask outlives it. If one subtask fails, the scope is cancelled, its siblings are interrupted, and
 * {@code join()} throws an {@link ExecutionException} with that failure as the cause. {@code open()} uses the
 * default joiner, {@code awaitAllSuccessfulOrThrow}, so the results come from the {@link Subtask} handles.
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class TripPlanner {

    private final Lookup<Trip.Flight> flights;
    private final Lookup<Trip.Hotel> hotels;
    private final Lookup<Trip.Forecast> forecasts;

    public TripPlanner(Lookup<Trip.Flight> flights, Lookup<Trip.Hotel> hotels, Lookup<Trip.Forecast> forecasts) {
        this.flights = Objects.requireNonNull(flights, "flights");
        this.hotels = Objects.requireNonNull(hotels, "hotels");
        this.forecasts = Objects.requireNonNull(forecasts, "forecasts");
    }

    public Trip plan(String destination) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            Subtask<Trip.Flight> flight = scope.fork(() -> flights.find(destination));      // each on its own
            Subtask<Trip.Hotel> hotel = scope.fork(() -> hotels.find(destination));         // virtual thread
            Subtask<Trip.Forecast> forecast = scope.fork(() -> forecasts.find(destination));
            scope.join();                        // waits for all; throws on the first failure
            return new Trip(flight.get(), hotel.get(), forecast.get());
        }                                        // close(): every subtask has finished here
    }
}
