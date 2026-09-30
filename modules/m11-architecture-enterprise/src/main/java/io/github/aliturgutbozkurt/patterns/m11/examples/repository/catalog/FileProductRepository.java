package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The same repository backed by a text file, one product per line: {@code BOOK-1|Design Patterns|BOOKS|3990}. Every
 * call reads the file, so a new instance on the same path sees what earlier instances saved.
 *
 * @see "m11 lesson, section Repository"
 */
public final class FileProductRepository implements ProductRepository {

    private final Path file;

    public FileProductRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public void save(Product product) {
        Objects.requireNonNull(product, "product");
        if (product.name().matches("(?s).*[|\\r\\n].*")) {
            throw new IllegalArgumentException("product name must not contain '|' or a line break: " + product.name());
        }
        SortedMap<Sku, Product> products = load();
        products.put(product.sku(), product);
        store(products);
    }

    @Override
    public Optional<Product> findById(Sku sku) {
        Objects.requireNonNull(sku, "sku");
        return Optional.ofNullable(load().get(sku));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(load().values());
    }

    @Override
    public List<Product> findMatching(Specification<Product> specification) {
        Objects.requireNonNull(specification, "specification");
        return load().values().stream().filter(specification::isSatisfiedBy).toList();
    }

    @Override
    public boolean delete(Sku sku) {
        Objects.requireNonNull(sku, "sku");
        SortedMap<Sku, Product> products = load();
        boolean removed = products.remove(sku) != null;
        if (removed) {
            store(products);
        }
        return removed;
    }

    @Override
    public int count() {
        return load().size();
    }

    private SortedMap<Sku, Product> load() {
        SortedMap<Sku, Product> products = new TreeMap<>();
        if (!Files.exists(file)) {
            return products;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Product product = parse(line);
                products.put(product.sku(), product);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + file, e);
        }
        return products;
    }

    private void store(SortedMap<Sku, Product> products) {
        List<String> lines = products.values().stream()
                .map(p -> String.join("|", p.sku().value(), p.name(), p.category().name(), Long.toString(p.price().cents())))
                .toList();
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot write " + file, e);
        }
    }

    private static Product parse(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 4) {
            throw new IllegalStateException("corrupt catalogue line: " + line);
        }
        return new Product(new Sku(fields[0]), fields[1], Category.valueOf(fields[2]), new Money(Long.parseLong(fields[3])));
    }
}
