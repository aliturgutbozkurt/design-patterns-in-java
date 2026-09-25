package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/** A triangle given by its three side lengths. */
public record Triangle(double a, double b, double c) implements Shape {

    public Triangle {
        if (a <= 0 || b <= 0 || c <= 0 || a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException("sides violate the triangle inequality: " + a + ", " + b + ", " + c);
        }
    }

    /** Heron's formula. */
    @Override
    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
