package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Resolution;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Ticket;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Chain of Responsibility as functions: a handler returns a resolution or {@code Optional.empty()} ("not me"), and
 * {@link #orElse(TicketHandler)} composes two handlers with {@link Optional#or}.
 *
 * @see "m07 lesson, section Chain of Responsibility — Modern Java 27"
 */
@FunctionalInterface
public interface TicketHandler {

    Optional<Resolution> handle(Ticket ticket);

    /** Names of the support levels inside this handler, in order, for the escalation path; a bare lambda has none. */
    default List<String> levels() {
        return List.of();
    }

    /** Asks this handler first and {@code next} only if this one returned empty. */
    default TicketHandler orElse(TicketHandler next) {
        Objects.requireNonNull(next, "next");
        TicketHandler first = this;
        List<String> levels = Stream.concat(first.levels().stream(), next.levels().stream()).toList();
        return new TicketHandler() {
            @Override
            public Optional<Resolution> handle(Ticket ticket) {
                return first.handle(ticket)
                        .or(() -> next.handle(ticket).map(resolution -> resolution.escalatedFrom(first.levels())));
            }

            @Override
            public List<String> levels() {
                return levels;
            }
        };
    }
}
