package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after;

import java.util.Objects;

/**
 * Domain event: a user signed up. The service announces it; it no longer knows who reacts.
 *
 * @param email the new user's address
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
public record UserRegistered(String email) {

    public UserRegistered {
        Objects.requireNonNull(email, "email");
    }
}
