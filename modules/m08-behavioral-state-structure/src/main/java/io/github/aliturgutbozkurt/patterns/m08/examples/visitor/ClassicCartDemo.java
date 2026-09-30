package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Book;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.CartItem;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Electronics;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Grocery;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.OverloadTrap;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ReceiptVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ShippingWeightVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.TaxVisitor;
import java.util.List;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/visitor/ClassicCartDemo.java} */
public final class ClassicCartDemo {

    private ClassicCartDemo() {}

    public static void main(String[] args) {
        List<CartItem> cart = List.of(
                new Book("Refactoring", 4500),
                new Electronics("Headphones", 12000, 350),
                new Grocery("Coffee beans", 2400, 500));

        var receipt = new ReceiptVisitor();
        cart.forEach(item -> System.out.println(item.accept(receipt)));
        var tax = new TaxVisitor();
        System.out.println("tax:             " + ReceiptVisitor.money(cart.stream().mapToLong(i -> i.accept(tax)).sum()));
        var weight = new ShippingWeightVisitor();
        System.out.println("shipping weight: " + cart.stream().mapToInt(i -> i.accept(weight)).sum() + " g");

        CartItem first = cart.getFirst(); // a Book, but the static type is CartItem
        System.out.println("describe(CartItem) for a Book: " + OverloadTrap.describe(first));
        System.out.println("accept -> visitBook:          " + OverloadTrap.describeWithVisitor(first));
    }
}
