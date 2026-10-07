package io.github.aliturgutbozkurt.patterns.capstone.api.external;

import java.util.Objects;

/**
 * GIVEN — do not modify. One message to a recipient: a customer id ({@code alice}) or {@code ops}.
 *
 * @param recipient who receives it
 * @param subject   e.g. {@code Order order-1 confirmed}
 * @param body      the text
 * @see "capstone brief, Business rules — Events and notifications (F8)"
 */
public record Notification(String recipient, String subject, String body) {

    public Notification {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(body, "body");
    }
}
