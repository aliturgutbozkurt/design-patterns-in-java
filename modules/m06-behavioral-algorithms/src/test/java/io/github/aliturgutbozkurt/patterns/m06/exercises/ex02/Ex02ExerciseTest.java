package io.github.aliturgutbozkurt.patterns.m06.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected TreeTools tools() {
        return new DefaultTreeTools();
    }
}
