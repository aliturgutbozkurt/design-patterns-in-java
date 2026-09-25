package io.github.aliturgutbozkurt.patterns.sample.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/_sample test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected WordCounter newCounter() {
        return new SimpleWordCounter();
    }
}
