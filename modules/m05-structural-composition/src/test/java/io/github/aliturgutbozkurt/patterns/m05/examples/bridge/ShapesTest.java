package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.AsciiRenderer;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Circle;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Rectangle;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Renderer;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.Shape;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.shapes.SvgRenderer;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShapesTest {

    /** A third implementor written only for the test: {@link Shape} needs no change to use it. */
    static final class RecordingRenderer implements Renderer {
        final List<String> calls = new ArrayList<>();

        @Override
        public String circle(int x, int y, int radius) {
            calls.add("circle(" + x + ", " + y + ", " + radius + ")");
            return "";
        }

        @Override
        public String rectangle(int x, int y, int width, int height) {
            calls.add("rectangle(" + x + ", " + y + ", " + width + ", " + height + ")");
            return "";
        }
    }

    @Test
    void circleOnSvg() {
        assertThat(new Circle(new SvgRenderer(), 5, 5, 2).draw()).isEqualTo("<circle cx=\"5\" cy=\"5\" r=\"2\"/>");
    }

    @Test
    void rectangleOnSvg() {
        assertThat(new Rectangle(new SvgRenderer(), 0, 1, 4, 3).draw())
                .isEqualTo("<rect x=\"0\" y=\"1\" width=\"4\" height=\"3\"/>");
    }

    @Test
    void circleOnAscii() {
        assertThat(new Circle(new AsciiRenderer(), 5, 5, 2).draw()).isEqualTo("""
                  *
                 ***
                *****
                 ***
                  *
                """);
    }

    @Test
    void rectangleOnAscii() {
        assertThat(new Rectangle(new AsciiRenderer(), 0, 1, 4, 3).draw()).isEqualTo("""
                +--+
                |  |
                +--+
                """);
    }

    @Test
    void shapesDelegatePrimitiveCallsToTheirRenderer() {
        var recorder = new RecordingRenderer();
        List<Shape> shapes = List.of(new Circle(recorder, 1, 2, 3), new Rectangle(recorder, 4, 5, 6, 7));
        shapes.forEach(Shape::draw);
        assertThat(recorder.calls).containsExactly("circle(1, 2, 3)", "rectangle(4, 5, 6, 7)");
    }

    @Test
    void rejectsInvalidShapes() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Circle(new SvgRenderer(), 0, 0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Rectangle(new SvgRenderer(), 0, 0, 1, -1));
        assertThatThrownBy(() -> new Circle(null, 0, 0, 1)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void demoDrawsEveryShapeWithEveryRenderer() {
        assertThat(Console.capture(() -> ShapesDemo.main(new String[0]))).isEqualTo("""
                Circle with SvgRenderer:
                <circle cx="5" cy="5" r="2"/>
                Circle with AsciiRenderer:
                  *
                 ***
                *****
                 ***
                  *
                Rectangle with SvgRenderer:
                <rect x="0" y="0" width="6" height="3"/>
                Rectangle with AsciiRenderer:
                +----+
                |    |
                +----+
                2 shapes x 2 renderers = 4 combinations from 2 + 2 classes
                """);
    }
}
