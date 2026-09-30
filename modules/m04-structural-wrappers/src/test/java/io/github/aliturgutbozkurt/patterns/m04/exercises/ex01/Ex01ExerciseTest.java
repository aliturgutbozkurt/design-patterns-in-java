package io.github.aliturgutbozkurt.patterns.m04.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m04-structural-wrappers test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected DataSource compression(DataSource wrapped) {
        return new CompressionDecorator(wrapped);
    }

    @Override
    protected DataSource base64(DataSource wrapped) {
        return new Base64Decorator(wrapped);
    }
}
