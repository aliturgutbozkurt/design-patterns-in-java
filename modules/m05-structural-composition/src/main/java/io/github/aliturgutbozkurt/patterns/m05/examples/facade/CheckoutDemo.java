package io.github.aliturgutbozkurt.patterns.m05.examples.facade;

import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Address;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Card;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Cart;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.CheckoutFacade;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Inventory;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.PaymentGateway;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Placed;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Rejected;
import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.ShippingService;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/facade/CheckoutDemo.java}
 *
 * @see "m05 lesson, section Facade"
 */
public final class CheckoutDemo {

    private CheckoutDemo() {}

    public static void main(String[] args) {
        var inventory = new Inventory(Map.of("mug", 10, "tshirt", 2));
        var payments = new PaymentGateway(Set.of("4000000000000002"));
        var checkout = new CheckoutFacade(inventory, payments, new ShippingService(Set.of("TR", "DE")));

        var good = new Card("4111111111111111");
        var declined = new Card("4000000000000002");
        var istanbul = new Address("Ayşe", "Istanbul", "TR");
        var mugs = Cart.of(new Cart.Line("mug", 3, 1999));

        print(1, checkout.placeOrder(mugs, good, istanbul));
        print(2, checkout.placeOrder(Cart.of(new Cart.Line("tshirt", 5, 2500)), good, istanbul));
        print(3, checkout.placeOrder(mugs, declined, istanbul));
        print(4, checkout.placeOrder(mugs, good, new Address("Nemo", "Atlantis", "AQ")));

        System.out.println("stock left: mug " + inventory.available("mug") + ", tshirt "
                + inventory.available("tshirt"));
        System.out.println("charges " + payments.chargeCount() + ", refunds " + payments.refundCount()
                + ", net charged " + euros(payments.netChargedCents()));
    }

    private static void print(int order, CheckoutResult result) {
        String outcome = switch (result) {
            case Placed(var tracking, var total) -> "placed, tracking " + tracking + ", charged " + euros(total);
            case Rejected(var reason, var detail) -> "rejected " + reason + " (" + detail + ")";
        };
        System.out.println("order " + order + ": " + outcome);
    }

    private static String euros(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }
}
