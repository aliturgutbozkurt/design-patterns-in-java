package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;

/**
 * A custom sequential gatherer: splits a stream of clicks into sessions. A new session starts when the gap to the
 * previous click is longer than {@code maxGapSeconds}. It needs state (the open session), an integrator and a
 * finisher that emits the last session.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class SessionGatherer {

    private SessionGatherer() {}

    public static Gatherer<Click, ?, List<Click>> sessions(long maxGapSeconds) {
        if (maxGapSeconds < 0) {
            throw new IllegalArgumentException("maxGapSeconds must be >= 0: " + maxGapSeconds);
        }

        class OpenSession {
            List<Click> clicks = new ArrayList<>();
        }

        return Gatherer.ofSequential(
                OpenSession::new,                                   // initializer: fresh state per stream
                Gatherer.Integrator.of((state, click, downstream) -> {
                    boolean wantsMore = true;
                    if (!state.clicks.isEmpty() && click.second() - state.clicks.getLast().second() > maxGapSeconds) {
                        wantsMore = downstream.push(List.copyOf(state.clicks));  // false = downstream is done
                        state.clicks = new ArrayList<>();
                    }
                    state.clicks.add(click);
                    return wantsMore;
                }),
                (state, downstream) -> {                            // finisher: the last session is still open
                    if (!state.clicks.isEmpty()) {
                        downstream.push(List.copyOf(state.clicks));
                    }
                });
    }
}
