package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Specification;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Products in a concurrent sorted map keyed by SKU text, so every query comes back sorted by SKU. Products are
 * immutable, so handing them out is safe.
 *
 * @see "capstone guide §1 Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "in-memory repository (outbound adapter)")
public final class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> products = new ConcurrentSkipListMap<>();

    @Override
    public void save(Product product) {
        products.put(Objects.requireNonNull(product, "product").sku().value(), product);
    }

    @Override
    public Optional<Product> find(Sku sku) {
        return Optional.ofNullable(products.get(sku.value()));
    }

    @Override
    public List<Product> findMatching(Specification<Product> specification) {
        return products.values().stream().filter(specification::isSatisfiedBy).toList();
    }
}
