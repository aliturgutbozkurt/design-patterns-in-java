package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Resolution;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Ticket;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Topic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Static factories for the support levels as {@link TicketHandler} functions — the same rules as the classic
 * {@code Helpdesk}, {@code TechnicalSupport} and {@code Engineering} classes.
 *
 * @see "m07 lesson, section Chain of Responsibility — Modern Java 27"
 */
public final class SupportLevels {

    private SupportLevels() {}

    /** A single level that resolves the tickets matching {@code canHandle}. */
    public static TicketHandler level(String name, Predicate<Ticket> canHandle) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(canHandle, "canHandle");
        return new TicketHandler() {
            @Override
            public Optional<Resolution> handle(Ticket ticket) {
                return canHandle.test(ticket)
                        ? Optional.of(new Resolution(ticket.id(), name, List.of(name)))
                        : Optional.empty();
            }

            @Override
            public List<String> levels() {
                return List.of(name);
            }
        };
    }

    public static TicketHandler helpdesk() {
        return level("Helpdesk", ticket ->
                (ticket.topic() == Topic.PASSWORD || ticket.topic() == Topic.BILLING) && ticket.severity() <= 2);
    }

    public static TicketHandler technicalSupport() {
        return level("Technical support", ticket ->
                ticket.topic() != Topic.OUTAGE && ticket.topic() != Topic.LEGAL && ticket.severity() <= 3);
    }

    public static TicketHandler engineering() {
        return level("Engineering", ticket -> ticket.topic() == Topic.BUG || ticket.topic() == Topic.OUTAGE);
    }

    /** Helpdesk, then technical support, then engineering. */
    public static TicketHandler standardChain() {
        return helpdesk().orElse(technicalSupport()).orElse(engineering());
    }
}
