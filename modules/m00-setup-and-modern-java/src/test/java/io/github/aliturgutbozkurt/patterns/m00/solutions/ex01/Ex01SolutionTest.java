package io.github.aliturgutbozkurt.patterns.m00.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m00.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex01.Temperature;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex01.Unit;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected Temperature of(double value, Unit unit) {
        return new TemperatureReading(value, unit);
    }
}
