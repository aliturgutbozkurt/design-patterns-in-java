package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Functions as values: small steps composed into pipelines. Passing behaviour as data is the heart of the Strategy
 * and Command patterns (m06).
 */
public final class TextPipeline {

    private TextPipeline() {}

    /** strip → collapse whitespace → lower-case, composed with {@code andThen}. */
    public static Function<String, String> normalize() {
        Function<String, String> strip = String::strip;
        return strip.andThen(text -> text.replaceAll("\\s+", " ")).andThen(String::toLowerCase);
    }

    /** Composes any list of steps, left to right. An empty list is the identity function. */
    public static Function<String, String> compose(List<UnaryOperator<String>> steps) {
        Function<String, String> pipeline = Function.identity();
        for (UnaryOperator<String> step : steps) {
            pipeline = pipeline.andThen(step);
        }
        return pipeline;
    }

    public static Predicate<String> isKeyword(Set<String> keywords) {
        return keywords::contains;
    }

    public static Predicate<String> longerThan(int length) {
        return word -> word.length() > length;
    }
}
