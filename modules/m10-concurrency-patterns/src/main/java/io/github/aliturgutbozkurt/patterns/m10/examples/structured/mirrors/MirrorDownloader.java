package io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * "First success wins, cancel the rest": every mirror is asked at once, and
 * {@link Joiner#anySuccessfulOrThrow()} cancels the scope as soon as one of them delivers, which interrupts the
 * slower ones. Only when every mirror failed does {@code join()} throw. Preview API (JEP 533).
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class MirrorDownloader {

    private final List<Mirror> mirrors;

    public MirrorDownloader(List<Mirror> mirrors) {
        this.mirrors = List.copyOf(mirrors);
    }

    public Download download(String file) throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<Download>anySuccessfulOrThrow())) {
            for (Mirror mirror : mirrors) {
                scope.fork(() -> new Download(mirror.name(), mirror.source().fetch(file)));
            }
            return scope.join();                 // the first successful result
        }
    }
}
