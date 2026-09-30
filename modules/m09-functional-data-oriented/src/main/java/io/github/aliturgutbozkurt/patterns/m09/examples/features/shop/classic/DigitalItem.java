package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * A download: taxed at a lower rate, weighs nothing.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class DigitalItem extends LineItem {

    private final long priceCents;

    public DigitalItem(String sku, long priceCents) {
        super(sku);
        this.priceCents = priceCents;
    }

    @Override
    public long amountCents() {
        return priceCents;
    }

    @Override
    public String describe() {
        return getSku() + " (download)";
    }

    @Override
    public <R> R accept(LineItemVisitor<R> visitor) {
        return visitor.visitDigital(this);
    }
}
