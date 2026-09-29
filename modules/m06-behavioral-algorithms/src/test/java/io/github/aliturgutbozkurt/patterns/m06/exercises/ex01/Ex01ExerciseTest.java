package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected Editor newEditor(String initialText, int maxHistory) {
        return new CommandEditor(initialText, maxHistory);
    }
}
