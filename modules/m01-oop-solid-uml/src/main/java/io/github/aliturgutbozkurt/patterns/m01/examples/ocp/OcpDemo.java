package io.github.aliturgutbozkurt.patterns.m01.examples.ocp;

import static io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRules.percentOff;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.Checkout;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after.DiscountRule;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.before.PriceCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/ocp/OcpDemo.java}
 *
 * @see "m01 lesson, section OCP"
 */
public final class OcpDemo {

    private OcpDemo() {}

    public static void main(String[] args) {
        var amount = new BigDecimal("250");

        System.out.println("== before: if/else on a customer-type string ==");
        var calculator = new PriceCalculator();
        for (String type : List.of("REGULAR", "STUDENT", "VIP")) {
            System.out.println(type + " pays " + calculator.price(type, amount));
        }

        System.out.println("== after: discount rules are values ==");
        var checkouts = new LinkedHashMap<String, Checkout>();
        checkouts.put("REGULAR", new Checkout(List.of()));
        checkouts.put("STUDENT", new Checkout(List.of(percentOff(10))));
        checkouts.put("VIP", new Checkout(List.of(percentOff(15))));
        checkouts.forEach((type, checkout) -> System.out.println(type + " pays " + checkout.price(amount)));

        DiscountRule roundDownToWholeLira = price -> price.setScale(0, RoundingMode.FLOOR);
        var blackFriday = new Checkout(List.of(percentOff(15), roundDownToWholeLira));
        System.out.println("VIP_BLACK_FRIDAY pays " + blackFriday.price(amount) + " (new lambda rule, Checkout unchanged)");
    }
}
