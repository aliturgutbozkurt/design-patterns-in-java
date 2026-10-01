package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications;

/**
 * Side effect 2 of a sign-up: a contact in the CRM.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
@FunctionalInterface
public interface CrmClient {

    void createContact(String email);
}
