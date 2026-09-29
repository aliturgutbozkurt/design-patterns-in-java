package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.List;

/**
 * The write side, for clients that change the catalog.
 *
 * @see "m01 lesson, section ISP"
 */
public interface ProductWriter {

    void save(Product product);

    void delete(String sku);

    /** Saves every product in order. */
    default void importAll(List<Product> products) {
        products.forEach(this::save);
    }
}
