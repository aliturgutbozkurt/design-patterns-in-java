package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

/**
 * Component: asks the warehouse how many units of a product are in stock.
 *
 * @see "m04 lesson, section Decorator"
 */
public interface StockService {

    int available(String sku);
}
