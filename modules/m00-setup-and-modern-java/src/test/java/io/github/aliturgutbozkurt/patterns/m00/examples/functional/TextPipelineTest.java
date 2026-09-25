package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class TextPipelineTest {

    @Test
    void normalizeComposesThreeSteps() {
        assertThat(TextPipeline.normalize().apply("  Design   PATTERNS\tin Java ")).isEqualTo("design patterns in java");
    }

    @Test
    void stepsAreDataThatCanBeComposedInAnyOrder() {
        List<UnaryOperator<String>> steps = List.of(String::strip, s -> s + "!", String::toUpperCase);
        assertThat(TextPipeline.compose(steps).apply("  hi ")).isEqualTo("HI!");
        assertThat(TextPipeline.compose(List.of()).apply("unchanged")).isEqualTo("unchanged");
    }

    @Test
    void predicatesCombine() {
        var keyword = TextPipeline.isKeyword(Set.of("record", "sealed"));
        var interesting = keyword.or(TextPipeline.longerThan(8));
        assertThat(List.of("record", "class", "polymorphism", "sealed", "var"))
                .filteredOn(interesting)
                .containsExactly("record", "polymorphism", "sealed");
        assertThat(keyword.negate().test("class")).isTrue();
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> TextPipelineDemo.main(new String[0]))).isEqualTo("""
                normalize: "design patterns in java"
                shout:     "DESIGN PATTERNS IN JAVA!"
                keywords:  [record, sealed, switch]
                """);
    }
}
