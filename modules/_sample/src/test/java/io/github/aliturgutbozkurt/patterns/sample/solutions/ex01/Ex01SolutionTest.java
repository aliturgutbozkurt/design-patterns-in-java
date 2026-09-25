package io.github.aliturgutbozkurt.patterns.sample.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.sample.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.sample.exercises.ex01.WordCounter;

/** Runs the contract against the reference solution (part of the default build / CI). */
class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected WordCounter newCounter() {
        return new SimpleWordCounter();
    }
}
