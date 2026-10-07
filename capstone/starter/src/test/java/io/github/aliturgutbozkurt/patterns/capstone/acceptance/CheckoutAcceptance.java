package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.CARD;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.address;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.money;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.noAddress;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.orderId;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult.Placed;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult.Rejected;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedPaymentApi;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** F5, F6 — checkout: validation chain, payment through the external API, placing the order (brief, Business rules). */
public abstract class CheckoutAcceptance extends AcceptanceContract {

    private CheckoutResult checkout(CartId cart, Address address, String cardToken) {
        return shop().checkout().checkout(new CheckoutRequest(cart, address, cardToken));
    }

    /** The cart of the brief's worked example with coupon AUTUMN5 (total 987.91). */
    private CartId workedExampleCart() {
        CartId cart = kit().cartWith("alice",
                item("BOK-001", 2), item("BOK-002", 1), item("TOY-001", 3), item("DIG-001", 1));
        shop().carts().applyCoupon(cart, "AUTUMN5");
        return cart;
    }

    private static Rejected rejected(String... reasons) {
        return new Rejected(List.of(reasons));
    }

    @Test
    void validCheckoutPlacesAPaidOrder() {
        CheckoutResult result = kit().checkout(workedExampleCart());

        assertThat(result).isEqualTo(new Placed(orderId("order-1"), money("987.91"), "txn-1"));
        OrderView order = kit().order(orderId("order-1"));
        assertThat(order.customer()).isEqualTo(customer("alice"));
        assertThat(order.status()).isEqualTo(OrderStatus.PAID);
        assertThat(order.total()).isEqualTo(money("987.91"));
        assertThat(order.paymentReference()).isEqualTo("txn-1");
        assertThat(order.trackingCode()).isEmpty();
        assertThat(order.placedAt()).isEqualTo(kit().clock().instant());
        assertThat(order.lines()).containsExactly(
                new OrderLine(sku("BOK-001"), "Design Patterns Handbook", ProductType.PHYSICAL, 2, money("250.00")),
                new OrderLine(sku("BOK-002"), "Java 27 in Action", ProductType.PHYSICAL, 1, money("400.00")),
                new OrderLine(sku("TOY-001"), "Pattern Puzzle", ProductType.PHYSICAL, 3, money("120.00")),
                new OrderLine(sku("DIG-001"), "E-book Bundle", ProductType.DIGITAL, 1, money("99.90")));
    }

    @Test
    void chargesTheQuotedTotalExactlyOnceInProviderFormat() {
        kit().checkout(workedExampleCart());

        assertThat(kit().payments().calls()).containsExactly(new SandboxPaymentApi.Call("authorize",
                List.of("PATTERNSHOP", CARD, "987.91", "TRY", "cart-1")));
    }

    @Test
    void checkoutReservesStockAndClosesTheCart() {
        CartId cart = workedExampleCart();

        kit().checkout(cart);

        assertThat(kit().stock("BOK-001")).isEqualTo(18);
        assertThat(kit().stock("BOK-002")).isEqualTo(7);
        assertThat(kit().stock("TOY-001")).isEqualTo(3);
        assertThat(kit().stock("DIG-001")).isZero();
        CartUseCase carts = shop().carts();
        assertThat(carts.view(cart).open()).isFalse();
        assertThatIllegalStateException().isThrownBy(() -> carts.add(cart, sku("HOM-001"), 1))
                .withMessage("cart closed: cart-1");
        assertThatIllegalStateException().isThrownBy(() -> carts.remove(cart, sku("BOK-001")))
                .withMessage("cart closed: cart-1");
        assertThatIllegalStateException().as("undo of a closed cart").isThrownBy(() -> carts.undo(cart))
                .withMessage("cart closed: cart-1");
        assertThatIllegalStateException().as("redo of a closed cart, even with nothing to redo")
                .isThrownBy(() -> carts.redo(cart)).withMessage("cart closed: cart-1");
    }

    @Test
    void rejectsEmptyCart() {
        CartId cart = shop().carts().open(customer("alice"));

        assertThat(checkout(cart, noAddress(), "")).as("the only reason, whatever else is missing")
                .isEqualTo(rejected("empty cart"));
    }

    @Test
    void rejectsMissingAddressOnlyForPhysicalItems() {
        CartId physical = kit().cartWith("alice", item("HOM-001", 1), item("DIG-001", 1));
        assertThat(checkout(physical, new Address("Alice Doe", "Bagdat Cd. 1", " ", "34710"), CARD))
                .isEqualTo(rejected("missing address"));

        CartId digital = kit().cartWith("bob", item("DIG-001", 2));
        assertThat(checkout(digital, noAddress(), CARD)).isInstanceOf(Placed.class);
    }

