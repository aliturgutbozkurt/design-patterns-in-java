package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue.VisitorRow.Modern.Circle;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue.VisitorRow.Modern.Rect;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue.VisitorRow.Modern.Shape;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CatalogueTest {

    static Stream<Catalogue.Row> rows() {
        return Catalogue.rows().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rows")
    void classicAndModernProduceTheSameOutput(Catalogue.Row row) {
        assertThat(row.modern().get()).isEqualTo(row.classic().get()).isNotBlank();
    }

    @Test
    void theCatalogueHasEightRowsEachNamingItsLesson() {
        assertThat(Catalogue.rows()).extracting(Catalogue.Row::pattern).containsExactly("Strategy", "Command",
                "Template Method", "Visitor", "Iterator", "Factory", "Decorator", "Singleton");
        assertThat(Catalogue.rows()).allSatisfy(row -> assertThat(row.lesson()).matches("m0[0-9]"));
    }

    /** A brand-new operation, written here, without touching VisitorRow.Modern's data types. */
    private static double perimeter(Shape shape) {
        return switch (shape) {
            case Circle(var r) -> 2 * Math.PI * r;
            case Rect(var w, var h) -> 2 * (w + h);
        };
    }

    @Test
    void theModernVisitorAddsAnOperationWithoutTouchingTheDataTypes() {
        assertThat(List.of(new Circle(1), new Rect(2, 3)).stream().map(s -> VisitorRow.format(perimeter(s))))
                .containsExactly("6.28", "10.00");
    }

    @Test
    void reflectivelyCreatingTheEnumSingletonFails() throws ReflectiveOperationException {
        Constructor<SingletonRow.Modern.Settings> constructor =
                SingletonRow.Modern.Settings.class.getDeclaredConstructor(String.class, int.class);
        constructor.setAccessible(true);
        assertThatThrownBy(() -> constructor.newInstance("SECOND", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot reflectively create enum objects");
    }

    @Test
    void theFactoryMapRejectsAnUnknownKeyClearly() {
        assertThatIllegalArgumentException().isThrownBy(() -> FactoryRow.Modern.create("hexagon"))
                .withMessage("unknown shape: hexagon");
        assertThatIllegalArgumentException().isThrownBy(() -> FactoryRow.Classic.ShapeFactory.forName("hexagon"))
                .withMessage("unknown shape: hexagon");
    }

    @Test
    void theMarkdownTableHasAHeaderAndOneLinePerRow() {
        assertThat(Catalogue.markdownTable().lines()).hasSize(10)
                .first().isEqualTo("| Pattern | Java feature that does its job | Still write the class when… | Full treatment |");
    }

    @Test
    void demoPrintsTheComparisonAndTheTable() {
        String out = Console.capture(() -> CatalogueDemo.main(new String[0]));
        assertThat(out).startsWith("""
                -- classic and modern side by side (same input, same output)
                Strategy        same: [cap, mug, tee, bag]
                Command         same: [tee, mug] history=2
                Template Method same: STOCK|- mug|- tee|end
                Visitor         same: [3.14, 6.00]
                Iterator        same: [[1, 2, 3], [4, 5, 6], [7]]
                Factory         same: [a circle, a square, a circle]
                Decorator       same: [SPRING SALE]
                Singleton       same: same instance: true, currency EUR
                -- the table
                """);
        assertThat(out).endsWith(Catalogue.markdownTable()).doesNotContain("DIFFERENT");
    }
}
