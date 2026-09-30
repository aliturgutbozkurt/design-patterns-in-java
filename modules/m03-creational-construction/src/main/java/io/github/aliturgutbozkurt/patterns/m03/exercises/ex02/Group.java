package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

import java.util.List;

/** Assignment 02 — a group of shapes (at least one); its copy must be <em>deep</em>. */
public class Group implements Shape {

    public Group(List<Shape> children) {
        // TODO(ex02): reject an empty list; keep your own list.
    }

    @Override
    public Point position() {
        throw new UnsupportedOperationException("TODO(ex02): the first child's position");
    }

    @Override
    public void moveBy(int dx, int dy) {
        throw new UnsupportedOperationException("TODO(ex02): move every child");
    }

    @Override
    public Shape copy() {
        throw new UnsupportedOperationException("TODO(ex02): copy every child, too");
    }

    @Override
    public String describe() {
        throw new UnsupportedOperationException("TODO(ex02): \"group[\" + children joined with \", \" + \"]\"");
    }
}
