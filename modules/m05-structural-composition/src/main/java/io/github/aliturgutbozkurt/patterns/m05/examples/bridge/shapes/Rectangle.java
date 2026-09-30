package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes;

/**
 * Refined abstraction: a rectangle, drawn through the renderer's {@code rectangle} primitive.
 *
 * @see "m05 lesson, section Bridge"
 */
public final class Rectangle extends Shape {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Rectangle(Renderer renderer, int x, int y, int width, int height) {
        super(renderer);
        this.x = x;
        this.y = y;
        this.width = positive("width", width);
        this.height = positive("height", height);
    }

    @Override
    public String draw() {
        return renderer.rectangle(x, y, width, height);
    }
}
