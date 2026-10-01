package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.memory;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.ProductCatalog;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Outbound adapter: a fixed price list.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class InMemoryProductCatalog implements ProductCatalog {

    private final Map<Sku, Money> prices;

    public InMemoryProductCatalog(Map<Sku, Money> prices) {
        this.prices = Map.copyOf(prices);
    }

    @Override
    public Optional<Money> priceOf(Sku sku) {
        return Optional.ofNullable(prices.get(Objects.requireNonNull(sku, "sku")));
    }
}
