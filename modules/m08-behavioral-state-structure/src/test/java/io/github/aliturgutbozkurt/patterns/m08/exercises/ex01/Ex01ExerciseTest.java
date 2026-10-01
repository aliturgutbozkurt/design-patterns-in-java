package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected DocumentWorkflow newWorkflow(String author, int requiredApprovals) {
        return new ReviewWorkflow(author, requiredApprovals);
    }
}
