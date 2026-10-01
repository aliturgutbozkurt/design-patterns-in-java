package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/immutability/cart/DefensiveCopyDemo.java}
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public final class DefensiveCopyDemo {

    private DefensiveCopyDemo() {}

    public static void main(String[] args) {
        var mug = new CartLine("MUG-0001", 2, 12_50);
        var tee = new CartLine("TEE-0002", 1, 20_00);

        System.out.println("-- a record is only as immutable as its components");
        var source = new ArrayList<>(List.of(mug));
        var leaky = new LeakyCart(source);
        var cart = new Cart(source);
        source.add(tee);
        System.out.println("after source.add(TEE): LeakyCart has " + leaky.lines().size()
                + " line(s), Cart has " + cart.lines().size() + " line(s)");
        System.out.println("leaky.lines() == source: " + (leaky.lines() == source));
        try {
            cart.lines().add(tee);
        } catch (UnsupportedOperationException e) {
            System.out.println("cart.lines().add(...) -> " + e.getClass().getSimpleName());
        }

        System.out.println("-- change = a new value (withers)");
        var original = new Cart(List.of(mug, tee));
        print("original       ", original);
        print("withLine(CAP)  ", original.withLine(new CartLine("CAP-0003", 1, 8_00)));
        print("withoutSku(MUG)", original.withoutSku("MUG-0001"));
        print("withQuantity 3 ", original.withQuantity("TEE-0002", 3));
        print("original again ", original);

        System.out.println("-- view vs. copy");
        var letters = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(letters);
        List<String> copy = List.copyOf(letters);
        letters.add("b");
        System.out.println("unmodifiableList view: " + view);
        System.out.println("List.copyOf copy:      " + copy);
    }

    private static void print(String label, Cart cart) {
        System.out.println(label + cart.lines() + " total " + cart.totalCents());
    }
}
