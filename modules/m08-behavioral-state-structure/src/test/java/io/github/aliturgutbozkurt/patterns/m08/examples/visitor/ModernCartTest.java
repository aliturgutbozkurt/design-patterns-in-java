package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.OverloadTrap;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ReceiptVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ShippingWeightVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.TaxVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Book;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Electronics;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Grocery;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartOperations;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ModernCartTest {

    /** One item described once, so the same cart can be built in both packages. */
    record Spec(String kind, String name, long price, int grams) {

        io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.CartItem classic() {
            return switch (kind) {
                case "book" -> new io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Book(name,
                        price);
                case "electronics" -> new io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic
                        .Electronics(name, price, grams);
                default -> new io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Grocery(name,
                        price, grams);
            };
        }

        CartItem modern() {
            return switch (kind) {
                case "book" -> new Book(name, price);
                case "electronics" -> new Electronics(name, price, grams);
                default -> new Grocery(name, price, grams);
            };
        }
    }

    static Stream<List<Spec>> carts() {
        return Stream.of(
                List.of(new Spec("book", "Refactoring", 4500, 0), new Spec("electronics", "Headphones", 12000, 350),
                        new Spec("grocery", "Coffee beans", 2400, 500)),
                List.of(new Spec("electronics", "Cable", 1234, 50), new Spec("grocery", "Apples", 399, 750),
                        new Spec("grocery", "Gum", 5000, 1)),
                List.of(new Spec("book", "Free sample", 0, 0)),
                List.of());
    }

    @ParameterizedTest
    @MethodSource("carts")
    void everyOperationMatchesTheClassicVisitors(List<Spec> cart) {
        var tax = new TaxVisitor();
        var weight = new ShippingWeightVisitor();
        var receipt = new ReceiptVisitor();
        for (Spec spec : cart) {
            var classic = spec.classic();
            CartItem modern = spec.modern();
            assertThat(CartOperations.tax(modern)).isEqualTo(classic.accept(tax));
            assertThat(CartOperations.shippingWeight(modern)).isEqualTo(classic.accept(weight));
            assertThat(CartOperations.receipt(modern)).isEqualTo(classic.accept(receipt));
            assertThat(CartOperations.describe(modern)).isEqualTo(OverloadTrap.describeWithVisitor(classic));
        }
        assertThat(CartOperations.totalTax(cart.stream().map(Spec::modern).toList()))
                .isEqualTo(cart.stream().mapToLong(spec -> spec.classic().accept(tax)).sum());
    }

    @Test
    void describeMatchesTheDynamicTypeOfAnItemHeldAsCartItem() {
        CartItem item = new Book("Refactoring", 4500);
        assertThat(CartOperations.describe(item)).isEqualTo("the book \"Refactoring\"");
    }

    @Test
    void aGuardedCaseAddsTheBulkySurchargeLine() {
        assertThat(CartOperations.receipt(new Electronics("Fridge", 49900, 20_001))).isEqualTo("""
                electronics  Fridge                   499.00
                             bulky surcharge           25.00""");
        assertThat(CartOperations.receipt(new Electronics("Kettle", 3900, 20_000)))
                .isEqualTo("electronics  Kettle                    39.00");
    }

    @Test
    void demoPrintsTheSameCartAndABulkyItem() {
        assertThat(Console.capture(() -> ModernCartDemo.main(new String[0]))).isEqualTo("""
                book         Refactoring               45.00
                electronics  Headphones               120.00
                grocery      Coffee beans 500 g        12.00
                electronics  Fridge                   499.00
                             bulky surcharge           25.00
                tax:             125.00
                shipping weight: 36250 g
                describe(CartItem) for a Book: the book "Refactoring"
                """);
    }
}
