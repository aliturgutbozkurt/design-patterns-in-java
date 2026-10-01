package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m09-functional-data-oriented test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected Payroll newPayroll() {
        return new DataOrientedPayroll();
    }
}
