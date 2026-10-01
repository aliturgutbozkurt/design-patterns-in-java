package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.Optional;

/**
 * Outbound port: prices come from somewhere the core does not care about.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface ProductCatalog {

    /** The unit price, or empty for an unknown product. */
    Optional<Money> priceOf(Sku sku);
}
