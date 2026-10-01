package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Book;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.CartItem;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.CartVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Electronics;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.Grocery;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.OverloadTrap;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ReceiptVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.ShippingWeightVisitor;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic.TaxVisitor;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ClassicCartTest {

    private static final List<CartItem> CART = List.of(
            new Book("Refactoring", 4500),
            new Electronics("Headphones", 12000, 350),
            new Grocery("Coffee beans", 2400, 500));

    private static long totalTax(List<CartItem> items) {
        var tax = new TaxVisitor();
        return items.stream().mapToLong(item -> item.accept(tax)).sum();
    }

    @Test
    void taxIsExactPerCategory() {
        var tax = new TaxVisitor();
        assertThat(new Book("Refactoring", 4500).accept(tax)).isZero();
        assertThat(new Electronics("Headphones", 12000, 350).accept(tax)).isEqualTo(2400);
        assertThat(new Grocery("Coffee beans", 2400, 500).accept(tax)).isEqualTo(120);
        assertThat(totalTax(CART)).isEqualTo(2520);
    }

    @Test
    void taxIsRoundedHalfUpPerLine() {
        var tax = new TaxVisitor();
        assertThat(new Electronics("Cable", 1234, 50).accept(tax)).isEqualTo(247); // 246.8
        assertThat(new Grocery("Apples", 399, 750).accept(tax)).isEqualTo(30); // price 299.25 -> 299, tax 29.9
        assertThat(new Grocery("Gum", 5000, 1).accept(tax)).isEqualTo(1); // price 5, tax 0.5 -> 1
    }

    @Test
    void groceryPriceIsPerKiloRoundedHalfUp() {
        assertThat(new Grocery("Apples", 399, 750).priceCents()).isEqualTo(299);
        assertThat(new Grocery("Rice", 300, 1500).priceCents()).isEqualTo(450);
    }

    @Test
    void shippingWeightSumsPhysicalWeightWithBooksAt400Grams() {
        var weight = new ShippingWeightVisitor();
        assertThat(new Book("Refactoring", 4500).accept(weight)).isEqualTo(400);
        assertThat(CART.stream().mapToInt(item -> item.accept(weight)).sum()).isEqualTo(1250);
    }

    @Test
    void receiptTextIsExact() {
        var receipt = new ReceiptVisitor();
        assertThat(CART.stream().map(item -> item.accept(receipt)).collect(Collectors.joining("\n"))).isEqualTo("""
                book         Refactoring               45.00
                electronics  Headphones               120.00
                grocery      Coffee beans 500 g        12.00""");
    }

    @Test
    void aNewOperationIsANewVisitorWithoutTouchingTheItems() {
        CartVisitor<String> kind = new CartVisitor<>() {
            @Override
            public String visitBook(Book book) {
                return "book";
            }

            @Override
            public String visitElectronics(Electronics electronics) {
                return "electronics";
            }

            @Override
            public String visitGrocery(Grocery grocery) {
                return "grocery";
            }
        };
        var cart = List.<CartItem>of(new Book("A", 100), new Book("B", 200), new Grocery("C", 100, 100));
        Map<String, Long> counts = cart.stream()
                .collect(Collectors.groupingBy(item -> item.accept(kind), Collectors.counting()));
        assertThat(counts).containsExactlyInAnyOrderEntriesOf(Map.of("book", 2L, "grocery", 1L));
    }

    @Test
    void overloadingIsChosenByTheStaticTypeButAcceptReachesVisitBook() {
        var book = new Book("Refactoring", 4500);
        CartItem item = book;
        assertThat(OverloadTrap.describe(book)).isEqualTo("the book \"Refactoring\"");
        assertThat(OverloadTrap.describe(item)).isEqualTo("a cart item");
        assertThat(OverloadTrap.describeWithVisitor(item)).isEqualTo("the book \"Refactoring\"");
        Function<CartItem, String> viaLambda = OverloadTrap::describe;
        assertThat(viaLambda.apply(book)).isEqualTo("a cart item");
    }

    @Test
    void itemsValidateTheirData() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Book("A", -1));
        assertThatIllegalArgumentException().isThrownBy(() -> new Electronics("A", 100, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Grocery("A", 100, 0));
    }

    @Test
    void demoPrintsReceiptTaxWeightAndTheOverloadTrap() {
        assertThat(Console.capture(() -> ClassicCartDemo.main(new String[0]))).isEqualTo("""
                book         Refactoring               45.00
                electronics  Headphones               120.00
                grocery      Coffee beans 500 g        12.00
                tax:             25.20
                shipping weight: 1250 g
                describe(CartItem) for a Book: a cart item
                accept -> visitBook:          the book "Refactoring"
                """);
    }
}
