package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A full store plays both roles; each client still sees only the role it asked for.
 *
 * @see "m01 lesson, section ISP"
 */
public final class InMemoryProducts implements ProductReader, ProductWriter {

    private final SortedMap<String, Product> bySku = new TreeMap<>();

    @Override
    public Optional<Product> find(String sku) {
        return Optional.ofNullable(bySku.get(sku));
    }

    /** All products, ordered by SKU. */
    @Override
    public List<Product> list() {
        return List.copyOf(bySku.values());
    }

    @Override
    public void save(Product product) {
        bySku.put(product.sku(), product);
    }

    @Override
    public void delete(String sku) {
        bySku.remove(sku);
    }
}
