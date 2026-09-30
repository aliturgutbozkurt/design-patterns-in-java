package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.before;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.List;
import java.util.Optional;

/**
 * ISP violation: readers and writers share one interface, so every client sees — and every implementer must
 * provide — all five operations.
 *
 * @see "m01 lesson, section ISP"
 */
public interface ProductStore {

    Optional<Product> find(String sku);

    List<Product> list();

    void save(Product product);

    void delete(String sku);

    void importAll(List<Product> products);
}
