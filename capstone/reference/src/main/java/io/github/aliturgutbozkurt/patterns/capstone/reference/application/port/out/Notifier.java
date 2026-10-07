package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * Outbound port: sends a message to a customer or to {@code ops}; how (e-mail, SMS, …) is the adapter's business.
 *
 * @see "capstone guide §1 Pattern map — Observer"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "outbound port")
@FunctionalInterface
public interface Notifier {

    /** Sends one message. */
    void notify(String recipient, String subject, String body);
}
