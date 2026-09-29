package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after;

/**
 * An immutable rectangle; "changing" it returns a new value.
 *
 * @see "m01 lesson, section LSP"
 */
public record Rectangle(double width, double height) implements Shape {

    public Rectangle {
        Shape.requirePositive("width", width);
        Shape.requirePositive("height", height);
    }

    public Rectangle withWidth(double newWidth) {
        return new Rectangle(newWidth, height);
    }

    public Rectangle withHeight(double newHeight) {
        return new Rectangle(width, newHeight);
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }
}
