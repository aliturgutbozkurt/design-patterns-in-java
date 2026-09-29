package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

/**
 * Second level: resolves password, billing and bug tickets up to severity 3.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public final class TechnicalSupport extends SupportHandler {

    @Override
    public String name() {
        return "Technical support";
    }

    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.topic() != Topic.OUTAGE && ticket.topic() != Topic.LEGAL && ticket.severity() <= 3;
    }
}
