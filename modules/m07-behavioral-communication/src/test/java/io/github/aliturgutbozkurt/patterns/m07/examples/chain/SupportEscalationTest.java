package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Engineering;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Helpdesk;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Resolution;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.SupportHandler;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.TechnicalSupport;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Ticket;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.classic.Topic;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern.SupportLevels;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.support.modern.TicketHandler;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SupportEscalationTest {

    private static final String HELPDESK = "Helpdesk";
    private static final String TECHNICAL = "Technical support";
    private static final String ENGINEERING = "Engineering";

    private static SupportHandler classicChain() {
        SupportHandler head = new Helpdesk();
        head.linkTo(new TechnicalSupport()).linkTo(new Engineering());
        return head;
    }

    /** The shared table: ticket → expected resolver (null = nobody) and escalation path. */
    static Stream<Arguments> tickets() {
        return Stream.of(
                Arguments.of(new Ticket("T-1", Topic.PASSWORD, 1), HELPDESK, List.of(HELPDESK)),
                Arguments.of(new Ticket("T-2", Topic.BILLING, 2), HELPDESK, List.of(HELPDESK)),
                Arguments.of(new Ticket("T-3", Topic.BILLING, 3), TECHNICAL, List.of(HELPDESK, TECHNICAL)),
                Arguments.of(new Ticket("T-4", Topic.BUG, 1), TECHNICAL, List.of(HELPDESK, TECHNICAL)),
                Arguments.of(new Ticket("T-5", Topic.BUG, 4), ENGINEERING, List.of(HELPDESK, TECHNICAL, ENGINEERING)),
                Arguments.of(new Ticket("T-6", Topic.OUTAGE, 5), ENGINEERING,
                        List.of(HELPDESK, TECHNICAL, ENGINEERING)),
                Arguments.of(new Ticket("T-7", Topic.LEGAL, 1), null, List.of()),
                Arguments.of(new Ticket("T-8", Topic.PASSWORD, 5), null, List.of()));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("tickets")
    void classicChainResolvesAtTheFirstCapableLevelAndRecordsThePath(
            Ticket ticket, String resolver, List<String> path) {
        var resolution = classicChain().handle(ticket);
        if (resolver == null) {
            assertThat(resolution).as("end of chain reached").isEmpty();
        } else {
            assertThat(resolution).contains(new Resolution(ticket.id(), resolver, path));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tickets")
    void functionalChainGivesTheSameResolutionAsTheClassicChain(Ticket ticket) {
        assertThat(SupportLevels.standardChain().handle(ticket)).isEqualTo(classicChain().handle(ticket));
    }

    @Test
    void handlersAfterTheDecidingOneAreNeverInvoked() {
        var calls = new AtomicInteger();
        TicketHandler counting = ticket -> {
            calls.incrementAndGet();
            return SupportLevels.engineering().handle(ticket);
        };
        TicketHandler chain = SupportLevels.helpdesk().orElse(SupportLevels.technicalSupport()).orElse(counting);

        chain.handle(new Ticket("T-1", Topic.PASSWORD, 1));
        chain.handle(new Ticket("T-2", Topic.BUG, 2));
        assertThat(calls).hasValue(0);
        chain.handle(new Ticket("T-3", Topic.OUTAGE, 5));
        assertThat(calls).hasValue(1);
    }

    @Test
    void escalationPathIsImmutable() {
        var resolution = classicChain().handle(new Ticket("T-1", Topic.BUG, 5)).orElseThrow();
        assertThat(resolution.escalationPath()).isUnmodifiable();
    }

    @Test
    void ticketSeverityMustBeBetweenOneAndFive() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Ticket("T-0", Topic.BUG, 6));
    }

    @Test
    void demoPrintsBothChainsWithTheSameResults() {
        assertThat(Console.capture(() -> SupportEscalationDemo.main(new String[0]))).isEqualTo("""
                classic chain (linked objects):
                  T-1 PASSWORD/1 -> Helpdesk via [Helpdesk]
                  T-2 BUG/2 -> Technical support via [Helpdesk, Technical support]
                  T-3 OUTAGE/5 -> Engineering via [Helpdesk, Technical support, Engineering]
                  T-4 LEGAL/1 -> unresolved (end of chain)
                modern chain (composed functions):
                  T-1 PASSWORD/1 -> Helpdesk via [Helpdesk]
                  T-2 BUG/2 -> Technical support via [Helpdesk, Technical support]
                  T-3 OUTAGE/5 -> Engineering via [Helpdesk, Technical support, Engineering]
                  T-4 LEGAL/1 -> unresolved (end of chain)
                same resolutions: true
                """);
    }
}
