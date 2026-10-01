package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m11-architecture-enterprise test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected OrderLifecycle newLifecycle(OrderStore store, Supplier<OrderId> ids,
                                          Consumer<RuntimeException> errorHandler) {
        return new OrderLifecycleService(store, ids, errorHandler);
    }
}
