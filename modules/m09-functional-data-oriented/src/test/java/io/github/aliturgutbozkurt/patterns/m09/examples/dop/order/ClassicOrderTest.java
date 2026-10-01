package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic.LegacyOrderService;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic.MutableOrder;
import org.junit.jupiter.api.Test;

/** The "before" picture: every test here documents a state the classic model should not allow, but does. */
class ClassicOrderTest {

    private final LegacyOrderService service = new LegacyOrderService();

    @Test
    void acceptsShippedWithANullTrackingCode() {
        var order = new MutableOrder("A-1", 45_00);
        order.setStatus("SHIPPED");
        assertThat(order.getTrackingCode()).isNull();
        assertThat(service.describe(order)).isEqualTo("A-1: shipped, tracking null");
    }

    @Test
    void acceptsATypoStatusSilentlyAndDescribeFallsThroughToUnknown() {
        var order = new MutableOrder("A-2", 10_00);
        order.setStatus("SHIPED");
        assertThat(order.getStatus()).isEqualTo("SHIPED");
        assertThat(service.describe(order)).isEqualTo("A-2: unknown");
    }

    @Test
    void anOrderCanBeCancelledAndHaveATrackingCodeAtTheSameTime() {
        var order = new MutableOrder("A-3", 10_00);
        service.place(order);
        service.pay(order, "PAY-1");
        service.ship(order, "TRK-1");
        order.setStatus("CANCELLED");
        order.setCancelReason("lost in transit");
        assertThat(order.getTrackingCode()).isEqualTo("TRK-1");
        assertThat(service.describe(order)).isEqualTo("A-3: cancelled (lost in transit)");
    }

    @Test
    void everyOperationReChecksTheStatusStringAtRunTime() {
        var order = new MutableOrder("A-4", 10_00);
        assertThatIllegalStateException().isThrownBy(() -> service.ship(order, "TRK-2"))
                .withMessage("cannot ship A-4 in status DRAFT");
        service.place(order);
        service.pay(order, "PAY-2");
        service.ship(order, "TRK-2");
        assertThat(service.describe(order)).isEqualTo("A-4: shipped, tracking TRK-2");
    }
}
