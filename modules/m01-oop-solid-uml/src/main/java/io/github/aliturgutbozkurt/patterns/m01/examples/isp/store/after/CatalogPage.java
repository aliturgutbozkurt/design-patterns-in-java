package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.store.Product;
import java.util.Objects;

/**
 * A read-only client: it depends on {@link ProductReader}, so write methods are not even visible to it.
 *
 * @see "m01 lesson, section ISP"
 */
public final class CatalogPage {

    private final ProductReader products;

    public CatalogPage(ProductReader products) {
        this.products = Objects.requireNonNull(products, "products");
    }

    public String render() {
        var all = products.list();
        var text = new StringBuilder("Catalog (" + all.size() + " products)\n");
        for (Product product : all) {
            text.append("%-6s %-14s%8s".formatted(product.sku(), product.name(), product.price().toPlainString()))
                    .append('\n');
        }
        return text.toString();
    }
}
