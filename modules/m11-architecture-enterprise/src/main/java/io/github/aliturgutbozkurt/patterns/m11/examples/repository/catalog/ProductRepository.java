package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.List;
import java.util.Optional;

/**
 * Repository: the catalogue as if it were an in-memory collection of products. Callers never see files, SQL or
 * maps; every returned list is an immutable snapshot sorted by SKU.
 *
 * @see "m11 lesson, section Repository"
 */
public interface ProductRepository {

    /** Adds {@code product}, or replaces the product with the same SKU. */
    void save(Product product);

    Optional<Product> findById(Sku sku);

    /** Every product, sorted by SKU. */
    List<Product> findAll();

    /** The products {@code specification} is satisfied by, sorted by SKU. */
    List<Product> findMatching(Specification<Product> specification);

    /** Removes the product; {@code false} if there was none. */
    boolean delete(Sku sku);

    int count();
}
