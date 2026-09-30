package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import java.util.Objects;

/**
 * After: one collaborator. Registration raises {@link UserRegistered}; mail, CRM and analytics subscribe to it, and a
 * new reaction is a new subscription — this class does not change.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
public final class RegistrationService {

    private final DomainEventDispatcher<UserRegistered> events;

    public RegistrationService(DomainEventDispatcher<UserRegistered> events) {
        this.events = Objects.requireNonNull(events, "events");
    }

    public void register(String email) {
        if (!Objects.requireNonNull(email, "email").contains("@")) {
            throw new IllegalArgumentException("invalid e-mail: " + email);
        }
        events.dispatch(new UserRegistered(email));
    }
}
