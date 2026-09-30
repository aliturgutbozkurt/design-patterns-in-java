package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after;

/**
 * An immutable square — a sibling of {@link Rectangle}, not a subtype, so neither can break the other's contract.
 *
 * @see "m01 lesson, section LSP"
 */
public record Square(double side) implements Shape {

    public Square {
        Shape.requirePositive("side", side);
    }

    public Square withSide(double newSide) {
        return new Square(newSide);
    }

    @Override
    public double area() {
        return side * side;
    }

    @Override
    public double perimeter() {
        return 4 * side;
    }
}
