package io.github.aliturgutbozkurt.patterns.m03.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Point;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Shape;
import java.util.Objects;

/**
 * Reference solution for assignment 02.
 *
 * @see "m03 lesson, section Prototype"
 */
public class Rect implements Shape {

    private Point corner;
    private final int width;
    private final int height;

    public Rect(Point corner, int width, int height) {
        this.corner = Objects.requireNonNull(corner, "corner");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("size must be positive: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public Point position() {
        return corner;
    }

    @Override
    public void moveBy(int dx, int dy) {
        corner = corner.moved(dx, dy);
    }

    @Override
    public Shape copy() {
        return new Rect(corner, width, height);
    }

    @Override
    public String describe() {
        return "rect " + width + "x" + height + " at (" + corner.x() + "," + corner.y() + ")";
    }
}
