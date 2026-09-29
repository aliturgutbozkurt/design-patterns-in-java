package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

/** Assignment 02 — see assignments/02-shape-prototypes.en.md (Türkçe: 02-shape-prototypes.tr.md). */
public class Circle implements Shape {

    public Circle(Point centre, int radius) {
        // TODO(ex02): keep the (mutable) position and the radius.
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
        throw new UnsupportedOperationException("TODO(ex02): implement describe(), e.g. \"circle r=5 at (1,2)\"");
    }
}
