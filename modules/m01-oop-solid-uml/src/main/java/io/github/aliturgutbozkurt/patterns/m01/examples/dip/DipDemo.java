package io.github.aliturgutbozkurt.patterns.m01.examples.dip;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.after.EmailSender;
import io.github.aliturgutbozkurt.patterns.m01.examples.dip.after.NotificationService;
import io.github.aliturgutbozkurt.patterns.m01.examples.dip.after.SmsSender;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/dip/DipDemo.java} */
public final class DipDemo {

    private DipDemo() {}

    public static void main(String[] args) {
        var ada = new Customer("Ada", "ada@example.com", "+90 555 000 00 01", Channel.EMAIL);
        var alan = new Customer("Alan", "alan@example.com", "+90 555 000 00 02", Channel.SMS);

        System.out.println("== before: NotificationService calls new EmailSender() ==");
        var before = new io.github.aliturgutbozkurt.patterns.m01.examples.dip.before.NotificationService();
        before.notifyShipped(ada, "A-1");
        before.notifyShipped(alan, "A-2");
        System.out.println("(Alan prefers SMS, but the service can only e-mail without being edited)");

        System.out.println("== after: senders are injected by main (the composition root) ==");
        var service = new NotificationService(new EmailSender(), new SmsSender());
        service.notifyShipped(ada, "A-1");
        service.notifyShipped(alan, "A-2");
    }
}
