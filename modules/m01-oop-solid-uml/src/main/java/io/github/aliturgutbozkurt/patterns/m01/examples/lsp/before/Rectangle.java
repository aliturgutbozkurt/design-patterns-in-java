package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before;

/**
 * A mutable rectangle. Its implicit contract: {@code setWidth} changes only the width, {@code setHeight} only the
 * height.
 *
 * @see "m01 lesson, section LSP"
 */
public class Rectangle {

    private double width;
    private double height;

    public Rectangle(double width, double height) {
        this.width = requirePositive("width", width);
        this.height = requirePositive("height", height);
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public void setWidth(double width) {
        this.width = requirePositive("width", width);
    }

    public void setHeight(double height) {
        this.height = requirePositive("height", height);
    }

    public double area() {
        return width * height;
    }

    static double requirePositive(String name, double value) {
        if (!(value > 0)) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
        return value;
    }
}
