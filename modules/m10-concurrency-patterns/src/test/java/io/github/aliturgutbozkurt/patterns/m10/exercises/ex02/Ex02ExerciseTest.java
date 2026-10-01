package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

import java.util.concurrent.ThreadFactory;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m10-concurrency-patterns test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected JobQueue newQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory) {
        return new WorkerPoolQueue(capacity, workers, handler, threadFactory);
    }
}
