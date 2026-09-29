package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.math.BigDecimal;

/**
 * Second, unrelated demo interface: the same proxy helpers work for it without new code.
 *
 * @see "m04 lesson, section Proxy"
 */
public interface PriceList {

    BigDecimal priceOf(String sku);

    @Mutator
    void changePrice(String sku, BigDecimal price);
}
