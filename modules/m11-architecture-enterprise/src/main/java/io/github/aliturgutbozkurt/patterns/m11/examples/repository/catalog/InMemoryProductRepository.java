package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The repository as a sorted map — fast, and the natural test double for everything that uses a
 * {@link ProductRepository}.
 *
 * @see "m11 lesson, section Repository"
 */
public final class InMemoryProductRepository implements ProductRepository {

    private final SortedMap<Sku, Product> products = new TreeMap<>();

    @Override
    public void save(Product product) {
        products.put(Objects.requireNonNull(product, "product").sku(), product);
    }

    @Override
    public Optional<Product> findById(Sku sku) {
        return Optional.ofNullable(products.get(Objects.requireNonNull(sku, "sku")));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public List<Product> findMatching(Specification<Product> specification) {
        Objects.requireNonNull(specification, "specification");
        return products.values().stream().filter(specification::isSatisfiedBy).toList();
    }

    @Override
    public boolean delete(Sku sku) {
        return products.remove(Objects.requireNonNull(sku, "sku")) != null;
    }

    @Override
    public int count() {
        return products.size();
    }
}
