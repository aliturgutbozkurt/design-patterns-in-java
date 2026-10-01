package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Optional;

/** GIVEN — do not modify. Outbound port: the product catalogue. */
@FunctionalInterface
public interface ProductCatalog {

    Optional<Product> find(Sku sku);
}
