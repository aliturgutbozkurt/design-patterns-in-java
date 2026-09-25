package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/**
 * Operations written <em>outside</em> the hierarchy with pattern matching (data-oriented style). Adding a new
 * operation needs no change to the shapes; adding a new shape makes these switches fail to compile until handled.
 */
public final class Shapes {

    private Shapes() {}

    public static double perimeter(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> 2 * Math.PI * r;
            case Rectangle(double w, double h) -> 2 * (w + h);
            case Triangle(double a, double b, double c) -> a + b + c;
        };
    }

    public static String describe(Shape shape) {
        return switch (shape) {
            case Circle(double r) when r == 0 -> "a point";
            case Circle(double r) -> "a circle with radius " + r;
            case Rectangle(double w, double h) when w == h -> "a " + w + " × " + h + " square";
            case Rectangle(double w, double h) -> "a " + w + " × " + h + " rectangle";
            case Triangle _ -> "a triangle";
        };
    }
}
