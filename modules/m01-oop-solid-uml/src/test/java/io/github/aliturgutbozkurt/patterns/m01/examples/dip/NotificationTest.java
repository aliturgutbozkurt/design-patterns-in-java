package io.github.aliturgutbozkurt.patterns.m01.examples.dip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.after.MessageSender;
import io.github.aliturgutbozkurt.patterns.m01.examples.dip.after.NotificationService;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationTest {

    static final Customer ADA = new Customer("Ada", "ada@example.com", "+90 555 000 00 01", Channel.EMAIL);
    static final Customer ALAN = new Customer("Alan", "alan@example.com", "+90 555 000 00 02", Channel.SMS);

    /** A hand-written test double: records what would have been sent. */
    static final class RecordingSender implements MessageSender {
        final List<String> sent = new ArrayList<>();

        @Override
        public void send(String to, String message) {
            sent.add(to + " | " + message);
        }
    }

    @Test
    void beforeCanOnlyBeTestedByCapturingConsoleOutput() {
        var service = new io.github.aliturgutbozkurt.patterns.m01.examples.dip.before.NotificationService();
        assertThat(Console.capture(() -> service.notifyShipped(ALAN, "A-2")))
                .isEqualTo("EMAIL to alan@example.com: Order A-2 has shipped, Alan.\n");
    }

    @Test
    void afterSendsThroughTheInjectedSenderForTheCustomersChannel() {
        var email = new RecordingSender();
        var sms = new RecordingSender();
        var service = new NotificationService(email, sms);

        service.notifyShipped(ADA, "A-1");
        service.notifyShipped(ALAN, "A-2");

        assertThat(email.sent).containsExactly("ada@example.com | Order A-1 has shipped, Ada.");
        assertThat(sms.sent).containsExactly("+90 555 000 00 02 | Order A-2 has shipped, Alan.");
    }

    @Test
    void afterWorksWithALambdaSender() {
        List<String> log = new ArrayList<>();
        MessageSender toLog = (to, message) -> log.add(message);
        new NotificationService(toLog, toLog).notifyShipped(ADA, "A-9");
        assertThat(log).containsExactly("Order A-9 has shipped, Ada.");
    }

    @Test
    void afterRejectsMissingDependencies() {
        var sender = new RecordingSender();
        assertThatNullPointerException().isThrownBy(() -> new NotificationService(null, sender));
        assertThatNullPointerException().isThrownBy(() -> new NotificationService(sender, null));
    }

    @Test
    void demoWiresRealSendersInTheCompositionRoot() {
        assertThat(Console.capture(() -> DipDemo.main(new String[0]))).isEqualTo("""
                == before: NotificationService calls new EmailSender() ==
                EMAIL to ada@example.com: Order A-1 has shipped, Ada.
                EMAIL to alan@example.com: Order A-2 has shipped, Alan.
                (Alan prefers SMS, but the service can only e-mail without being edited)
                == after: senders are injected by main (the composition root) ==
                EMAIL to ada@example.com: Order A-1 has shipped, Ada.
                SMS to +90 555 000 00 02: Order A-2 has shipped, Alan.
                """);
    }
}
