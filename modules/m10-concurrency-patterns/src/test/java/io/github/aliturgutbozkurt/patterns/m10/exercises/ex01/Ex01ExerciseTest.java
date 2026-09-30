package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m10-concurrency-patterns test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

    @Override
    protected PriceComparator comparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy,
            ExecutorService executor) {
        return new ParallelPriceComparator(providers, deadline, policy, executor);
    }
}
