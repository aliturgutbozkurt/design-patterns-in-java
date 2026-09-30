package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Tax per line: 20 % for physical goods, 10 % for downloads, none for gift cards.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class TaxVisitor implements LineItemVisitor<Long> {

    @Override
    public Long visitPhysical(PhysicalItem item) {
        return item.amountCents() * 20 / 100;
    }

    @Override
    public Long visitDigital(DigitalItem item) {
        return item.amountCents() * 10 / 100;
    }

    @Override
    public Long visitGiftCard(GiftCard item) {
        return 0L;
    }
}
