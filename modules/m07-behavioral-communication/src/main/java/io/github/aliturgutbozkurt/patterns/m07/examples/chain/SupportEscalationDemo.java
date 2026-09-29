package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Engineering;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Helpdesk;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Resolution;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.SupportHandler;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.TechnicalSupport;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Ticket;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Topic;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern.SupportLevels;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern.TicketHandler;
import java.util.List;
import java.util.Optional;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/chain/SupportEscalationDemo.java} */
public final class SupportEscalationDemo {

    private SupportEscalationDemo() {}

    public static void main(String[] args) {
        List<Ticket> tickets = List.of(
                new Ticket("T-1", Topic.PASSWORD, 1),
                new Ticket("T-2", Topic.BUG, 2),
                new Ticket("T-3", Topic.OUTAGE, 5),
                new Ticket("T-4", Topic.LEGAL, 1));

        SupportHandler classic = new Helpdesk();
        classic.linkTo(new TechnicalSupport()).linkTo(new Engineering());
        System.out.println("classic chain (linked objects):");
        tickets.forEach(ticket -> print(ticket, classic.handle(ticket)));

        TicketHandler modern = SupportLevels.helpdesk()
                .orElse(SupportLevels.technicalSupport())
                .orElse(SupportLevels.engineering());
        System.out.println("modern chain (composed functions):");
        tickets.forEach(ticket -> print(ticket, modern.handle(ticket)));

        boolean same = tickets.stream().allMatch(ticket -> classic.handle(ticket).equals(modern.handle(ticket)));
        System.out.println("same resolutions: " + same);
    }

    private static void print(Ticket ticket, Optional<Resolution> resolution) {
        System.out.println("  " + ticket.id() + " " + ticket.topic() + "/" + ticket.severity() + " -> "
                + resolution.map(r -> r.handledBy() + " via " + r.escalationPath())
                        .orElse("unresolved (end of chain)"));
    }
}
