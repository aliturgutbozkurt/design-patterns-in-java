package io.github.aliturgutbozkurt.patterns.capstone.api.external;

/**
 * GIVEN — do not modify. Sends notifications (e-mail, SMS, … — PatternShop does not care which).
 *
 * @see "capstone brief §2.2 — Events and notifications (F8)"
 */
@FunctionalInterface
public interface NotificationGateway {

    /** Sends one notification. */
    void send(Notification notification);
}
