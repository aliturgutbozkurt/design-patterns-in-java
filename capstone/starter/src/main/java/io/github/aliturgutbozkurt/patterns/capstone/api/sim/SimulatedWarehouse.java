package io.github.aliturgutbozkurt.patterns.capstone.api.sim;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseException;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

/**
 * GIVEN — do not modify. A deterministic, thread-safe stand-in for the warehouse: every call blocks for 20 ms; the
 * parcel of {@code order-7} is {@code PCL-7} and its tracking code {@code TRK-0007}, whatever the thread interleaving.
 *
 * @see "capstone brief, Business rules — Fulfilment (F9)"
 */
public final class SimulatedWarehouse implements WarehouseApi {

    private static final Duration LATENCY = Duration.ofMillis(20);

    @Override
    public void pick(String orderRef, String sku, int quantity) {
        Objects.requireNonNull(orderRef, "orderRef");
        Objects.requireNonNull(sku, "sku");
        pause();
    }

    @Override
    public String pack(String orderRef) {
        long number = new OrderId(Objects.requireNonNull(orderRef, "orderRef")).number();
        pause();
        return "PCL-" + number;
    }

    @Override
    public String ship(String parcelId, String postalCode) {
        Objects.requireNonNull(postalCode, "postalCode");
        if (!Objects.requireNonNull(parcelId, "parcelId").matches("PCL-[0-9]+")) {
            throw new WarehouseException("unknown parcel: " + parcelId);
        }
        pause();
        return String.format(Locale.ROOT, "TRK-%04d", Long.parseLong(parcelId.substring("PCL-".length())));
    }

    private static void pause() {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WarehouseException("interrupted", e);
        }
    }
}
