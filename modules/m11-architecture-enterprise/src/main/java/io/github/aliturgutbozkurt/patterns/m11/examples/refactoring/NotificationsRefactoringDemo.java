package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Analytics;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.CrmClient;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Mailer;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after.SignUpReactions;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after.UserRegistered;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/refactoring/NotificationsRefactoringDemo.java} */
public final class NotificationsRefactoringDemo {

    private NotificationsRefactoringDemo() {}

    public static void main(String[] args) {
        Mailer mailer = email -> System.out.println("  mail: welcome " + email);
        CrmClient crm = email -> System.out.println("  crm: contact " + email);
        Analytics analytics = (event, email) -> System.out.println("  analytics: " + event + " " + email);

        System.out.println("before (3 collaborators):");
        new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.before.RegistrationService(
                mailer, crm, analytics).register("ada@example.com");

        var events = new DomainEventDispatcher<UserRegistered>(error -> System.out.println("  failed: " + error));
        SignUpReactions.subscribe(events, mailer, crm, analytics);
        var service = new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after
                .RegistrationService(events);
        System.out.println("after (1 collaborator):");
        service.register("ada@example.com");

        events.subscribe(UserRegistered.class, registered ->
                System.out.println("  rewards: 100 points for " + registered.email()));
        System.out.println("after, with a fourth reaction and no change to RegistrationService:");
        service.register("alan@example.com");
    }
}
