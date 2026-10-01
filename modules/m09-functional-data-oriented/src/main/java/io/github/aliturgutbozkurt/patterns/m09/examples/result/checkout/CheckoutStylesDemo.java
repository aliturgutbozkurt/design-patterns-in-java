package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/result/checkout/CheckoutStylesDemo.java}
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class CheckoutStylesDemo {

    private CheckoutStylesDemo() {}

    public static void main(String[] args) {
        var mugs = new CartItem("MUG-0001", 2, 12_50);
        var tee = new CartItem("TEE-0002", 1, 20_00);
        var happy = new CheckoutRequest(List.of(mugs, tee), "", "card-ok");
        var shortStock = new CheckoutRequest(List.of(new CartItem("TEE-0002", 2, 20_00)), "", "card-ok");
        var declined = new CheckoutRequest(List.of(mugs), "", "card-declined");

        for (var scenario : List.of(Map.entry("happy path", happy), Map.entry("short stock", shortStock),
                Map.entry("declined", declined))) {
            System.out.println("-- " + scenario.getKey());
            CheckoutRequest request = scenario.getValue();
            String byException;
            try {
                byException = new ExceptionCheckout(inventory(), gateway()).checkout(request).toString();
            } catch (CheckoutException e) {
                byException = "threw " + e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            System.out.println("exceptions: " + byException);
            System.out.println("Optional:   " + new OptionalCheckout(inventory(), gateway()).checkout(request));
            System.out.println("Result:     " + new ResultCheckout(inventory(), gateway()).checkout(request));
        }

        System.out.println("-- the Result caller must handle every kind (exhaustive switch, no default)");
        for (var scenario : List.of(Map.entry("short stock", shortStock), Map.entry("declined   ", declined))) {
            String text = new ResultCheckout(inventory(), gateway()).checkout(scenario.getValue())
                    .fold(receipt -> "paid " + receipt.paymentId(), CheckoutErrors::message);
            System.out.println(scenario.getKey() + " -> " + text);
        }
    }

    private static InMemoryInventory inventory() {
        return new InMemoryInventory(Map.of("MUG-0001", 5, "TEE-0002", 1));
    }

    private static ScriptedPaymentGateway gateway() {
        return new ScriptedPaymentGateway(Set.of("card-declined"));
    }
}
