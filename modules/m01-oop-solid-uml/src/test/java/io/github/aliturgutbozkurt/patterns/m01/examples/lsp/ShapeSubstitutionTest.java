package io.github.aliturgutbozkurt.patterns.m01.examples.lsp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Rectangle;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Shape;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after.Square;
import io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.RectangleClient;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShapeSubstitutionTest {

    @Test
    void beforeClientWorksForARectangle() {
        var rectangle = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Rectangle(2, 3);
        assertThat(RectangleClient.resizeTo5By4(rectangle)).isEqualTo(20.0);
    }

    /** Documents the violation on purpose: the subtype silently changes what the client observes. */
    @Test
    void beforeSquareBreaksTheRectangleContract() {
        var square = new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Square(3);
        assertThat(RectangleClient.resizeTo5By4(square)).isEqualTo(16.0).isNotEqualTo(20.0);
    }

    @Test
    void beforeSquareValidatesItsSideBeforeCallingSuper() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Square(-1))
                .withMessage("side must be positive: -1.0");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new io.github.aliturgutbozkurt.patterns.m01.examples.lsp.before.Rectangle(0, 1))
                .withMessage("width must be positive: 0.0");
    }

    @Test
    void afterWithWidthReturnsANewRectangleAndLeavesTheOriginal() {
        var original = new Rectangle(2, 3);
        var wider = original.withWidth(5);
        assertThat(wider).isEqualTo(new Rectangle(5, 3));
        assertThat(original).isEqualTo(new Rectangle(2, 3));
        assertThat(original.withHeight(4)).isEqualTo(new Rectangle(2, 4));
        assertThat(new Square(2).withSide(5)).isEqualTo(new Square(5));
    }

    @Test
    void afterEveryShapeIsUsableThroughTheInterface() {
        List<Shape> shapes = List.of(new Rectangle(2, 3), new Square(4));
        assertThat(shapes.stream().mapToDouble(Shape::area).sum()).isEqualTo(22.0);
        assertThat(shapes.stream().mapToDouble(Shape::perimeter).sum()).isEqualTo(26.0);
    }

    @Test
    void afterShapesRejectNonPositiveSides() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Rectangle(0, 1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Rectangle(1, Double.NaN));
        assertThatIllegalArgumentException().isThrownBy(() -> new Square(-2));
    }

    @Test
    void demoShowsTheViolationAndTheFix() {
        assertThat(Console.capture(() -> RectangleDemo.main(new String[0]))).isEqualTo("""
                == before: Square extends a mutable Rectangle ==
                Rectangle resized to 5x4 -> area 20.0
                Square resized to 5x4 -> area 16.0 (a Rectangle client expected 20.0: LSP broken)
                new Square(-1) -> side must be positive: -1.0
                == after: immutable shapes behind a sealed interface ==
                Rectangle[width=2.0, height=3.0].withWidth(5) -> Rectangle[width=5.0, height=3.0]
                original is still Rectangle[width=2.0, height=3.0]
                Rectangle[width=2.0, height=3.0]: area 6.0, perimeter 10.0
                Square[side=4.0]: area 16.0, perimeter 16.0
                """);
    }
}
