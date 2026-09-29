package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

/**
 * Refined abstraction: a circle, drawn through the renderer's {@code circle} primitive.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class Circle extends Shape {

    private final int x;
    private final int y;
    private final int radius;

    public Circle(Renderer renderer, int x, int y, int radius) {
        super(renderer);
        this.x = x;
        this.y = y;
        this.radius = positive("radius", radius);
    }

    @Override
    public String draw() {
        return renderer.circle(x, y, radius);
    }
}
