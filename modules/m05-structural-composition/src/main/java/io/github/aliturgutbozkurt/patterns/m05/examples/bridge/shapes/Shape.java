package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

import java.util.Objects;

/**
 * Bridge abstraction: a shape <em>has</em> a renderer instead of <em>being</em> an SVG or ASCII shape, so shapes and
 * formats vary independently — N + M classes instead of N × M subclasses.
 *
 * @see "m05 lesson, section Bridge"
 */
public abstract class Shape {

    protected final Renderer renderer;

    protected Shape(Renderer renderer) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    /** Draws this shape with whatever renderer it was given. */
    public abstract String draw();

    protected static int positive(String what, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(what + " must be positive: " + value);
        }
        return value;
    }
}
