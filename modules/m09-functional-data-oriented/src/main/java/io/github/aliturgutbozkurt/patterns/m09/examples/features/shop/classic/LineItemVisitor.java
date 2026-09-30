package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Visitor over the line-item hierarchy: one method per concrete class.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public interface LineItemVisitor<R> {

    R visitPhysical(PhysicalItem item);

    R visitDigital(DigitalItem item);

    R visitGiftCard(GiftCard item);
}
