package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/** A circle; radius 0 is allowed and describes a point. */
public record Circle(double radius) implements Shape {

    public Circle {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must not be negative: " + radius);
        }
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
