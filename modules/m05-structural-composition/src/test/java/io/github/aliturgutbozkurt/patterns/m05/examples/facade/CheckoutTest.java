package io.github.aliturgutbozkurt.patterns.m05.examples.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Address;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Card;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Cart;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.CheckoutFacade;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Inventory;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.PaymentGateway;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Placed;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Rejected;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Rejected.Reason;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.ShippingService;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CheckoutTest {

    private static final Card GOOD_CARD = new Card("4111111111111111");
    private static final Card DECLINED_CARD = new Card("4000000000000002");
    private static final Address ISTANBUL = new Address("Ayşe", "Istanbul", "TR");
    private static final Address NOWHERE = new Address("Nemo", "Atlantis", "AQ");

    private final Inventory inventory = new Inventory(Map.of("mug", 10, "tshirt", 2));
    private final PaymentGateway payments = new PaymentGateway(Set.of(DECLINED_CARD.number()));
    private final ShippingService shipping = new ShippingService(Set.of("TR", "DE"));
    private final CheckoutFacade checkout = new CheckoutFacade(inventory, payments, shipping);

    private static Cart threeMugs() {
        return Cart.of(new Cart.Line("mug", 3, 1999));
    }

    @Test
    void happyPathReservesChargesTheExactTotalAndShips() {
        CheckoutResult result = checkout.placeOrder(
                Cart.of(new Cart.Line("mug", 3, 1999), new Cart.Line("tshirt", 1, 2500)), GOOD_CARD, ISTANBUL);
        assertThat(result).isEqualTo(new Placed("TRK-1001", 3 * 1999 + 2500));
        assertThat(inventory.available("mug")).isEqualTo(7);
        assertThat(inventory.available("tshirt")).isEqualTo(1);
        assertThat(payments.netChargedCents()).isEqualTo(8497);
    }

    @Test
    void outOfStockIsRejectedWithoutACharge() {
        CheckoutResult result = checkout.placeOrder(Cart.of(new Cart.Line("tshirt", 5, 2500)), GOOD_CARD, ISTANBUL);
        assertThat(result).isInstanceOf(Rejected.class)
                .extracting(r -> ((Rejected) r).reason()).isEqualTo(Reason.OUT_OF_STOCK);
        assertThat(payments.netChargedCents()).isZero();
        assertThat(payments.chargeCount()).isZero();
        assertThat(inventory.available("tshirt")).isEqualTo(2);
    }

    @Test
    void declinedPaymentReleasesTheReservation() {
        CheckoutResult result = checkout.placeOrder(threeMugs(), DECLINED_CARD, ISTANBUL);
        assertThat(result).isEqualTo(new Rejected(Reason.PAYMENT_DECLINED, "card ending 0002 declined"));
        assertThat(inventory.available("mug")).isEqualTo(10);
        assertThat(payments.netChargedCents()).isZero();
    }

    @Test
    void shippingFailureRefundsThePaymentAndReleasesTheReservation() {
        CheckoutResult result = checkout.placeOrder(threeMugs(), GOOD_CARD, NOWHERE);
        assertThat(result).isEqualTo(new Rejected(Reason.SHIPPING_UNAVAILABLE, "no shipping to AQ"));
        assertThat(payments.chargeCount()).isEqualTo(1);
        assertThat(payments.refundCount()).isEqualTo(1);
        assertThat(payments.netChargedCents()).isZero();
        assertThat(inventory.available("mug")).isEqualTo(10);
    }

    @Test
    void stockNeverGoesNegative() {
        for (int i = 0; i < 5; i++) {
            checkout.placeOrder(threeMugs(), GOOD_CARD, ISTANBUL);
        }
        assertThat(inventory.available("mug")).isEqualTo(1);
        assertThat(payments.chargeCount()).isEqualTo(3);
        assertThat(inventory.reserve(Map.of("mug", 2))).isEmpty();
        assertThat(inventory.reserve(Map.of("unknown", 1))).isEmpty();
        assertThat(inventory.available("mug")).isEqualTo(1);
    }

    @Test
    void reservationIsAllOrNothing() {
        assertThat(inventory.reserve(Map.of("mug", 1, "tshirt", 3))).isEmpty();
        assertThat(inventory.available("mug")).isEqualTo(10);
    }

    @Test
    void rejectsInvalidInput() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Cart.Line("mug", 0, 100));
        assertThatIllegalArgumentException().isThrownBy(() -> new Cart.Line("mug", 1, -1));
        assertThatIllegalArgumentException().isThrownBy(Cart::of);
        assertThatIllegalArgumentException().isThrownBy(() -> new Card("12"));
    }

    @Test
    void demoPrintsEveryOutcomeAndTheFinalState() {
        assertThat(Console.capture(() -> CheckoutDemo.main(new String[0]))).isEqualTo("""
                order 1: placed, tracking TRK-1001, charged 59.97
                order 2: rejected OUT_OF_STOCK (not enough stock)
                order 3: rejected PAYMENT_DECLINED (card ending 0002 declined)
                order 4: rejected SHIPPING_UNAVAILABLE (no shipping to AQ)
                stock left: mug 7, tshirt 2
                charges 2, refunds 1, net charged 59.97
                """);
    }
}
