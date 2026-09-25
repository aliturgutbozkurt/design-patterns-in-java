package io.github.aliturgutbozkurt.patterns.m00.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected Temperature of(double value, Unit unit) {
        return new TemperatureReading(value, unit);
    }
}
