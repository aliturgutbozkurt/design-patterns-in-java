package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Catalogue row: Factory. A factory class hierarchy became a map from names to {@link Supplier}s filled with
 * constructor references.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class FactoryRow {

    interface Shape {
        String describe();
    }

    record Circle() implements Shape {
        public String describe() { return "a circle"; }
    }

    record Square() implements Shape {
        public String describe() { return "a square"; }
    }

    static final List<String> REQUESTS = List.of("circle", "square", "circle");

    private FactoryRow() {}

    /**
     * Before: one factory subclass per product, chosen by a static lookup.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Classic {

        abstract static class ShapeFactory {
            abstract Shape create();

            static ShapeFactory forName(String name) {
                return switch (name) {
                    case "circle" -> new CircleFactory();
                    case "square" -> new SquareFactory();
                    default -> throw new IllegalArgumentException("unknown shape: " + name);
                };
            }
        }

        static final class CircleFactory extends ShapeFactory {
            Shape create() { return new Circle(); }
        }

        static final class SquareFactory extends ShapeFactory {
            Shape create() { return new Square(); }
        }

        private Classic() {}

        public static String run() {
            return REQUESTS.stream().map(n -> ShapeFactory.forName(n).create().describe()).toList().toString();
        }
    }

    /**
     * After: the registry is data; constructor references are the factories.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Modern {

        static final Map<String, Supplier<Shape>> SHAPES = Map.of("circle", Circle::new, "square", Square::new);

        private Modern() {}

        static Shape create(String name) {
            return Optional.ofNullable(SHAPES.get(name))
                    .orElseThrow(() -> new IllegalArgumentException("unknown shape: " + name))
                    .get();
        }

        public static String run() {
            return REQUESTS.stream().map(n -> create(n).describe()).toList().toString();
        }
    }
}
