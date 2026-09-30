package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

import java.util.List;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m03-creational-construction test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected Shape circle(Point centre, int radius) {
        return new Circle(centre, radius);
    }

    @Override
    protected Shape rect(Point corner, int width, int height) {
        return new Rect(corner, width, height);
    }

    @Override
    protected Shape group(List<Shape> children) {
        return new Group(children);
    }

    @Override
    protected ShapeRegistry registry() {
        return new TemplateRegistry();
    }
}
