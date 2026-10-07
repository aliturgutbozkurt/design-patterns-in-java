package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * One stage of pricing. Stages wrap each other, so the fixed order of the brief's Business rules is the order of
 * wrapping.
 *
 * @see "capstone guide, Pattern map — Decorator"
 */
@PatternRole(value = DesignPattern.DECORATOR, role = "component")
@FunctionalInterface
public interface PriceStep {

    /** The price sheet of {@code basket} after this stage and every stage it wraps. */
    PriceSheet price(Basket basket);
}