    @Test
    void rejectsQuantityAboveLimitPerSku() {
        CartId eleven = kit().cartWith("alice", item("HOM-001", 6), item("HOM-001", 5));
        assertThat(kit().checkout(eleven)).isEqualTo(rejected("quantity limit exceeded: HOM-001"));

        CartId ten = kit().cartWith("bob", item("HOM-001", 10), item("DIG-001", 10));
        assertThat(kit().checkout(ten)).as("10 units of one SKU are allowed").isInstanceOf(Placed.class);
    }

    @Test
    void rejectsInsufficientStockNamingTheSku() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1), item("TOY-001", 7));

        assertThat(kit().checkout(cart)).isEqualTo(rejected("insufficient stock: TOY-001"));

        shop().carts().changeQuantity(cart, sku("TOY-001"), 6);
        assertThat(kit().checkout(cart)).as("exactly the stock is fine").isInstanceOf(Placed.class);
    }

    @Test
    void rejectsExpiredCouponAndMissingCardToken() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1));
        shop().carts().applyCoupon(cart, "AUTUMN5");
        kit().clock().advance(Duration.ofDays(46)); // 2027-01-01

        assertThat(checkout(cart, address(), " ")).isEqualTo(rejected("expired coupon: AUTUMN5", "missing card token"));
    }

    @Test
    void collectsAllValidationErrorsInRuleOrder() {
        CartId cart = kit().cartWith("alice", item("ELE-001", 11), item("TOY-001", 7), item("DIG-001", 1));
        shop().carts().applyCoupon(cart, "AUTUMN5");
        kit().clock().advance(Duration.ofDays(46));

        assertThat(checkout(cart, noAddress(), "")).isEqualTo(rejected(
                "missing address",
                "quantity limit exceeded: ELE-001",
                "insufficient stock: ELE-001",
                "insufficient stock: TOY-001",
                "expired coupon: AUTUMN5",
                "missing card token"));
    }

    @Test
    void invalidCheckoutNeverCallsThePaymentApi() {
        kit().checkout(shop().carts().open(customer("alice")));
        kit().checkout(kit().cartWith("bob", item("TOY-001", 7)));
        checkout(kit().cartWith("carol", item("BOK-001", 1)), noAddress(), CARD);

        assertThat(kit().payments().calls()).isEmpty();
    }

    @Test
    void declinedPaymentPlacesNoOrderAndKeepsStockAndCart() {
        List<ShopEvent> events = new ArrayList<>();
        shop().events().subscribe(ShopEvent.class, events::add);
        CartId cart = kit().cartWith("alice", item("TOY-001", 3));

        assertThat(checkout(cart, address(), SimulatedPaymentApi.DECLINED)).isEqualTo(rejected("payment declined"));

        assertThat(shop().orders().find(orderId("order-1"))).isEmpty();
        assertThat(shop().orders().ordersOf(customer("alice"))).isEmpty();
        assertThat(kit().stock("TOY-001")).isEqualTo(6);
        assertThat(shop().carts().view(cart).open()).isTrue();
        assertThat(events).isEmpty();
        assertThat(kit().notifications().all()).isEmpty();

        assertThat(kit().checkout(cart)).as("the same cart can be paid with another card")
                .isEqualTo(new Placed(orderId("order-1"), money("289.90"), "txn-1"));
    }

    @Test
    void providerErrorIsReportedAsPaymentUnavailable() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1));

        assertThat(checkout(cart, address(), SimulatedPaymentApi.UNAVAILABLE))
                .isEqualTo(rejected("payment unavailable"));
        assertThat(shop().carts().view(cart).open()).isTrue();
        assertThat(kit().stock("BOK-001")).isEqualTo(20);
    }

    @Test
    void orderIdsAreConsumedOnlyByPlacedOrders() {
        kit().checkout(shop().carts().open(customer("alice")));
        checkout(kit().cartWith("alice", item("BOK-001", 1)), address(), SimulatedPaymentApi.DECLINED);
        checkout(kit().cartWith("alice", item("BOK-001", 1)), address(), SimulatedPaymentApi.UNAVAILABLE);

        assertThat(kit().placeOrder("alice", item("BOK-001", 1))).isEqualTo(orderId("order-1"));
        assertThat(kit().placeOrder("bob", item("HOM-001", 1))).isEqualTo(orderId("order-2"));
    }

    @Test
    void freeOrderIsPlacedWithoutCallingThePaymentApi() {
        shop().pricing().addPromotion(new PromotionSpec.Coupon("FREEBIE", 100, LocalDate.of(2026, 12, 31)));
        CartId cart = kit().cartWith("alice", item("DIG-001", 1));
        shop().carts().applyCoupon(cart, "FREEBIE");

        assertThat(kit().checkout(cart)).isEqualTo(new Placed(orderId("order-1"), Money.ZERO, "FREE"));
        assertThat(kit().payments().calls()).isEmpty();
        OrderView order = kit().order(orderId("order-1"));
        assertThat(order.status()).isEqualTo(OrderStatus.PAID);
        assertThat(order.paymentReference()).isEqualTo("FREE");
    }
}
