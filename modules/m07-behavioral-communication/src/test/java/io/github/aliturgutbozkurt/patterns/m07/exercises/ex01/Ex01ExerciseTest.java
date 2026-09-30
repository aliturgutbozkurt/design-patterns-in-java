package io.github.aliturgutbozkurt.patterns.m07.exercises.ex01;

import java.util.function.Consumer;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m07-behavioral-communication test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected Auction newAuction(
            long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler) {
        return new LiveAuction(startingPriceCents, minIncrementCents, errorHandler);
    }
}
