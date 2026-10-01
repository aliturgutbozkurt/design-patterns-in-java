package io.github.aliturgutbozkurt.patterns.m03.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Point;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Shape;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reference solution for assignment 02: a deep copy copies every child — recursively, through nested groups.
 *
 * @see "m03 lesson, section Prototype"
 */
public class Group implements Shape {

    private final List<Shape> children;

    public Group(List<Shape> children) {
        if (children.isEmpty()) {
            throw new IllegalArgumentException("a group needs at least one shape");
        }
        this.children = List.copyOf(children);
    }

    @Override
    public Point position() {
        return children.getFirst().position();
    }

    @Override
    public void moveBy(int dx, int dy) {
        children.forEach(child -> child.moveBy(dx, dy));
    }

    @Override
    public Shape copy() {
        return new Group(children.stream().map(Shape::copy).toList());
    }

    @Override
    public String describe() {
        return children.stream().map(Shape::describe).collect(Collectors.joining(", ", "group[", "]"));
    }
}
