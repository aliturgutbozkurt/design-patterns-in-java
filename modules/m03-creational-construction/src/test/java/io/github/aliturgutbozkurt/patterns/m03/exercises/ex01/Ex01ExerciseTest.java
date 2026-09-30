package io.github.aliturgutbozkurt.patterns.m03.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m03-creational-construction test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected BookingBuilder newBuilder() {
        return new DefaultBookingBuilder();
    }
}
