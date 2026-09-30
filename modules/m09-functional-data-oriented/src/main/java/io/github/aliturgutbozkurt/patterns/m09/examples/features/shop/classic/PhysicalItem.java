package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * A parcel item: has a weight and ships.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class PhysicalItem extends LineItem {

    private final long unitPriceCents;
    private final int quantity;
    private final int gramsEach;

    public PhysicalItem(String sku, long unitPriceCents, int quantity, int gramsEach) {
        super(sku);
        this.unitPriceCents = unitPriceCents;
        this.quantity = quantity;
        this.gramsEach = gramsEach;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getGramsEach() {
        return gramsEach;
    }

    @Override
    public long amountCents() {
        return unitPriceCents * quantity;
    }

    @Override
    public String describe() {
        return getSku() + " x" + quantity;
    }

    @Override
    public <R> R accept(LineItemVisitor<R> visitor) {
        return visitor.visitPhysical(this);
    }
}
