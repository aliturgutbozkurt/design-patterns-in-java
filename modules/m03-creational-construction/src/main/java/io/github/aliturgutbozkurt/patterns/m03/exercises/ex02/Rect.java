package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

/** Assignment 02 — a rectangle positioned by its top-left corner. */
public class Rect implements Shape {

    public Rect(Point corner, int width, int height) {
        // TODO(ex02)
    }

    @Override
    public Point position() {
        throw new UnsupportedOperationException("TODO(ex02): implement position()");
    }

    @Override
    public void moveBy(int dx, int dy) {
        throw new UnsupportedOperationException("TODO(ex02): implement moveBy(int, int)");
    }

    @Override
    public Shape copy() {
        throw new UnsupportedOperationException("TODO(ex02): implement copy()");
    }

    @Override
    public String describe() {
        throw new UnsupportedOperationException("TODO(ex02): implement describe(), e.g. \"rect 3x4 at (0,0)\"");
    }
}
