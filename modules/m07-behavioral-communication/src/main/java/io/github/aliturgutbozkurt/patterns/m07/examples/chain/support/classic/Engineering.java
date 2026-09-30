package io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic;

/**
 * Last level: resolves every bug and outage, whatever the severity — but no billing, password or legal questions.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public final class Engineering extends SupportHandler {

    @Override
    public String name() {
        return "Engineering";
    }

    @Override
    protected boolean canHandle(Ticket ticket) {
        return ticket.topic() == Topic.BUG || ticket.topic() == Topic.OUTAGE;
    }
}
