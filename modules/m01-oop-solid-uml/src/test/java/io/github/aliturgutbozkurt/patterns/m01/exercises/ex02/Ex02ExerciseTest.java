package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.time.Clock;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m01-oop-solid-uml test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected LoanStore store() {
        return new InMemoryLoanStore();
    }

    @Override
    protected LoanService service(LoanStore store, Notifier notifier, Clock clock) {
        return new LibraryLoanService(store, notifier, clock);
    }
}
