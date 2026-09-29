package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Where imported products end up; keyed by SKU, keeps insertion order. Not thread-safe.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class InMemoryProductStore {

    private final Map<String, Product> bySku = new LinkedHashMap<>();

    public void save(Product product) {
        Objects.requireNonNull(product, "product");
        bySku.put(product.sku(), product);
    }

    public List<Product> all() {
        return List.copyOf(bySku.values());
    }
}
