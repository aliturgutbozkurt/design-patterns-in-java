package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import java.util.Objects;

/**
 * What the warehouse did with one order.
 *
 * @see "capstone guide, Pattern map — Thread-per-task"
 */
public sealed interface ShipmentOutcome {

    /** @param trackingCode the carrier's tracking code */
    record Shipped(String trackingCode) implements ShipmentOutcome {
        public Shipped {
            Objects.requireNonNull(trackingCode, "trackingCode");
        }
    }

    /** @param reason why the warehouse could not ship it */
    record Failed(String reason) implements ShipmentOutcome {
        public Failed {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
