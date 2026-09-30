package io.github.aliturgutbozkurt.patterns.m03.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Assignment 01 — travel booking builder. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract BookingBuilder newBuilder();

    private static final LocalDate NOV_2 = LocalDate.of(2026, 11, 2);
    private static final LocalDate NOV_9 = LocalDate.of(2026, 11, 9);

    private BookingBuilder oneWay() {
        return newBuilder().traveller("Ada").from("IST").to("ESB").departure(NOV_2);
    }

    @Test
    void buildsWithDefaults() {
        Booking booking = oneWay().build();
        assertThat(booking.traveller()).isEqualTo("Ada");
        assertThat(booking.returnTrip()).isEmpty();
        assertThat(booking.passengers()).isEqualTo(1);
        assertThat(booking.cabin()).isEqualTo(CabinClass.ECONOMY);
        assertThat(booking.extras()).isEmpty();
    }

    @Test
    void buildsAReturnTrip() {
        Booking booking = oneWay().returnDate(NOV_9).passengers(2).cabin(CabinClass.BUSINESS)
                .extra("meal").extra("wifi").build();
        assertThat(booking.returnTrip()).contains(NOV_9);
        assertThat(booking.passengers()).isEqualTo(2);
        assertThat(booking.cabin()).isEqualTo(CabinClass.BUSINESS);
        assertThat(booking.extras()).containsExactlyInAnyOrder("meal", "wifi");
    }

    @Test
    void missingRequiredFieldsAreAllReported() {
        assertThatIllegalStateException().isThrownBy(() -> newBuilder().passengers(0).build())
                .withMessageContaining("traveller")
                .withMessageContaining("from")
                .withMessageContaining("to")
                .withMessageContaining("departure")
                .withMessageContaining("passengers");
    }

    @Test
    void rejectsSameOriginAndDestination() {
        assertThatIllegalStateException()
                .isThrownBy(() -> newBuilder().traveller("Ada").from("IST").to("ist").departure(NOV_2).build())
                .withMessageContaining("must differ");
    }

    @Test
    void rejectsReturnBeforeDeparture() {
        assertThatIllegalStateException().isThrownBy(() -> oneWay().returnDate(NOV_2).build())
                .withMessageContaining("after departure");
        assertThatIllegalStateException().isThrownBy(() -> oneWay().returnDate(NOV_2.minusDays(1)).build())
                .withMessageContaining("after departure");
    }

    @Test
    void rejectsPassengersOutsideOneToNine() {
        assertThatIllegalStateException().isThrownBy(() -> oneWay().passengers(0).build())
                .withMessageContaining("passengers");
        assertThatIllegalStateException().isThrownBy(() -> oneWay().passengers(10).build())
                .withMessageContaining("passengers");
        assertThat(oneWay().passengers(9).build().passengers()).isEqualTo(9);
    }

    @Test
    void extrasAreImmutableAndIndependentOfTheBuilder() {
        BookingBuilder builder = oneWay().extra("meal");
        Booking booking = builder.build();
        builder.extra("lounge");
        assertThat(booking.extras()).containsExactly("meal");
        assertThatThrownBy(() -> booking.extras().add("x")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void builderCanBuildSeveralIndependentBookings() {
        BookingBuilder builder = oneWay();
        Booking single = builder.build();
        Booking family = builder.passengers(4).build();
        assertThat(single.passengers()).isEqualTo(1);
        assertThat(family.passengers()).isEqualTo(4);
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> newBuilder().traveller(null));
        assertThatNullPointerException().isThrownBy(() -> newBuilder().from(null));
        assertThatNullPointerException().isThrownBy(() -> newBuilder().departure(null));
        assertThatNullPointerException().isThrownBy(() -> newBuilder().cabin(null));
        assertThatNullPointerException().isThrownBy(() -> newBuilder().extra(null));
    }
}
