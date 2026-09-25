package io.github.aliturgutbozkurt.patterns.m00.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.FeeCalculator;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected FeeCalculator calculator() {
        return new PaymentFees();
    }
}
