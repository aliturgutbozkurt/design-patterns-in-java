package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand.LineRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Placed;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config.ShopCompositionRoot;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The use case through a whole composition root; run once per root (memory, file). */
abstract class ShopUseCaseContract {

    protected ShopCompositionRoot shop;

    protected abstract ShopCompositionRoot newRoot();

    @BeforeEach
    void wire() {
        shop = newRoot();
    }

    static LineRequest line(String sku, int quantity) {
        return new LineRequest(new Sku(sku), quantity);
    }

    private PlaceOrderResult place(String customer, LineRequest... lines) {
        return shop.placeOrder().place(new PlaceOrderCommand(customer, List.of(lines)));
    }

    private void assertNothingSavedOrPublished() {
        assertThat(shop.orders().count()).isZero();
        assertThat(shop.events().published()).isEmpty();
    }

    @Test
    void validCommandIsPlacedSavedAndPublishedExactlyOnce() {
        PlaceOrderResult result = place("alice", line("BOOK-1", 2), line("PEN-7", 1));

        assertThat(result).isEqualTo(new Placed(new OrderId("order-1"), Money.of("47.00")));
        Order saved = shop.orders().findById(new OrderId("order-1")).orElseThrow();
        assertThat(saved.customer()).isEqualTo("alice");
        assertThat(saved.total()).isEqualTo(Money.of("47.00"));
        assertThat(shop.acme().charges()).containsExactly("alice 4700");
        assertThat(shop.events().published())
                .containsExactly(new OrderPlaced(new OrderId("order-1"), "alice", Money.of("47.00")));
    }

    @Test
    void unknownSkuIsRejectedAndNothingIsChargedSavedOrPublished() {
        assertThat(place("bob", line("BOOK-1", 1), line("TOY-9", 1))).isEqualTo(new Rejected("unknown product: TOY-9"));
        assertThat(shop.acme().charges()).isEmpty();
        assertNothingSavedOrPublished();
    }

    @Test
    void declinedPaymentIsRejectedAndNothingIsSavedOrPublished() {
        assertThat(place("carol", line("BOOK-1", 30))).isEqualTo(new Rejected("payment declined"));
        assertThat(shop.acme().charges()).containsExactly("carol 60000");
        assertNothingSavedOrPublished();
    }

    @Test
    void emptyCommandIsRejected() {
        assertThat(place("dave")).isEqualTo(new Rejected("empty order"));
        assertThat(place(" ", line("BOOK-1", 1))).isEqualTo(new Rejected("missing customer"));
        assertThat(shop.acme().charges()).isEmpty();
        assertNothingSavedOrPublished();
    }

    @Test
    void nonPositiveQuantityIsRejected() {
        assertThat(place("erin", line("PEN-7", 0))).isEqualTo(new Rejected("invalid quantity: PEN-7"));
        assertNothingSavedOrPublished();
    }

    @Test
    void idsAreConsumedOnlyByPlacedOrders() {
        place("carol", line("BOOK-1", 30));
        assertThat(place("alice", line("MUG-3", 1))).isEqualTo(new Placed(new OrderId("order-1"), Money.of("8.75")));
    }

    @Test
    void commandLineAdapterDrivesTheSameUseCase() {
        assertThat(shop.cli().handle("place alice BOOK-1:2 PEN-7:1")).isEqualTo("PLACED order-1 total 47.00");
        assertThat(shop.cli().handle("place bob TOY-9:1")).isEqualTo("REJECTED unknown product: TOY-9");
        assertThat(shop.orders().count()).isEqualTo(1);
    }
}
