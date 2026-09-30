package io.github.aliturgutbozkurt.patterns.m02.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex01.Color;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex01.Ex01Contract;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected Color rgb(int red, int green, int blue) {
        return RgbColor.rgb(red, green, blue);
    }

    @Override
    protected Color hex(String text) {
        return RgbColor.hex(text);
    }

    @Override
    protected Color named(String name) {
        return RgbColor.named(name);
    }

    @Override
    protected Class<? extends Color> colorClass() {
        return RgbColor.class;
    }
}
