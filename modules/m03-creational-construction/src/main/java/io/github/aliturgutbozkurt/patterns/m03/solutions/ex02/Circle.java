package io.github.aliturgutbozkurt.patterns.m03.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Point;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Shape;
import java.util.Objects;

/** Reference solution for assignment 02: {@link Point} is immutable, so copying the reference is enough. */
public class Circle implements Shape {

    private Point centre;
    private final int radius;

    public Circle(Point centre, int radius) {
        this.centre = Objects.requireNonNull(centre, "centre");
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive: " + radius);
        }
        this.radius = radius;
    }

    @Override
    public Point position() {
        return centre;
    }

    @Override
    public void moveBy(int dx, int dy) {
        centre = centre.moved(dx, dy);
    }

    @Override
    public Shape copy() {
        return new Circle(centre, radius);
    }

    @Override
    public String describe() {
        return "circle r=" + radius + " at (" + centre.x() + "," + centre.y() + ")";
    }
}
