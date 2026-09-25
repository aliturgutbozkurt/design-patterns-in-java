package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.List;
import java.util.Locale;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/sealedtypes/ShapeDemo.java} */
public final class ShapeDemo {

    private ShapeDemo() {}

    public static void main(String[] args) {
        List<Shape> shapes = List.of(new Circle(1), new Rectangle(2, 2), new Rectangle(2, 3), new Triangle(3, 4, 5), new Circle(0));
        for (Shape shape : shapes) {
            System.out.println(String.format(Locale.ROOT, "%s: area %.2f, perimeter %.2f",
                    Shapes.describe(shape), shape.area(), Shapes.perimeter(shape)));
        }
    }
}
