package io.github.aliturgutbozkurt.patterns.m05.examples.bridge;

import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.AlertService;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.DigestAlerts;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.EmailChannel;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.MessageChannel;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.SmsChannel;
import io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts.UrgentAlerts;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/bridge/AlertsDemo.java}
 *
 * @see "m05 lesson, section Bridge"
 */
public final class AlertsDemo {

    private AlertsDemo() {}

    public static void main(String[] args) {
        MessageChannel email = new EmailChannel("ops@example.com", System.out::println);
        MessageChannel sms = new SmsChannel("+90 555 000 00 00", System.out::println);
        MessageChannel chat = message -> System.out.println("chat #ops: " + message);   // no new class needed

        new UrgentAlerts(email).raise("payment service down");
        new UrgentAlerts(sms).raise("payment service down");

        var digest = new DigestAlerts(email);
        digest.raise("cpu 85%");
        digest.raise("disk 80%");
        digest.raise("certificate expires in 14 days");
        System.out.println("digest: " + digest.pending() + " pending, nothing sent yet");
        digest.flush();

        AlertService toChat = new UrgentAlerts(chat);
        toChat.raise("payment service back up");
        new UrgentAlerts(sms).raise("payment retries failing for order 1042; ".repeat(5));
    }
}
