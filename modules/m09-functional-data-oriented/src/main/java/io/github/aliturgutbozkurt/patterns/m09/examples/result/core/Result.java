package io.github.aliturgutbozkurt.patterns.m09.examples.result.core;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Errors as values: either {@link Ok} with a value or {@link Err} with the reason it failed. Functions passed to
 * {@link #map} and {@link #flatMap} run only on the {@code Ok} track, the function passed to {@link #mapError} only on
 * the {@code Err} track ("railway").
 *
 * @param <T> type of the success value
 * @param <E> type of the error
 * @see "m09 lesson, section Optional and Result — errors as values"
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    /** The success track. {@code value} is never {@code null}. */
    record Ok<T, E>(T value) implements Result<T, E> {
        public Ok {
            Objects.requireNonNull(value, "value");
        }
    }

    /** The failure track. {@code error} is never {@code null}. */
    record Err<T, E>(E error) implements Result<T, E> {
        public Err {
            Objects.requireNonNull(error, "error");
        }
    }

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }

    /** Runs {@code action}; a thrown exception becomes an {@code Err} built by {@code onError}. */
    static <T, E> Result<T, E> attempt(Callable<? extends T> action, Function<? super Exception, ? extends E> onError) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(onError, "onError");
        T value;
        try {
            value = action.call();
        } catch (Exception e) {
            return new Err<>(onError.apply(e));
        }
        return new Ok<>(value);
    }

    /** Transforms the value on the {@code Ok} track; an {@code Err} passes through untouched. */
    default <U> Result<U, E> map(Function<? super T, ? extends U> f) {
        Objects.requireNonNull(f, "f");
        return switch (this) {
            case Ok<T, E>(var value) -> new Ok<>(f.apply(value));
            case Err<T, E>(var error) -> new Err<>(error);
        };
    }

    /** Transforms the error on the {@code Err} track; an {@code Ok} passes through untouched. */
    default <F> Result<T, F> mapError(Function<? super E, ? extends F> f) {
        Objects.requireNonNull(f, "f");
        return switch (this) {
            case Ok<T, E>(var value) -> new Ok<>(value);
            case Err<T, E>(var error) -> new Err<>(f.apply(error));
        };
    }

    /** Chains a step that can itself fail: the first {@code Err} wins and later steps never run. */
    default <U> Result<U, E> flatMap(Function<? super T, ? extends Result<? extends U, E>> f) {
        Objects.requireNonNull(f, "f");
        return switch (this) {
            case Ok<T, E>(var value) -> narrow(f.apply(value));
            case Err<T, E>(var error) -> new Err<>(error);
        };
    }

    /** Leaves the railway: exactly one of the two functions runs. */
    default <R> R fold(Function<? super T, ? extends R> onOk, Function<? super E, ? extends R> onErr) {
        Objects.requireNonNull(onOk, "onOk");
        Objects.requireNonNull(onErr, "onErr");
        return switch (this) {
            case Ok<T, E>(var value) -> onOk.apply(value);
            case Err<T, E>(var error) -> onErr.apply(error);
        };
    }

    default T orElse(T fallback) {
        return fold(Function.identity(), _ -> fallback);
    }

    /** The value, or a fallback computed from the error (only called on {@code Err}). */
    default T orElseGet(Function<? super E, ? extends T> fallback) {
        return fold(Function.identity(), fallback);
    }

    /** The value, if any. The error is lost — use this only when the caller does not care why. */
    default Optional<T> toOptional() {
        return fold(Optional::of, _ -> Optional.empty());
    }

    /**
     * Rebuilds a {@code Result<? extends U, E>} as a {@code Result<U, E>}. A cast would be an unchecked warning
     * (a build error under {@code -Werror}); copying the value into a new record is type-safe.
     */
    private static <U, E> Result<U, E> narrow(Result<? extends U, E> result) {
        return switch (result) {
            case Ok<? extends U, E>(var value) -> new Ok<>(value);
            case Err<? extends U, E>(var error) -> new Err<>(error);
        };
    }
}
