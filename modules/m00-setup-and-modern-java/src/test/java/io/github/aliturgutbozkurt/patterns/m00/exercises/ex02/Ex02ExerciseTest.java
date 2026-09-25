package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected FeeCalculator calculator() {
        return new PaymentFees();
    }
}
