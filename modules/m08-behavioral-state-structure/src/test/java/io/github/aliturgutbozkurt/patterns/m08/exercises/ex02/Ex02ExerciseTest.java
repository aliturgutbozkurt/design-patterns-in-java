package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected Language newLanguage() {
        return new MiniLanguage();
    }
}
