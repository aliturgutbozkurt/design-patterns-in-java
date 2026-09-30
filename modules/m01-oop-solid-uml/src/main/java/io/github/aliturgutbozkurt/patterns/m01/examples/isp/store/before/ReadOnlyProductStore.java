package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.before;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.List;
import java.util.Optional;

/**
 * A read-only view forced to "implement" writes by throwing — the same smell as {@code List.of(...).add(...)}.
 *
 * @see "m01 lesson, section ISP"
 */
public final class ReadOnlyProductStore implements ProductStore {

    private final List<Product> products;

    public ReadOnlyProductStore(List<Product> products) {
        this.products = List.copyOf(products);
    }

    @Override
    public Optional<Product> find(String sku) {
        return products.stream().filter(product -> product.sku().equals(sku)).findFirst();
    }

    @Override
    public List<Product> list() {
        return products;
    }

    @Override
    public void save(Product product) {
        throw new UnsupportedOperationException("read-only store");
    }

    @Override
    public void delete(String sku) {
        throw new UnsupportedOperationException("read-only store");
    }

    @Override
    public void importAll(List<Product> products) {
        throw new UnsupportedOperationException("read-only store");
    }
}
