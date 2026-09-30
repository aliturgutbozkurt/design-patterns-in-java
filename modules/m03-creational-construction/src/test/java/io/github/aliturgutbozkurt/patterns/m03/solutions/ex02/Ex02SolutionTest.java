package io.github.aliturgutbozkurt.patterns.m03.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Point;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Shape;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.ShapeRegistry;
import java.util.List;

class Ex02SolutionTest extends Ex02Contract {

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
