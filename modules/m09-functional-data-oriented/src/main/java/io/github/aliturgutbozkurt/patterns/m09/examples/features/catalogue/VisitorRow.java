package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.List;
import java.util.Locale;

/**
 * Catalogue row: Visitor. {@code accept} plus a visitor interface (double dispatch) became sealed records and an
 * exhaustive {@code switch}; a new operation is a new function, and the data types stay untouched.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class VisitorRow {

    private VisitorRow() {}

    static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    /** Before: every shape accepts a visitor; every operation is a visitor class. */
    public static final class Classic {

        interface ShapeVisitor<R> {
            R visitCircle(Circle c);

            R visitRect(Rect r);
        }

        interface Shape {
            <R> R accept(ShapeVisitor<R> visitor);
        }

        record Circle(double radius) implements Shape {
            public <R> R accept(ShapeVisitor<R> visitor) { return visitor.visitCircle(this); }
        }

        record Rect(double width, double height) implements Shape {
            public <R> R accept(ShapeVisitor<R> visitor) { return visitor.visitRect(this); }
        }

        static final class Area implements ShapeVisitor<Double> {
            public Double visitCircle(Circle c) { return Math.PI * c.radius() * c.radius(); }

            public Double visitRect(Rect r) { return r.width() * r.height(); }
        }

        private Classic() {}

        public static String run() {
            List<Shape> shapes = List.of(new Circle(1), new Rect(2, 3));
            return shapes.stream().map(s -> format(s.accept(new Area()))).toList().toString();
        }
    }

    /** After: the data is a sealed hierarchy; each operation is one {@code switch}. */
    public static final class Modern {

        sealed interface Shape permits Circle, Rect {}

        record Circle(double radius) implements Shape {}

        record Rect(double width, double height) implements Shape {}

        private Modern() {}

        static double area(Shape shape) {
            return switch (shape) {
                case Circle(var r) -> Math.PI * r * r;
                case Rect(var w, var h) -> w * h;
            };
        }

        public static String run() {
            List<Shape> shapes = List.of(new Circle(1), new Rect(2, 3));
            return shapes.stream().map(s -> format(area(s))).toList().toString();
        }
    }
}
