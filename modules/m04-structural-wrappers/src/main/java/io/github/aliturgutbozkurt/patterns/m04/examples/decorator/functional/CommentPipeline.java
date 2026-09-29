package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Functional decorator chain: text filters stacked with {@link Function#andThen}, built once and reused.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class CommentPipeline {

    private final Function<String, String> steps;

    private CommentPipeline(Function<String, String> steps) {
        this.steps = steps;
    }

    /** The empty pipeline: returns every comment unchanged. */
    public static CommentPipeline identity() {
        return new CommentPipeline(Function.identity());
    }

    /** A pipeline that applies {@code filters} from first to last. */
    public static CommentPipeline of(List<UnaryOperator<String>> filters) {
        CommentPipeline pipeline = identity();
        for (UnaryOperator<String> filter : filters) {
            pipeline = pipeline.then(filter);
        }
        return pipeline;
    }

    /** A new pipeline that runs this one and then {@code filter}; this pipeline is unchanged. */
    public CommentPipeline then(UnaryOperator<String> filter) {
        return new CommentPipeline(steps.andThen(Objects.requireNonNull(filter, "filter")));
    }

    public String apply(String comment) {
        return steps.apply(Objects.requireNonNull(comment, "comment"));
    }
}
