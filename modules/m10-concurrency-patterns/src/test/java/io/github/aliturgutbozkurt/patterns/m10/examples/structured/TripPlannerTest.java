package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.Lookup;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.Trip;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.TripPlanner;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.travel.TripPlannerWithDeadline;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Preview API (JEP 533): compiled and run with {@code --enable-preview} by this module's POM. */
@Timeout(10)
class TripPlannerTest {

    private static final Trip.Flight FLIGHT = new Trip.Flight("TP 1234");
    private static final Trip.Hotel HOTEL = new Trip.Hotel("Casa Azul");
    private static final Trip.Forecast FORECAST = new Trip.Forecast("sunny, 24 C");

    /** A look-up that blocks until it is interrupted, and counts down {@code interrupted} when that happens. */
    private static <T> Lookup<T> blockedUntilInterrupted(CountDownLatch interrupted) {
        return _ -> {
            try {
                new CountDownLatch(1).await();          // never opened: only cancellation ends this
                throw new AssertionError("unreachable");
            } catch (InterruptedException e) {
                interrupted.countDown();
                throw e;
            }
        };
    }

    @Test
    void allThreeLookupsSucceedAndTheTripIsBuiltFromTheirResults() throws Exception {
        var planner = new TripPlanner(_ -> FLIGHT, _ -> HOTEL, _ -> FORECAST);
        assertThat(planner.plan("Lisbon")).isEqualTo(new Trip(FLIGHT, HOTEL, FORECAST));
    }

    @Test
    void aFailingHotelCancelsTheScopeAndTheBlockedFlightLookupIsInterruptedBeforePlanReturns() {
        var flightInterrupted = new CountDownLatch(1);
        var noRooms = new IllegalStateException("no rooms in Lisbon");
        var planner = new TripPlanner(blockedUntilInterrupted(flightInterrupted), _ -> {
            throw noRooms;
        }, _ -> FORECAST);

        assertThatThrownBy(() -> planner.plan("Lisbon")).isInstanceOf(ExecutionException.class).hasCause(noRooms);
        assertThat(flightInterrupted.getCount())         // no waiting: close() already waited for the subtask
                .as("flight subtask saw its interrupt before plan() returned").isZero();
    }

    @Test
    void theCancelledSiblingStaysUnavailable() throws InterruptedException {
        var interrupted = new CountDownLatch(1);
        Lookup<Trip.Flight> blocked = blockedUntilInterrupted(interrupted);
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.<Object>allSuccessfulOrThrow())) {
            var flight = scope.fork(() -> blocked.find("Lisbon"));
            scope.fork(() -> {
                throw new IllegalStateException("hotel down");
            });
            assertThatThrownBy(scope::join).isInstanceOf(ExecutionException.class);
            assertThat(scope.isCancelled()).isTrue();
            assertThat(flight.state()).isEqualTo(StructuredTaskScope.Subtask.State.UNAVAILABLE);
        }
        assertThat(interrupted.getCount()).isZero();
    }

    @Test
    void aDeadlineCancelsTheScopeWithCancelledByTimeoutException() {
        var weatherInterrupted = new CountDownLatch(1);
        var planner = new TripPlannerWithDeadline(_ -> FLIGHT, _ -> HOTEL,
                blockedUntilInterrupted(weatherInterrupted), Duration.ofMillis(50));
        assertThatThrownBy(() -> planner.plan("Lisbon")).isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(StructuredTaskScope.CancelledByTimeoutException.class);
        assertThat(weatherInterrupted.getCount()).isZero();
    }

    @Test
    void withARoomyDeadlineThePlannerWithDeadlineBuildsTheTrip() throws Exception {
        var planner = new TripPlannerWithDeadline(_ -> FLIGHT, _ -> HOTEL, _ -> FORECAST, Duration.ofSeconds(5));
        assertThat(planner.plan("Lisbon")).isEqualTo(new Trip(FLIGHT, HOTEL, FORECAST));
    }

    @Test
    void subtasksRunOnVirtualThreads() throws Exception {
        var virtual = new AtomicBoolean();
        var planner = new TripPlanner(_ -> {
            virtual.set(Thread.currentThread().isVirtual());
            return FLIGHT;
        }, _ -> HOTEL, _ -> FORECAST);
        planner.plan("Lisbon");
        assertThat(virtual).isTrue();
    }

    @Test
    void demoPrintsTheTripTheFailureAndTheTimeout() {
        assertThat(Demos.output(() -> TripPlannerDemo.main(new String[0]))).isEqualTo("""
                plan(Lisbon) -> Trip[flight=Flight[number=TP 1234], hotel=Hotel[name=Casa Azul], \
                forecast=Forecast[summary=sunny, 24 C]]
                hotel service down -> ExecutionException caused by IllegalStateException: no rooms in Lisbon
                flight look-up was interrupted before plan() returned: true
                weather service hangs, deadline 50 ms -> ExecutionException caused by CancelledByTimeoutException
                """);
    }
}
