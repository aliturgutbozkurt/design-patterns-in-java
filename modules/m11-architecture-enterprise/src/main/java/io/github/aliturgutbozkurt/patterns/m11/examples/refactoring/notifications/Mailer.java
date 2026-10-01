package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications;

/**
 * Side effect 1 of a sign-up: the welcome e-mail.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
@FunctionalInterface
public interface Mailer {

    void sendWelcome(String email);
}
