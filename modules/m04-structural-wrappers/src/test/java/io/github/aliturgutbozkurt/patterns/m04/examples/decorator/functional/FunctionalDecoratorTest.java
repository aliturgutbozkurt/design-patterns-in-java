package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional;

import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.censor;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.collapseSpaces;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.trim;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.truncate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class FunctionalDecoratorTest {

    private static final String COMMENT = "   This   darn  product is GREAT,   darn it!   ";

    @Test
    void eachFilterDoesOneThing() {
        assertThat(trim().apply("  a  b  ")).isEqualTo("a  b");
        assertThat(collapseSpaces().apply("a  \t b\n\nc")).isEqualTo("a b c");
        assertThat(censor(Set.of("darn")).apply("Darn, darn it! darning is fine"))
                .isEqualTo("****, **** it! darning is fine");
        assertThat(truncate(4).apply("abcdef")).isEqualTo("abcd");
        assertThat(truncate(10).apply("short")).isEqualTo("short");
    }

    @Test
    void pipelineAppliesStepsInOrder() {
        var pipeline = CommentPipeline.of(List.of(trim(), collapseSpaces(), censor(Set.of("darn")), truncate(30)));
        assertThat(pipeline.apply(COMMENT)).isEqualTo("This **** product is GREAT, **");
    }

    @Test
    void pipelineIsBuiltOnceAndReused() {
        var pipeline = CommentPipeline.of(List.of(trim(), collapseSpaces()));
        assertThat(pipeline.apply("  a   b ")).isEqualTo("a b");
        assertThat(pipeline.apply(" c  d")).isEqualTo("c d");
    }

    @Test
    void thenReturnsANewPipelineAndLeavesTheOriginalUnchanged() {
        var base = CommentPipeline.of(List.of(trim()));
        var longer = base.then(truncate(3));
        assertThat(base.apply("  abcdef ")).isEqualTo("abcdef");
        assertThat(longer.apply("  abcdef ")).isEqualTo("abc");
    }

    @Test
    void orderMattersTruncateThenCensorDiffersFromCensorThenTruncate() {
        Function<String, String> censorThenTruncate = censor(Set.of("darn")).andThen(truncate(10));
        Function<String, String> truncateThenCensor = truncate(10).andThen(censor(Set.of("darn")));
        assertThat(censorThenTruncate.apply("this is darn good")).isEqualTo("this is **");
        assertThat(truncateThenCensor.apply("this is darn good")).isEqualTo("this is da");
        // compose is andThen read backwards:
        assertThat(truncate(10).compose(censor(Set.of("darn"))).apply("this is darn good")).isEqualTo("this is **");
    }

    @Test
    void identityPipelineReturnsTheInput() {
        assertThat(CommentPipeline.identity().apply(COMMENT)).isSameAs(COMMENT);
        assertThat(CommentPipeline.of(List.of()).apply(COMMENT)).isSameAs(COMMENT);
    }

    @Test
    void rejectsInvalidArguments() {
        assertThatIllegalArgumentException().isThrownBy(() -> truncate(-1)).withMessage("maxLength must not be negative: -1");
        assertThatNullPointerException().isThrownBy(() -> CommentPipeline.identity().apply(null));
        assertThatNullPointerException().isThrownBy(() -> censor(null));
    }

    @Test
    void demoPrintsTheModeratedCommentAndTheOrderEffect() {
        assertThat(Console.capture(() -> FunctionalDecoratorDemo.main(new String[0]))).isEqualTo("""
                before: [   This   darn  product is GREAT,   darn it!   ]
                after:  [This **** product is GREAT, **]
                censor, then truncate(10): [this is **]
                truncate(10), then censor: [this is da]
                """);
    }
}
