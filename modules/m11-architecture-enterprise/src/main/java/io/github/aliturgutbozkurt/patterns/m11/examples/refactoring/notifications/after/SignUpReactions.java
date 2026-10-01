package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Analytics;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.CrmClient;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Mailer;

/**
 * Wiring of the three existing reactions — the list that used to be hard-coded inside the service now lives where
 * the application is assembled.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
public final class SignUpReactions {

    private SignUpReactions() {}

    public static void subscribe(DomainEventDispatcher<UserRegistered> events, Mailer mailer, CrmClient crm,
                                 Analytics analytics) {
        events.subscribe(UserRegistered.class, registered -> mailer.sendWelcome(registered.email()));
        events.subscribe(UserRegistered.class, registered -> crm.createContact(registered.email()));
        events.subscribe(UserRegistered.class, registered -> analytics.track("signup", registered.email()));
    }
}
