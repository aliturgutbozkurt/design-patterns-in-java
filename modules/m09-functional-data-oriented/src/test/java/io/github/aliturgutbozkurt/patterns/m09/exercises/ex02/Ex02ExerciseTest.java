package io.github.aliturgutbozkurt.patterns.m09.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m09-functional-data-oriented test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected SignupPipeline newPipeline(UserRegistry registry) {
        return new DefaultSignupPipeline(registry);
    }
}
