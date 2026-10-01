package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.inCategory;
import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.nameContains;
import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.priceAtMost;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Category;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.FileProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.InMemoryProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Product;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Sku;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Specification;
import io.github.aliturgutbozkurt.patterns.m11.examples.support.TempDirectory;
import java.nio.file.Path;
import java.util.List;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/repository/CatalogRepositoryDemo.java}
 *
 * @see "m11 lesson, section Repository"
 */
public final class CatalogRepositoryDemo {

    private CatalogRepositoryDemo() {}

    public static void main(String[] args) {
        showQueries(new InMemoryProductRepository());
        try (TempDirectory temp = TempDirectory.create("m11-catalog")) {
            Path file = temp.path().resolve("products.txt");
            showQueries(new FileProductRepository(file));
            ProductRepository afterRestart = new FileProductRepository(file); // a new instance, same file
            System.out.println("after a restart the file still holds " + afterRestart.count() + " products; BOOK-1 costs "
                    + afterRestart.findById(new Sku("BOOK-1")).map(Product::price).orElseThrow());
        }
    }

    /** The caller code is identical for every implementation: it only knows the interface. */
    private static void showQueries(ProductRepository catalog) {
        System.out.println("== " + catalog.getClass().getSimpleName());
        catalog.save(new Product(new Sku("PEN-7"), "Fountain Pen", Category.STATIONERY, Money.of("12.00")));
        catalog.save(new Product(new Sku("BOOK-2"), "Refactoring", Category.BOOKS, Money.of("29.50")));
        catalog.save(new Product(new Sku("MUG-3"), "Pattern Mug", Category.KITCHEN, Money.of("8.75")));
        catalog.save(new Product(new Sku("BOOK-1"), "Design Patterns", Category.BOOKS, Money.of("39.90")));

        Specification<Product> books = inCategory(Category.BOOKS);
        System.out.println("all:                     " + skus(catalog.findAll()));
        System.out.println("books up to 30.00:       " + skus(catalog.findMatching(books.and(priceAtMost(Money.of("30.00"))))));
        System.out.println("\"pattern\" or stationery: "
                + skus(catalog.findMatching(nameContains("pattern").or(inCategory(Category.STATIONERY)))));
        System.out.println("not books:               " + skus(catalog.findMatching(books.not())));
    }

    private static List<Sku> skus(List<Product> products) {
        return products.stream().map(Product::sku).toList();
    }
}
