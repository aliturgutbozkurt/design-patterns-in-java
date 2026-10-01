package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications;

/**
 * Side effect 3 of a sign-up: an analytics event.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
@FunctionalInterface
public interface Analytics {

    void track(String event, String email);
}
