package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.Lookup;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.Trip;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.TripPlanner;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.TripPlannerWithDeadline;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Run (preview API, JEP 533): {@code java --enable-preview --source 27 modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/structured/TripPlannerDemo.java}
 *
 * <p>The failing and the hanging look-ups are simulated with latches, so every run prints the same lines.
 */
public final class TripPlannerDemo {

    private TripPlannerDemo() {}

    public static void main(String[] args) throws InterruptedException {
        Lookup<Trip.Flight> flights = _ -> new Trip.Flight("TP 1234");
        Lookup<Trip.Hotel> hotels = _ -> new Trip.Hotel("Casa Azul");
        Lookup<Trip.Forecast> weather = _ -> new Trip.Forecast("sunny, 24 C");

        try {
            System.out.println("plan(Lisbon) -> " + new TripPlanner(flights, hotels, weather).plan("Lisbon"));
        } catch (ExecutionException e) {
            throw new IllegalStateException("unexpected failure", e);
        }

        var flightStarted = new CountDownLatch(1);
        var flightInterrupted = new CountDownLatch(1);
        Lookup<Trip.Flight> slowFlights = destination -> {
            flightStarted.countDown();
            try {
                new CountDownLatch(1).await();                  // hangs until the scope cancels it
                return new Trip.Flight("never");
            } catch (InterruptedException e) {
                flightInterrupted.countDown();
                throw e;
            }
        };
        Lookup<Trip.Hotel> noRooms = destination -> {
            flightStarted.await();                              // fail once the flight look-up is running
            throw new IllegalStateException("no rooms in " + destination);
        };
        try {
            new TripPlanner(slowFlights, noRooms, weather).plan("Lisbon");
        } catch (ExecutionException e) {
            System.out.println("hotel service down -> ExecutionException caused by "
                    + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
            System.out.println("flight look-up was interrupted before plan() returned: "
                    + (flightInterrupted.getCount() == 0));
        }

        Lookup<Trip.Forecast> hangingWeather = _ -> {
            new CountDownLatch(1).await();
            return new Trip.Forecast("never");
        };
        try {
            new TripPlannerWithDeadline(flights, hotels, hangingWeather, Duration.ofMillis(50)).plan("Lisbon");
        } catch (ExecutionException e) {
            System.out.println("weather service hangs, deadline 50 ms -> ExecutionException caused by "
                    + e.getCause().getClass().getSimpleName());
        }
    }
}
