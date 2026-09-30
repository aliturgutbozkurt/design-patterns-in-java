package io.github.aliturgutbozkurt.patterns.m02.exercises.ex01;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m02-creational-factories test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

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
