package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Classic GoF handler: each level either resolves the ticket or forwards it to its successor. The sender only knows
 * the head of the chain; reaching the end without a resolver yields {@code Optional.empty()}.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public abstract class SupportHandler {

    private SupportHandler next;

    /** Sets the successor and returns it, so a chain reads {@code a.linkTo(b).linkTo(c)}. */
    public final SupportHandler linkTo(SupportHandler successor) {
        this.next = Objects.requireNonNull(successor, "successor");
        return successor;
    }

    /** Handles the ticket here or passes it on. */
    public final Optional<Resolution> handle(Ticket ticket) {
        return handle(Objects.requireNonNull(ticket, "ticket"), new ArrayList<>());
    }

    private Optional<Resolution> handle(Ticket ticket, List<String> path) {
        path.add(name());
        if (canHandle(ticket)) {
            return Optional.of(new Resolution(ticket.id(), name(), path));
        }
        return next == null ? Optional.empty() : next.handle(ticket, path);
    }

    /** The level's name, e.g. {@code "Helpdesk"}. */
    public abstract String name();

    /** Whether this level can resolve the ticket. */
    protected abstract boolean canHandle(Ticket ticket);
}
