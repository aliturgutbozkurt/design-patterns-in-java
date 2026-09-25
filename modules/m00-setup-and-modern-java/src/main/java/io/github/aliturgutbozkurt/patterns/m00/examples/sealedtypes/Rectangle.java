package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/** An axis-aligned rectangle with positive sides. */
public record Rectangle(double width, double height) implements Shape {

    public Rectangle {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("sides must be positive: " + width + " × " + height);
        }
    }

    @Override
    public double area() {
        return width * height;
    }
}
