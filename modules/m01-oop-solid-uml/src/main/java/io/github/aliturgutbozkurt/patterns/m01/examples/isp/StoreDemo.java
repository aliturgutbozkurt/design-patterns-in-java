package io.github.aliturgutbozkurt.patterns.m01.examples.isp;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.CatalogPage;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after.InMemoryProducts;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.before.ProductStore;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.before.ReadOnlyProductStore;
import java.math.BigDecimal;
import java.util.List;

/**
 * Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/isp/StoreDemo.java}
 *
 * @see "m01 lesson, section ISP"
 */
public final class StoreDemo {

    private StoreDemo() {}

    public static void main(String[] args) {
        var notebook = new Product("A-100", "Notebook", new BigDecimal("4.50"));
        var pen = new Product("B-200", "Fountain pen", new BigDecimal("29.90"));

        System.out.println("== before: a read-only view of a fat ProductStore ==");
        ProductStore store = new ReadOnlyProductStore(List.of(notebook, pen));
        System.out.println("find A-100 -> " + store.find("A-100").map(Product::name).orElse("?"));
        try {
            store.save(notebook);
        } catch (UnsupportedOperationException e) {
            System.out.println("save -> UnsupportedOperationException: " + e.getMessage());
        }
        List<String> fixed = List.of("a");
        try {
            fixed.add("b");
        } catch (UnsupportedOperationException e) {
            System.out.println("JDK: List.of(...).add(...) -> UnsupportedOperationException");
        }

        System.out.println("== after: ProductReader + ProductWriter ==");
        var products = new InMemoryProducts();
        products.importAll(List.of(pen, notebook));  // the writer role
        System.out.print(new CatalogPage(products).render());  // the page only gets the reader role
    }
}
