package io.github.aliturgutbozkurt.patterns.m06.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex02.TreeTools;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected TreeTools tools() {
        return new DefaultTreeTools();
    }
}
