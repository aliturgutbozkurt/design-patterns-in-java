package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.before;

import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Analytics;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.CrmClient;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Mailer;
import java.util.Objects;

/**
 * Before: registration calls every reaction itself. A fourth reaction means a fourth constructor parameter and a
 * change to this class.
 *
 * @see "m11 lesson, section Refactoring to patterns — replace hard-wired calls with events"
 */
public final class RegistrationService {

    private final Mailer mailer;
    private final CrmClient crm;
    private final Analytics analytics;

    public RegistrationService(Mailer mailer, CrmClient crm, Analytics analytics) {
        this.mailer = Objects.requireNonNull(mailer, "mailer");
        this.crm = Objects.requireNonNull(crm, "crm");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
    }

    public void register(String email) {
        if (!Objects.requireNonNull(email, "email").contains("@")) {
            throw new IllegalArgumentException("invalid e-mail: " + email);
        }
        mailer.sendWelcome(email);
        crm.createContact(email);
        analytics.track("signup", email);
    }
}
