package io.github.aliturgutbozkurt.patterns.m05.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.MenuQueries;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected MenuQueries queries() {
        return new MenuReport();
    }
}
