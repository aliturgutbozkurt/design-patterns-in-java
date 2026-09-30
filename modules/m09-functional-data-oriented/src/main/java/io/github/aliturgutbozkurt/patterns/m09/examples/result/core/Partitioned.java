package io.github.aliturgutbozkurt.patterns.m09.examples.result.core;

import java.util.List;

/**
 * The values and the errors of many results, both in input order and unmodifiable.
 *
 * @param <T> type of the success values
 * @param <E> type of the errors
 * @see "m09 lesson, section Optional and Result — errors as values"
 */
public record Partitioned<T, E>(List<T> oks, List<E> errors) {

    public Partitioned {
        oks = List.copyOf(oks);
        errors = List.copyOf(errors);
    }
}
