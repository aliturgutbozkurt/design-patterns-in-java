package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CheckoutResult.Confirmed;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CheckoutResult.Rejected;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Assignment 01 — PatternShop checkout as Ports & Adapters. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    /** Your {@code CheckoutService}, wired to the given ports. */
    protected abstract CheckoutUseCase newService(ProductCatalog catalog, PaymentPort payments,
                                                  OrderRepository orders, EventPublisher events,
                                                  OrderIdGenerator ids);

    /** A new, empty {@code InMemoryOrderRepository}. */
    protected abstract OrderRepository newRepository();

    private static final Sku BOOK = new Sku("BOOK-1");
    private static final Sku PEN = new Sku("PEN-7");
    private static final Sku MUG = new Sku("MUG-3");
    private static final Sku UNKNOWN = new Sku("TOY-9");

    /** Stub catalogue: canned products. */
    private final Map<Sku, Product> products = Map.of(
            BOOK, new Product(BOOK, "Design Patterns", new Money(20_00), 5),
            PEN, new Product(PEN, "Fountain Pen", new Money(7_00), 100),
            MUG, new Product(MUG, "Pattern Mug", new Money(8_75), 1));
    private final ProductCatalog catalog = sku -> Optional.ofNullable(products.get(sku));

    /** One call log shared by the mock payment port, the spy repository and the spy publisher. */
    private final List<String> log = new ArrayList<>();

    /** Mock payment port: approves unless told otherwise, counts calls, remembers amounts. */
    private boolean approve = true;
    private final List<Money> charges = new ArrayList<>();
    private final PaymentPort payments = (customer, amount) -> {
        charges.add(amount);
        log.add("charge " + customer + " " + amount.cents());
        return approve;
    };

    /** Spy repository: records saves and can fail like a full disk. */
    private boolean failSave;
    private final List<Order> saved = new ArrayList<>();
    private final OrderRepository repository = new OrderRepository() {
        @Override
        public void save(Order order) {
            if (failSave) {
                throw new IllegalStateException("disk full");
            }
            saved.add(order);
            log.add("save " + order.id());
        }

        @Override
        public Optional<Order> findById(OrderId id) {
            return saved.stream().filter(order -> order.id().equals(id)).findFirst();
        }

        @Override
        public List<Order> findByCustomer(String customer) {
            return saved.stream().filter(order -> order.customer().equals(customer)).toList();
        }

        @Override
        public int count() {
            return saved.size();
        }
    };

    /** Spy publisher. */
    private final List<OrderPlaced> published = new ArrayList<>();
    private final EventPublisher publisher = event -> {
        published.add(event);
        log.add("publish " + event.id());
    };

    /** Sequence id generator: order-1, order-2, … */
    private int nextId;
    private final OrderIdGenerator ids = () -> new OrderId("order-" + ++nextId);

    private CheckoutUseCase service() {
        return newService(catalog, payments, repository, publisher, ids);
    }

    private static CartItem item(Sku sku, int quantity) {
        return new CartItem(sku, quantity);
    }

    private CheckoutResult checkout(CartItem... cart) {
        return service().checkout("ada", List.of(cart));
    }

    private void assertNothingChargedSavedOrPublished() {
        assertThat(charges).isEmpty();
        assertThat(saved).isEmpty();
        assertThat(published).isEmpty();
    }

    private static Order order(String id, String customer, OrderLine... lines) {
        Money total = List.of(lines).stream().map(OrderLine::total).reduce(Money.ZERO, Money::plus);
        return new Order(new OrderId(id), customer, List.of(lines), total);
    }

    @Test
    void confirmsValidCartAndReturnsTheOrder() {
        CheckoutResult result = checkout(item(BOOK, 2), item(PEN, 1));
        assertThat(result).isEqualTo(new Confirmed(order("order-1", "ada",
                new OrderLine(BOOK, 2, new Money(20_00)), new OrderLine(PEN, 1, new Money(7_00)))));
        assertThat(saved).containsExactly(((Confirmed) result).order());
    }

    @Test
    void totalIsSumOfLinePrices() {
        var result = (Confirmed) checkout(item(BOOK, 2), item(PEN, 3), item(MUG, 1));
        assertThat(result.order().total()).isEqualTo(new Money(2 * 20_00 + 3 * 7_00 + 8_75));
    }

    @Test
    void chargesTotalExactlyOnce() {
        checkout(item(BOOK, 2), item(PEN, 1));
        assertThat(charges).containsExactly(new Money(47_00));
    }

    @Test
    void savesBeforePublishing() {
        checkout(item(PEN, 1));
        assertThat(log).containsExactly("charge ada 700", "save order-1", "publish order-1");
    }

    @Test
    void publishesExactlyOneOrderPlaced() {
        checkout(item(BOOK, 1), item(PEN, 1));
        assertThat(published).containsExactly(new OrderPlaced(new OrderId("order-1"), "ada", new Money(27_00)));
    }

    @Test
    void rejectsEmptyCart() {
        assertThat(checkout()).isEqualTo(new Rejected("empty cart"));
        assertNothingChargedSavedOrPublished();
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThat(checkout(item(PEN, 0))).isEqualTo(new Rejected("invalid quantity: PEN-7"));
        assertThat(checkout(item(BOOK, 1), item(PEN, -1))).isEqualTo(new Rejected("invalid quantity: PEN-7"));
        assertNothingChargedSavedOrPublished();
    }

    @Test
    void rejectsUnknownProduct() {
        assertThat(checkout(item(BOOK, 1), item(UNKNOWN, 1))).isEqualTo(new Rejected("unknown product: TOY-9"));
        assertNothingChargedSavedOrPublished();
    }

    @Test
    void rejectsInsufficientStock() {
        assertThat(checkout(item(MUG, 2))).isEqualTo(new Rejected("insufficient stock: MUG-3"));
        assertNothingChargedSavedOrPublished();
    }

    @Test
    void firstInvalidItemDecidesTheReason() {
        assertThat(checkout(item(UNKNOWN, 1), item(PEN, 0))).isEqualTo(new Rejected("unknown product: TOY-9"));
        assertThat(checkout(item(PEN, 0), item(UNKNOWN, 1))).isEqualTo(new Rejected("invalid quantity: PEN-7"));
        assertThat(checkout(item(MUG, 5), item(UNKNOWN, 1))).isEqualTo(new Rejected("unknown product: TOY-9"));
        assertNothingChargedSavedOrPublished();
    }

    @Test
    void mergesDuplicateSkus() {
        var result = (Confirmed) checkout(item(BOOK, 2), item(PEN, 1), item(BOOK, 1));
        assertThat(result.order().lines()).containsExactly(
                new OrderLine(BOOK, 3, new Money(20_00)), new OrderLine(PEN, 1, new Money(7_00)));
        // Merged before the stock check: 4 + 2 = 6 books, but only 5 are in stock.
        assertThat(checkout(item(BOOK, 4), item(PEN, 1), item(BOOK, 2)))
                .isEqualTo(new Rejected("insufficient stock: BOOK-1"));
    }

    @Test
    void validationFailureNeverCharges() {
        checkout(item(UNKNOWN, 1));
        checkout(item(MUG, 9));
        checkout(item(PEN, 0));
        assertThat(charges).isEmpty();
    }

    @Test
    void declinedPaymentSavesAndPublishesNothing() {
        approve = false;
        assertThat(checkout(item(BOOK, 1))).isEqualTo(new Rejected("payment declined"));
        assertThat(charges).containsExactly(new Money(20_00));
        assertThat(saved).isEmpty();
        assertThat(published).isEmpty();
    }

    @Test
    void idsAreConsumedOnlyByConfirmedOrders() {
        checkout(item(UNKNOWN, 1));
        approve = false;
        checkout(item(PEN, 1));
        approve = true;
        var result = (Confirmed) checkout(item(PEN, 1));
        assertThat(result.order().id()).isEqualTo(new OrderId("order-1"));
    }

    @Test
    void saveFailurePublishesNothingAndPropagates() {
        failSave = true;
        assertThatIllegalStateException().isThrownBy(() -> checkout(item(PEN, 1))).withMessage("disk full");
        assertThat(published).isEmpty();
    }

    @Test
    void repositoryFindsSavedOrderById() {
        OrderRepository orders = newRepository();
        Order order = order("order-1", "ada", new OrderLine(PEN, 1, new Money(7_00)));
        orders.save(order);
        assertThat(orders.findById(new OrderId("order-1"))).contains(order);
        assertThat(orders.findById(new OrderId("order-2"))).isEmpty();
        assertThat(orders.count()).isEqualTo(1);
    }

    @Test
    void repositorySaveWithSameIdReplacesInPlace() {
        OrderRepository orders = newRepository();
        orders.save(order("order-1", "ada", new OrderLine(PEN, 1, new Money(7_00))));
        orders.save(order("order-2", "ada", new OrderLine(MUG, 1, new Money(8_75))));
        Order replacement = order("order-1", "ada", new OrderLine(PEN, 2, new Money(7_00)));
        orders.save(replacement);
        assertThat(orders.count()).isEqualTo(2);
        assertThat(orders.findByCustomer("ada")).extracting(Order::id)
                .containsExactly(new OrderId("order-1"), new OrderId("order-2"));
        assertThat(orders.findById(new OrderId("order-1"))).contains(replacement);
    }

    @Test
    void repositoryFindsByCustomerInSaveOrder() {
        OrderRepository orders = newRepository();
        orders.save(order("order-3", "ada", new OrderLine(PEN, 1, new Money(7_00))));
        orders.save(order("order-1", "alan", new OrderLine(PEN, 1, new Money(7_00))));
        orders.save(order("order-2", "ada", new OrderLine(MUG, 1, new Money(8_75))));
        assertThat(orders.findByCustomer("ada")).extracting(Order::id)
                .containsExactly(new OrderId("order-3"), new OrderId("order-2"));
        assertThat(orders.findByCustomer("grace")).isEmpty();
    }

    @Test
    void repositoryListsAreImmutable() {
        OrderRepository orders = newRepository();
        Order order = order("order-1", "ada", new OrderLine(PEN, 1, new Money(7_00)));
        orders.save(order);
        List<Order> found = orders.findByCustomer("ada");
        assertThatThrownBy(() -> found.add(order)).isInstanceOf(UnsupportedOperationException.class);
        orders.save(order("order-2", "ada", new OrderLine(PEN, 1, new Money(7_00))));
        assertThat(found).containsExactly(order);
    }

    @Test
    void serviceDoesNotDependOnAdapters() {
        Class<?> serviceClass = service().getClass();
        JavaClasses classes = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(serviceClass.getPackageName());
        noClasses().that().belongToAnyOf(serviceClass)
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .check(classes);
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> service().checkout(null, List.of(item(PEN, 1))));
        assertThatNullPointerException().isThrownBy(() -> service().checkout("ada", null));
        assertThatNullPointerException().isThrownBy(() -> newService(null, payments, repository, publisher, ids));
        assertThatNullPointerException().isThrownBy(() -> newService(catalog, payments, repository, publisher, null));
        assertThatNullPointerException().isThrownBy(() -> newRepository().save(null));
        assertNothingChargedSavedOrPublished();
    }
}
