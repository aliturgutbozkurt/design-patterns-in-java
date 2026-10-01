package io.github.aliturgutbozkurt.patterns.m09.examples.result.jdk;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Bridges {@link CompletableFuture} (the JDK's asynchronous result type) and {@link Result}.
 *
 * @see "m09 lesson, section Optional and Result — the same shape in the JDK"
 */
public final class FutureResults {

    private FutureResults() {}

    /**
     * Waits for {@code future} and turns it into a {@code Result}. A failure becomes an {@code Err} holding the
     * <em>original</em> cause, not the {@link CompletionException} the JDK wraps around it after a dependent stage.
     */
    public static <T> Result<T, Throwable> toResult(CompletableFuture<T> future) {
        Objects.requireNonNull(future, "future");
        return future.<Result<T, Throwable>>handle((value, failure) ->
                failure == null ? Result.ok(value) : Result.err(cause(failure))).join();
    }

    /** The exception that really happened: unwraps a {@link CompletionException}. */
    public static Throwable cause(Throwable failure) {
        return failure instanceof CompletionException wrapper && wrapper.getCause() != null
                ? wrapper.getCause()
                : failure;
    }
}
