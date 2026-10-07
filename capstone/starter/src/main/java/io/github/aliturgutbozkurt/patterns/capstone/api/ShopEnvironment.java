package io.github.aliturgutbozkurt.patterns.capstone.api;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.ExternalPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.NotificationGateway;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseApi;
import java.time.Clock;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * GIVEN — do not modify. Everything a shop gets from outside: time, the external systems, an error sink and settings.
 * Never read time anywhere but from {@code clock}.
 *
 * @param clock         the only source of time (and of the time zone that decides dates)
 * @param payments      the payment provider
 * @param warehouse     the warehouse
 * @param notifications the notification gateway
 * @param errors        receives exceptions thrown by event handlers
 * @param settings      merchant id and limits
 * @see "capstone brief, What you are given"
 */
public record ShopEnvironment(Clock clock, ExternalPaymentApi payments, WarehouseApi warehouse,
                              NotificationGateway notifications, Consumer<Throwable> errors, ShopSettings settings) {

    public ShopEnvironment {
        Objects.requireNonNull(clock, "clock");
        Objects.requireNonNull(payments, "payments");
        Objects.requireNonNull(warehouse, "warehouse");
        Objects.requireNonNull(notifications, "notifications");
        Objects.requireNonNull(errors, "errors");
        Objects.requireNonNull(settings, "settings");
    }
}
