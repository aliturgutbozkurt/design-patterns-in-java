package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.inCategory;
import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.nameContains;
import static io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductSpecs.priceAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Category;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Product;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Sku;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Specification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** One contract, every implementation: the in-memory and the file repository must behave the same. */
abstract class ProductRepositoryContract {

    static final Product PATTERNS = product("BOOK-1", "Design Patterns", Category.BOOKS, "39.90");
    static final Product REFACTORING = product("BOOK-2", "Refactoring", Category.BOOKS, "29.50");
    static final Product MUG = product("MUG-3", "Pattern Mug", Category.KITCHEN, "8.75");
    static final Product PEN = product("PEN-7", "Fountain Pen", Category.STATIONERY, "12.00");

    protected ProductRepository repository;

    /** A new, empty repository. */
    protected abstract ProductRepository newRepository();

    @BeforeEach
    void createRepository() {
        repository = newRepository();
    }

    static Product product(String sku, String name, Category category, String price) {
        return new Product(new Sku(sku), name, category, Money.of(price));
    }

    private void saveAll() {
        List.of(PEN, MUG, REFACTORING, PATTERNS).forEach(repository::save);
    }

    private static List<String> skus(List<Product> products) {
        return products.stream().map(product -> product.sku().value()).toList();
    }

    @Test
    void saveThenFindByIdReturnsAnEqualProduct() {
        repository.save(PATTERNS);
        assertThat(repository.findById(new Sku("BOOK-1"))).contains(PATTERNS);
        assertThat(repository.findById(new Sku("BOOK-9"))).isEmpty();
    }

    @Test
    void savingTheSameSkuAgainReplacesIt() {
        repository.save(PATTERNS);
        Product cheaper = product("BOOK-1", "Design Patterns", Category.BOOKS, "19.90");
        repository.save(cheaper);
        assertThat(repository.findById(new Sku("BOOK-1"))).contains(cheaper);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void findAllIsSortedBySku() {
        saveAll();
        assertThat(skus(repository.findAll())).containsExactly("BOOK-1", "BOOK-2", "MUG-3", "PEN-7");
        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    void returnedListsAreImmutableAndDoNotChangeAfterLaterSaves() {
        repository.save(PATTERNS);
        List<Product> all = repository.findAll();
        List<Product> books = repository.findMatching(inCategory(Category.BOOKS));
        repository.save(REFACTORING);
        assertThat(all).containsExactly(PATTERNS);
        assertThat(books).containsExactly(PATTERNS);
        assertThatThrownBy(() -> all.add(MUG)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> books.clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void andOrNotSpecificationsSelectExactlyTheExpectedSkus() {
        saveAll();
        Specification<Product> books = inCategory(Category.BOOKS);
        assertThat(skus(repository.findMatching(books.and(priceAtMost(Money.of("30.00"))))))
                .containsExactly("BOOK-2");
        assertThat(skus(repository.findMatching(nameContains("pattern").or(inCategory(Category.STATIONERY)))))
                .containsExactly("BOOK-1", "MUG-3", "PEN-7");
        assertThat(skus(repository.findMatching(books.not()))).containsExactly("MUG-3", "PEN-7");
    }

    @Test
    void deleteRemovesAKnownSkuAndReturnsFalseForAnUnknownOne() {
        saveAll();
        assertThat(repository.delete(new Sku("MUG-3"))).isTrue();
        assertThat(repository.delete(new Sku("MUG-3"))).isFalse();
        assertThat(repository.delete(new Sku("TOY-1"))).isFalse();
        assertThat(skus(repository.findAll())).containsExactly("BOOK-1", "BOOK-2", "PEN-7");
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> repository.save(null));
        assertThatNullPointerException().isThrownBy(() -> repository.findById(null));
        assertThatNullPointerException().isThrownBy(() -> repository.findMatching(null));
        assertThatNullPointerException().isThrownBy(() -> repository.delete(null));
    }
}
