package io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * A custom joiner, "best effort": failures are ignored, and the result is every successful subtask's value in
 * fork order. On timeout it returns the successes so far instead of throwing. {@code onFork} is called by the
 * scope's owner thread, and {@code result()}/{@code timeout()} are called from its {@code join()}, so the list of
 * forked subtasks needs no lock. Preview API (JEP 533).
 *
 * @param <T> result type of the subtasks
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class BestEffortJoiner<T> implements Joiner<T, List<T>, RuntimeException> {

    private final List<Subtask<T>> forked = new ArrayList<>();

    @Override
    public boolean onFork(Subtask<T> subtask) {
        forked.add(subtask);
        return false;                            // never cancel the scope because of a fork
    }

    @Override
    public boolean onComplete(Subtask<T> subtask) {
        return false;                            // a failure does not cancel the others either
    }

    @Override
    public List<T> result() {
        return successes();
    }

    @Override
    public List<T> timeout() {
        return successes();                      // partial result instead of an exception
    }

    private List<T> successes() {
        return forked.stream().filter(subtask -> subtask.state() == Subtask.State.SUCCESS).map(Subtask::get).toList();
    }
}
