package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * A gift card: no tax, no weight.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class GiftCard extends LineItem {

    private final long valueCents;

    public GiftCard(String code, long valueCents) {
        super(code);
        this.valueCents = valueCents;
    }

    @Override
    public long amountCents() {
        return valueCents;
    }

    @Override
    public String describe() {
        return "gift card " + getSku();
    }

    @Override
    public <R> R accept(LineItemVisitor<R> visitor) {
        return visitor.visitGiftCard(this);
    }
}
