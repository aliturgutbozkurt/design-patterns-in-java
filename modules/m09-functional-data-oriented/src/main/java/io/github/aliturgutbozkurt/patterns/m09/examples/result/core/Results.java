package io.github.aliturgutbozkurt.patterns.m09.examples.result.core;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Functions over many results at once: {@link #sequence} fails fast, {@link #partition} keeps everything.
 *
 * @see "m09 lesson, section Optional and Result — errors as values"
 */
public final class Results {

    private Results() {}

    /** {@code Ok} of all values in order, or the first {@code Err} in the list. */
    public static <T, E> Result<List<T>, E> sequence(List<? extends Result<T, E>> results) {
        Objects.requireNonNull(results, "results");
        List<T> values = new ArrayList<>(results.size());
        for (Result<T, E> result : results) {
            switch (result) {
                case Ok<T, E>(var value) -> values.add(value);
                case Err<T, E>(var error) -> {
                    return new Err<>(error);
                }
            }
        }
        return new Ok<>(List.copyOf(values));
    }

    /** Splits the results into the values and the errors, both in input order (one pass, {@code teeing}). */
    public static <T, E> Partitioned<T, E> partition(List<? extends Result<T, E>> results) {
        Objects.requireNonNull(results, "results");
        return results.stream().collect(Collectors.teeing(
                Collectors.flatMapping(Results::valueOf, Collectors.toList()),
                Collectors.flatMapping(Results::errorOf, Collectors.toList()),
                Partitioned::new));
    }

    private static <T, E> Stream<T> valueOf(Result<T, E> result) {
        return switch (result) {
            case Ok<T, E>(var value) -> Stream.of(value);
            case Err<T, E> _ -> Stream.empty();
        };
    }

    private static <T, E> Stream<E> errorOf(Result<T, E> result) {
        return switch (result) {
            case Ok<T, E> _ -> Stream.empty();
            case Err<T, E>(var error) -> Stream.of(error);
        };
    }
}
