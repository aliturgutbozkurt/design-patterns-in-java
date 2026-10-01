package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Shipping weight per line in grams.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class WeightVisitor implements LineItemVisitor<Integer> {

    @Override
    public Integer visitPhysical(PhysicalItem item) {
        return item.getGramsEach() * item.getQuantity();
    }

    @Override
    public Integer visitDigital(DigitalItem item) {
        return 0;
    }

    @Override
    public Integer visitGiftCard(GiftCard item) {
        return 0;
    }
}
