package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.warehouse;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseException;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ShipmentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class WarehouseAdapterTest {

    /** Warehouse API double: records calls; fails pick of a chosen SKU. */
    private static final class RecordingWarehouseApi implements WarehouseApi {
        final List<String> calls = new ArrayList<>();
        String failingSku = "";

        @Override
        public void pick(String orderRef, String sku, int quantity) {
            calls.add("pick " + orderRef + " " + sku + " " + quantity);
            if (sku.equals(failingSku)) {
                throw new WarehouseException("out of boxes");
            }
        }

        @Override
        public String pack(String orderRef) {
            calls.add("pack " + orderRef);
            return "PCL-7";
        }

        @Override
        public String ship(String parcelId, String postalCode) {
            calls.add("ship " + parcelId + " " + postalCode);
            return "TRK-0007";
        }
    }

    private static final List<OrderItem> ITEMS = List.of(
            new OrderItem(new Sku("BOK-001"), "Book", ProductType.PHYSICAL, 2, Money.of("1.00")),
            new OrderItem(new Sku("TOY-001"), "Toy", ProductType.PHYSICAL, 1, Money.of("1.00")));

    @Test
    void picksEveryLineThenPacksThenShips() {
        var api = new RecordingWarehouseApi();

        ShipmentOutcome outcome = new WarehouseAdapter(api).ship(OrderId.of(7), ITEMS, "34710");

        assertThat(outcome).isEqualTo(new ShipmentOutcome.Shipped("TRK-0007"));
        assertThat(api.calls).containsExactly("pick order-7 BOK-001 2", "pick order-7 TOY-001 1", "pack order-7",
                "ship PCL-7 34710");
    }

    @Test
    void warehouseExceptionBecomesAFailedOutcomeAndStopsTheOrder() {
        var api = new RecordingWarehouseApi();
        api.failingSku = "BOK-001";

        assertThat(new WarehouseAdapter(api).ship(OrderId.of(7), ITEMS, "34710"))
                .isEqualTo(new ShipmentOutcome.Failed("out of boxes"));
        assertThat(api.calls).containsExactly("pick order-7 BOK-001 2");
    }
}
