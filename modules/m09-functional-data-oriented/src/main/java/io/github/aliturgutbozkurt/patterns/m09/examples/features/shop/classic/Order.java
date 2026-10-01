package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.List;

/**
 * An order to invoice.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class Order {

    private final String id;
    private final String customer;
    private final List<LineItem> items;
    private final String discountCode;

    public Order(String id, String customer, List<LineItem> items, String discountCode) {
        this.id = id;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.discountCode = discountCode;
    }

    public String getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public String getDiscountCode() {
        return discountCode;
    }
}
