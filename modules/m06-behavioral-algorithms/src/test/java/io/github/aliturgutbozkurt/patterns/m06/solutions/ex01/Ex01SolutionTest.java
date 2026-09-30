package io.github.aliturgutbozkurt.patterns.m06.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Editor;
import io.github.aliturgutbozkurt.patterns.m06.exercises.ex01.Ex01Contract;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected Editor newEditor(String initialText, int maxHistory) {
        return new CommandEditor(initialText, maxHistory);
    }
}
