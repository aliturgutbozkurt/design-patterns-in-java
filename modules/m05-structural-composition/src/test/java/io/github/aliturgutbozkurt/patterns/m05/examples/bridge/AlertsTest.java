package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.AlertService;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.DigestAlerts;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.EmailChannel;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.SmsChannel;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.UrgentAlerts;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AlertsTest {

    private final List<String> sent = new ArrayList<>();

    @Test
    void urgentAlertReachesTheChannelImmediately() {
        AlertService alerts = new UrgentAlerts(new EmailChannel("ops@example.com", sent::add));
        alerts.raise("disk 91% full");
        assertThat(sent).containsExactly("email to ops@example.com: [URGENT] disk 91% full");
    }

    @Test
    void digestSendsNothingUntilFlushThenOneMessageInOrder() {
        var digest = new DigestAlerts(new EmailChannel("ops@example.com", sent::add));
        digest.raise("cpu 85%");
        digest.raise("queue lag 12 s");
        assertThat(sent).isEmpty();
        assertThat(digest.pending()).isEqualTo(2);
        digest.flush();
        assertThat(sent).containsExactly("email to ops@example.com: 2 alerts: cpu 85%; queue lag 12 s");
        assertThat(digest.pending()).isZero();
    }

    @Test
    void flushWithNothingPendingSendsNothing() {
        var digest = new DigestAlerts(sent::add);
        digest.flush();
        assertThat(sent).isEmpty();
    }

    @Test
    void smsTruncatesTo160CharactersWithAnEllipsis() {
        var sms = new SmsChannel("+90 555 000 00 00", sent::add);
        sms.send("x".repeat(200));
        String body = sent.getFirst().substring("sms to +90 555 000 00 00: ".length());
        assertThat(body).hasSize(160).endsWith("x…");
        sms.send("y".repeat(160));
        assertThat(sent.get(1)).endsWith("y".repeat(160));
    }

    @Test
    void aLambdaChannelWorksWithoutANewClass() {
        AlertService alerts = new UrgentAlerts(message -> sent.add("chat: " + message));
        alerts.raise("build broken");
        assertThat(sent).containsExactly("chat: [URGENT] build broken");
    }

    @Test
    void everyPolicyWorksWithEveryChannel() {
        var sms = new SmsChannel("+1", sent::add);
        new UrgentAlerts(sms).raise("a");
        var digest = new DigestAlerts(sms);
        digest.raise("b");
        digest.flush();
        assertThat(sent).containsExactly("sms to +1: [URGENT] a", "sms to +1: 1 alert: b");
    }

    @Test
    void rejectsBlankAlerts() {
        assertThatIllegalArgumentException().isThrownBy(() -> new UrgentAlerts(sent::add).raise(" "));
    }

    @Test
    void demoPrintsWhatEachChannelDelivers() {
        assertThat(Console.capture(() -> AlertsDemo.main(new String[0]))).isEqualTo("""
                email to ops@example.com: [URGENT] payment service down
                sms to +90 555 000 00 00: [URGENT] payment service down
                digest: 3 pending, nothing sent yet
                email to ops@example.com: 3 alerts: cpu 85%; disk 80%; certificate expires in 14 days
                chat #ops: [URGENT] payment service back up
                sms to +90 555 000 00 00: [URGENT] payment retries failing for order 1042; payment retries \
                failing for order 1042; payment retries failing for order 1042; payment retries failing for or…
                """);
    }
}
