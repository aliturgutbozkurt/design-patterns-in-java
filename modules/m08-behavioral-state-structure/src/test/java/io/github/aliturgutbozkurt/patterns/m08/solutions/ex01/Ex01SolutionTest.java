package io.github.aliturgutbozkurt.patterns.m08.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentWorkflow;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Ex01Contract;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected DocumentWorkflow newWorkflow(String author, int requiredApprovals) {
        return new ReviewWorkflow(author, requiredApprovals);
    }
}
