package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Book;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Electronics;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Grocery;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartOperations;
import java.util.List;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/visitor/ModernCartDemo.java} */
public final class ModernCartDemo {

    private ModernCartDemo() {}

    public static void main(String[] args) {
        List<CartItem> cart = List.of(
                new Book("Refactoring", 4500),
                new Electronics("Headphones", 12000, 350),
                new Grocery("Coffee beans", 2400, 500),
                new Electronics("Fridge", 49900, 35_000));

        cart.forEach(item -> System.out.println(CartOperations.receipt(item)));
        System.out.println("tax:             " + CartOperations.money(CartOperations.totalTax(cart)));
        System.out.println("shipping weight: " + cart.stream().mapToInt(CartOperations::shippingWeight).sum() + " g");

        CartItem first = cart.getFirst(); // a Book, held as CartItem: the switch still sees the Book
        System.out.println("describe(CartItem) for a Book: " + CartOperations.describe(first));
    }
}
