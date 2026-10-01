package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.AsciiRenderer;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Circle;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Rectangle;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Renderer;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Shape;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.SvgRenderer;
import java.util.List;
import java.util.function.Function;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/bridge/ShapesDemo.java}
 *
 * @see "m05 lesson, section Bridge"
 */
public final class ShapesDemo {

    private ShapesDemo() {}

    public static void main(String[] args) {
        List<Renderer> renderers = List.of(new SvgRenderer(), new AsciiRenderer());
        List<Function<Renderer, Shape>> shapes = List.of(
                renderer -> new Circle(renderer, 5, 5, 2),
                renderer -> new Rectangle(renderer, 0, 0, 6, 3));

        for (Function<Renderer, Shape> shape : shapes) {
            for (Renderer renderer : renderers) {
                Shape drawn = shape.apply(renderer);   // any shape composed with any renderer
                System.out.println(drawn.getClass().getSimpleName() + " with "
                        + renderer.getClass().getSimpleName() + ":");
                String picture = drawn.draw();
                System.out.print(picture.endsWith("\n") ? picture : picture + "\n");
            }
        }
        System.out.println(shapes.size() + " shapes x " + renderers.size() + " renderers = "
                + shapes.size() * renderers.size() + " combinations from 2 + 2 classes");
    }
}
