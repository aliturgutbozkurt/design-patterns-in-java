package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

/**
 * First level: resolves minor password and billing questions (severity ≤ 2).
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public final class Helpdesk extends SupportHandler {

    @Override
    public String name() {
        return "Helpdesk";
    }

    @Override
    protected boolean canHandle(Ticket ticket) {
        return (ticket.topic() == Topic.PASSWORD || ticket.topic() == Topic.BILLING) && ticket.severity() <= 2;
    }
}
