package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m05-structural-composition test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected MenuQueries queries() {
        return new MenuReport();
    }
}
