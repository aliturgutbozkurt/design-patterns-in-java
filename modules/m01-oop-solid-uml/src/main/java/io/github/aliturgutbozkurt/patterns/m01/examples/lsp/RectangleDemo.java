package io.github.aliturgutbozkurt.patterns.m01.examples.lsp;

import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Rectangle;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Shape;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Square;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.RectangleClient;
import java.util.List;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/lsp/RectangleDemo.java} */
public final class RectangleDemo {

    private RectangleDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: Square extends a mutable Rectangle ==");
        var rectangle = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Rectangle(2, 3);
        System.out.println("Rectangle resized to 5x4 -> area " + RectangleClient.resizeTo5By4(rectangle));
        var square = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Square(3);
        System.out.println("Square resized to 5x4 -> area " + RectangleClient.resizeTo5By4(square)
                + " (a Rectangle client expected 20.0: LSP broken)");
        try {
            new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Square(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("new Square(-1) -> " + e.getMessage());
        }

        System.out.println("== after: immutable shapes behind a sealed interface ==");
        var original = new Rectangle(2, 3);
        System.out.println(original + ".withWidth(5) -> " + original.withWidth(5));
        System.out.println("original is still " + original);
        for (Shape shape : List.<Shape>of(original, new Square(4))) {
            System.out.println(shape + ": area " + shape.area() + ", perimeter " + shape.perimeter());
        }
    }
}
