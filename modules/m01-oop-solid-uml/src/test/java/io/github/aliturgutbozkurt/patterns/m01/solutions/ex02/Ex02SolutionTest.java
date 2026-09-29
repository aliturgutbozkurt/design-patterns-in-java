package io.github.aliturgutbozkurt.patterns.m01.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.LoanService;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.LoanStore;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Notifier;
import java.time.Clock;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected LoanStore store() {
        return new InMemoryLoanStore();
    }

    @Override
    protected LoanService service(LoanStore store, Notifier notifier, Clock clock) {
        return new LibraryLoanService(store, notifier, clock);
    }
}
