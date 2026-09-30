package io.github.aliturgutbozkurt.patterns.m03.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex01.BookingBuilder;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex01.Ex01Contract;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected BookingBuilder newBuilder() {
        return new DefaultBookingBuilder();
    }
}
