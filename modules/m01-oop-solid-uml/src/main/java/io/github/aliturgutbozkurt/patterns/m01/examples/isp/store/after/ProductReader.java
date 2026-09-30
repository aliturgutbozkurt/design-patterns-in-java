package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.List;
import java.util.Optional;

/**
 * The read side, for clients that only look products up.
 *
 * @see "m01 lesson, section ISP"
 */
public interface ProductReader {

    Optional<Product> find(String sku);

    List<Product> list();
}
