package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Specification;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port: where products live. Implementations are thread-safe.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "repository (outbound port)")
public interface ProductRepository {

    /** Inserts or replaces the product with the same SKU. */
    void save(Product product);

    /** The product with this SKU, if any. */
    Optional<Product> find(Sku sku);

    /** The matching products, sorted by SKU. */
    List<Product> findMatching(Specification<Product> specification);
}
