package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CompositionRootTest {

    static final Instant NOON = Instant.parse("2026-10-01T12:00:00Z");

    @Test
    void productionRootBuildsAWorkingGraph() {
        ShopApp app = CompositionRoot.production();
        OrderRecord order = app.checkout().checkout("ada", "keyboard", 2);
        assertThat(order.total()).isEqualTo(new BigDecimal("99.80"));
        assertThat(order.receipt()).isEqualTo("PAY-1");
    }

    @Test
    void eachCollaboratorIsCreatedOnceAndShared() {
        ShopApp app = CompositionRoot.production();
        app.checkout().checkout("ada", "mouse", 1);
        app.checkout().checkout("alan", "keyboard", 1);
        assertThat(app.orders().all()).hasSize(2);
    }

    @Test
    void testRootUsesFakesForPredictableResults() {
        List<String> charges = new ArrayList<>();
        PaymentGateway recording = (customer, amount) -> {
            charges.add(customer + " " + amount);
            return "TEST-RECEIPT";
        };
        ShopApp app = CompositionRoot.forTests(recording, Clock.fixed(NOON, ZoneOffset.UTC));
        OrderRecord order = app.checkout().checkout("ada", "mouse", 3);
        assertThat(order).isEqualTo(new OrderRecord("ada", "mouse", 3, new BigDecimal("59.97"), "TEST-RECEIPT", NOON));
        assertThat(charges).containsExactly("ada 59.97");
        assertThat(app.orders().all()).containsExactly(order);
    }

    @Test
    void unknownItemIsRejectedBeforeCharging() {
        List<String> charges = new ArrayList<>();
        ShopApp app = CompositionRoot.forTests((customer, amount) -> {
            charges.add(customer);
            return "R";
        }, Clock.fixed(NOON, ZoneOffset.UTC));
        assertThatIllegalArgumentException().isThrownBy(() -> app.checkout().checkout("ada", "piano", 1))
                .withMessage("unknown item: piano");
        assertThat(charges).isEmpty();
    }

    @Test
    void demoPrintsTheCheckouts() {
        assertThat(Console.capture(() -> CompositionRootDemo.main(new String[0]))).isEqualTo("""
                ada bought 2 x keyboard for 99.80 (receipt PAY-1)
                alan bought 1 x monitor for 229.00 (receipt PAY-2)
                orders stored: 2
                """);
    }
}
