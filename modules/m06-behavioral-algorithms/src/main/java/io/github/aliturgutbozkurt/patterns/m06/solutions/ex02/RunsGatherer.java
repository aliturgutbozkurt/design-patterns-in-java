package io.github.aliturgutbozkurt.patterns.m06.solutions.ex02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Gatherer;

/**
 * Reference solution for assignment 02: a sequential gatherer whose state is the run being built.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class RunsGatherer {

    private RunsGatherer() {}

    public static <T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun) {
        Objects.requireNonNull(sameRun, "sameRun");

        class OpenRun {
            List<T> items = new ArrayList<>();

            /** Hands out the finished run as an unmodifiable list and starts a new one. */
            List<T> close() {
                List<T> finished = Collections.unmodifiableList(items);
                items = new ArrayList<>();
                return finished;
            }
        }

        return Gatherer.ofSequential(
                OpenRun::new,
                Gatherer.Integrator.of((run, element, downstream) -> {
                    boolean wantsMore = true;
                    if (!run.items.isEmpty() && !sameRun.test(run.items.getLast(), element)) {
                        wantsMore = downstream.push(run.close());
                    }
                    run.items.add(element);
                    return wantsMore;
                }),
                (run, downstream) -> {
                    if (!run.items.isEmpty()) {
                        downstream.push(run.close());
                    }
                });
    }
}
