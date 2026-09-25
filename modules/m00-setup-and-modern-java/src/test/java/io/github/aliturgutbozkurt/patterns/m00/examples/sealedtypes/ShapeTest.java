package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;

import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import org.junit.jupiter.api.Test;

class ShapeTest {

    @Test
    void eachShapeKnowsItsArea() {
        assertThat(new Circle(1).area()).isCloseTo(Math.PI, within(1e-9));
        assertThat(new Rectangle(2, 3).area()).isEqualTo(6);
        assertThat(new Triangle(3, 4, 5).area()).isCloseTo(6, within(1e-9));
    }

    @Test
    void perimeterIsComputedByAnExhaustiveSwitch() {
        assertThat(Shapes.perimeter(new Circle(1))).isCloseTo(2 * Math.PI, within(1e-9));
        assertThat(Shapes.perimeter(new Rectangle(2, 3))).isEqualTo(10);
        assertThat(Shapes.perimeter(new Triangle(3, 4, 5))).isEqualTo(12);
    }

    @Test
    void describeUsesGuardsForSpecialCases() {
        assertThat(Shapes.describe(new Circle(0))).isEqualTo("a point");
        assertThat(Shapes.describe(new Circle(2))).isEqualTo("a circle with radius 2.0");
        assertThat(Shapes.describe(new Rectangle(2, 2))).isEqualTo("a 2.0 × 2.0 square");
        assertThat(Shapes.describe(new Rectangle(2, 3))).isEqualTo("a 2.0 × 3.0 rectangle");
        assertThat(Shapes.describe(new Triangle(3, 4, 5))).isEqualTo("a triangle");
    }

    @Test
    void rejectsImpossibleShapes() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Circle(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Rectangle(0, 1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Triangle(1, 2, 10))
                .withMessageContaining("triangle inequality");
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> ShapeDemo.main(new String[0]))).isEqualTo("""
                a circle with radius 1.0: area 3.14, perimeter 6.28
                a 2.0 × 2.0 square: area 4.00, perimeter 8.00
                a 2.0 × 3.0 rectangle: area 6.00, perimeter 10.00
                a triangle: area 6.00, perimeter 12.00
                a point: area 0.00, perimeter 0.00
                """);
    }
}
