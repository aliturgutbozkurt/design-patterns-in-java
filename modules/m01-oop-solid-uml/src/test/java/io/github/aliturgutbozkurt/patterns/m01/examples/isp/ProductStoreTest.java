package io.github.aliturgutbozkurt.patterns.m01.examples.isp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.CatalogPage;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.InMemoryProducts;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.ProductReader;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.ProductWriter;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.before.ReadOnlyProductStore;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProductStoreTest {

    static final Product NOTEBOOK = new Product("A-100", "Notebook", new BigDecimal("4.50"));
    static final Product PEN = new Product("B-200", "Fountain pen", new BigDecimal("29.90"));

    /** A reader-only fake: it cannot even be asked to write. */
    record FixedReader(List<Product> products) implements ProductReader {
        @Override public Optional<Product> find(String sku) {
            return products.stream().filter(p -> p.sku().equals(sku)).findFirst();
        }
        @Override public List<Product> list() {
            return products;
        }
    }

    /** Documents the smell on purpose: a read-only view forced to implement writes. */
    @Test
    void beforeReadOnlyStoreThrowsOnEveryWrite() {
        var store = new ReadOnlyProductStore(List.of(NOTEBOOK, PEN));
        assertThat(store.find("A-100")).contains(NOTEBOOK);
        assertThatThrownBy(() -> store.save(NOTEBOOK)).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("read-only store");
        assertThatThrownBy(() -> store.delete("A-100")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> store.importAll(List.of(PEN))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void theJdkHasTheSameSmell() {
        assertThatThrownBy(() -> List.of("a").add("b")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void afterCatalogPageRendersFromAReaderOnlyFake() {
        assertThat(new CatalogPage(new FixedReader(List.of(NOTEBOOK, PEN))).render()).isEqualTo("""
                Catalog (2 products)
                A-100  Notebook          4.50
                B-200  Fountain pen     29.90
                """);
    }

    @Test
    void afterInMemoryProductsRoundTripsAndListsBySku() {
        var products = new InMemoryProducts();
        products.importAll(List.of(PEN, NOTEBOOK));
        assertThat(products.find("B-200")).contains(PEN);
        assertThat(products.list()).containsExactly(NOTEBOOK, PEN);
        products.delete("B-200");
        assertThat(products.find("B-200")).isEmpty();
    }

    @Test
    void afterInMemoryProductsPlaysBothRoles() {
        assertThat(new InMemoryProducts()).isInstanceOf(ProductReader.class).isInstanceOf(ProductWriter.class);
        assertThat(ProductWriter.class.isAssignableFrom(CatalogPage.class)).isFalse();
    }

    @Test
    void demoShowsTheSmellAndTheSplit() {
        assertThat(Console.capture(() -> StoreDemo.main(new String[0]))).isEqualTo("""
                == before: a read-only view of a fat ProductStore ==
                find A-100 -> Notebook
                save -> UnsupportedOperationException: read-only store
                JDK: List.of(...).add(...) -> UnsupportedOperationException
                == after: ProductReader + ProductWriter ==
                Catalog (2 products)
                A-100  Notebook          4.50
                B-200  Fountain pen     29.90
                """);
    }
}
