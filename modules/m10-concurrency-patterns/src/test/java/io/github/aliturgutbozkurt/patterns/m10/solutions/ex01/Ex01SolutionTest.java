package io.github.aliturgutbozkurt.patterns.m10.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.FailurePolicy;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.PriceComparator;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex01.PriceProvider;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected PriceComparator comparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy,
            ExecutorService executor) {
        return new ParallelPriceComparator(providers, deadline, policy, executor);
    }
}
