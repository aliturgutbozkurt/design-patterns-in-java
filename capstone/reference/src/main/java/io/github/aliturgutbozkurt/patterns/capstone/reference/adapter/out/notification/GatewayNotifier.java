package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.notification;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.Notification;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.NotificationGateway;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Notifier;
import java.util.Objects;

/**
 * Outbound adapter: the {@link Notifier} port over the external notification gateway.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "outbound adapter")
public final class GatewayNotifier implements Notifier {

    private final NotificationGateway gateway;

    public GatewayNotifier(NotificationGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    @Override
    public void notify(String recipient, String subject, String body) {
        gateway.send(new Notification(recipient, subject, body));
    }
}
