package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopEnvironment;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopSettings;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductView;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.DemoData;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedPaymentApi;

/**
 * One shop under test with its fakes: a {@link MutableClock}, a {@link SandboxPaymentApi}, a {@link ScriptedWarehouse},
 * {@link RecordingNotifications} and {@link RecordingErrors}. The shop is created with the binding's factory and seeded
 * with the GIVEN {@link DemoData}. The helpers only use the GIVEN API.
 */
public final class ShopTestKit {

    /** An approved card token. */
    public static final String CARD = SimulatedPaymentApi.APPROVED;

    /**
     * A cart line to create.
     *
     * @param sku      the SKU text
     * @param quantity units
     */
    public record Item(String sku, int quantity) {
    }

    private final MutableClock clock = new MutableClock();
    private final SandboxPaymentApi payments = new SandboxPaymentApi();
    private final ScriptedWarehouse warehouse = new ScriptedWarehouse();
    private final RecordingNotifications notifications = new RecordingNotifications();
    private final RecordingErrors errors = new RecordingErrors();
    private final ShopSettings settings;
    private final PatternShop shop;

    private ShopTestKit(PatternShopFactory factory, ShopSettings settings) {
        this.settings = settings;
        this.shop = factory.create(new ShopEnvironment(clock, payments, warehouse, notifications, errors, settings));
        DemoData.seed(shop);
    }

    /** A new shop from {@code factory} with {@code settings}, seeded with the demo data. */
    public static ShopTestKit start(PatternShopFactory factory, ShopSettings settings) {
        return new ShopTestKit(factory, settings);
    }

    public PatternShop shop() {
        return shop;
    }

    public MutableClock clock() {
        return clock;
    }

    public SandboxPaymentApi payments() {
        return payments;
    }

    public ScriptedWarehouse warehouse() {
        return warehouse;
    }

    public RecordingNotifications notifications() {
        return notifications;
    }

    public RecordingErrors errors() {
        return errors;
    }

    public ShopSettings settings() {
        return settings;
    }

    // ---- values ----

    public static Sku sku(String value) {
        return new Sku(value);
    }

    public static Money money(String value) {
        return Money.of(value);
    }

    public static CustomerId customer(String value) {
        return new CustomerId(value);
    }

    public static OrderId orderId(String value) {
        return new OrderId(value);
    }

    public static Item item(String sku, int quantity) {
        return new Item(sku, quantity);
    }

    /** A complete address in Istanbul, postal code {@code 34710}. */
    public static Address address() {
        return new Address("Alice Doe", "Bagdat Cd. 1", "Istanbul", "34710");
    }

    /** An address with every field blank. */
    public static Address noAddress() {
        return new Address("", "", "", "");
    }

    // ---- scenarios ----

    /** Opens a cart for {@code customer} and adds the items in order. */
    public CartId cartWith(String customer, Item... items) {
        CartId cart = shop.carts().open(customer(customer));
        for (Item item : items) {
            shop.carts().add(cart, sku(item.sku()), item.quantity());
        }
        return cart;
    }

    /** Checks out {@code cart} with {@link #address()} and the approved card. */
    public CheckoutResult checkout(CartId cart) {
        return shop.checkout().checkout(new CheckoutRequest(cart, address(), CARD));
    }

    /** Places (and pays) an order of the items; fails the test if the checkout is rejected. */
    public OrderId placeOrder(String customer, Item... items) {
        return placed(checkout(cartWith(customer, items))).order();
    }

    /** The result as {@link CheckoutResult.Placed}; fails the test otherwise. */
    public static CheckoutResult.Placed placed(CheckoutResult result) {
        if (result instanceof CheckoutResult.Placed placed) {
            return placed;
        }
        throw new AssertionError("expected the checkout to be placed but it was " + result);
    }

    /** The product, which must exist. */
    public ProductView product(String sku) {
        return shop.catalogue().find(sku(sku)).orElseThrow(() -> new AssertionError("no product " + sku));
    }

    /** The stock of a product. */
    public int stock(String sku) {
        return product(sku).stock();
    }

    /** The order, which must exist. */
    public OrderView order(OrderId id) {
        return shop.orders().find(id).orElseThrow(() -> new AssertionError("no order " + id.value()));
    }
}
