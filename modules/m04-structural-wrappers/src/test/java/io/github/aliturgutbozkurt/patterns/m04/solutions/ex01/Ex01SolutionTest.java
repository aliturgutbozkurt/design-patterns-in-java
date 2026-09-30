package io.github.aliturgutbozkurt.patterns.m04.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex01.DataSource;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex01.Ex01Contract;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected DataSource compression(DataSource wrapped) {
        return new CompressionDecorator(wrapped);
    }

    @Override
    protected DataSource base64(DataSource wrapped) {
        return new Base64Decorator(wrapped);
    }
}
