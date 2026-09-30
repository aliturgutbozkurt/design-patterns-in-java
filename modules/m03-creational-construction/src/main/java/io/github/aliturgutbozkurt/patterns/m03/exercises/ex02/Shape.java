package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

/** GIVEN — do not modify. A mutable shape that can copy itself (the prototype interface). */
public interface Shape {

    Point position();

    void moveBy(int dx, int dy);

    /** An independent copy: changing the copy never changes this shape, and vice versa. */
    Shape copy();

    /** E.g. {@code circle r=5 at (1,2)}, {@code rect 3x4 at (0,0)}, {@code group[circle r=5 at (1,2), …]}. */
    String describe();
}
