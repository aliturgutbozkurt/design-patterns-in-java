package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Classic invoice line: an abstract base class with an {@code accept} hook for visitors.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public abstract class LineItem {

    private final String sku;

    protected LineItem(String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public abstract long amountCents();

    public abstract String describe();

    public abstract <R> R accept(LineItemVisitor<R> visitor);
}
