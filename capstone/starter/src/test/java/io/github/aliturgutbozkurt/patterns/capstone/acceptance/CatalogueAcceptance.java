package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.money;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.Category.BOOKS;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.Category.ELECTRONICS;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.Category.HOME;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.Category.TOYS;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType.DIGITAL;
import static io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType.PHYSICAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductQuery;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductView;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** F1 — catalogue: add, find, search, restock (brief, Business rules — Catalogue). */
public abstract class CatalogueAcceptance extends AcceptanceContract {

    private CatalogueUseCase catalogue() {
        return shop().catalogue();
    }

    private static ProductSpec spec(String sku, String name, Category category, ProductType type, String price,
                                    int stock) {
        return new ProductSpec(sku(sku), name, category, type, money(price), stock);
    }

    private List<String> skus(ProductQuery query) {
        return catalogue().search(query).stream().map(ProductView::sku).map(Sku::value).toList();
    }

    @Test
    void addsProductAndFindsItBySku() {
        ProductView lamp = catalogue().add(spec("ELE-002", "Pattern Lamp", ELECTRONICS, PHYSICAL, "129.50", 7));
        ProductView course = catalogue().add(spec("DIG-002", "Audio Course", BOOKS, DIGITAL, "59.00", 0));

        assertThat(lamp).isEqualTo(new ProductView(sku("ELE-002"), "Pattern Lamp", ELECTRONICS, PHYSICAL,
                money("129.50"), 7));
        assertThat(course).isEqualTo(new ProductView(sku("DIG-002"), "Audio Course", BOOKS, DIGITAL,
                money("59.00"), 0));
        assertThat(catalogue().find(sku("ELE-002"))).contains(lamp);
        assertThat(catalogue().find(sku("DIG-002"))).contains(course);
        assertThat(catalogue().find(sku("BOK-001"))).contains(new ProductView(sku("BOK-001"),
                "Design Patterns Handbook", BOOKS, PHYSICAL, money("250.00"), 20));
        assertThat(catalogue().find(sku("XXX-999"))).isEmpty();
    }

    @Test
    void rejectsDuplicateSku() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> catalogue().add(spec("BOK-001", "Another Book", BOOKS, PHYSICAL, "10.00", 1)))
                .withMessage("duplicate SKU: BOK-001");

        assertThat(kit().product("BOK-001").name()).isEqualTo("Design Patterns Handbook");
        assertThat(catalogue().search(ProductQuery.all())).hasSize(6);
    }

    @Test
    void rejectsInvalidProductSpec() {
        CatalogueUseCase catalogue = catalogue();

        assertThatIllegalArgumentException().as("malformed SKU")
                .isThrownBy(() -> catalogue.add(spec("bok-1", "Lower Case", BOOKS, PHYSICAL, "10.00", 1)));
        assertThatIllegalArgumentException().as("blank name")
                .isThrownBy(() -> catalogue.add(spec("BOK-010", "  ", BOOKS, PHYSICAL, "10.00", 1)));
        assertThatIllegalArgumentException().as("zero price")
                .isThrownBy(() -> catalogue.add(spec("BOK-011", "Free Book", BOOKS, PHYSICAL, "0.00", 1)));
        assertThatIllegalArgumentException().as("negative stock")
                .isThrownBy(() -> catalogue.add(spec("BOK-012", "Owed Book", BOOKS, PHYSICAL, "10.00", -1)));
        assertThatIllegalArgumentException().as("digital product with stock")
                .isThrownBy(() -> catalogue.add(spec("DIG-010", "Stocked E-book", BOOKS, DIGITAL, "10.00", 3)));

        assertThat(catalogue.search(ProductQuery.all())).as("nothing was added").hasSize(6);
    }

    @Test
    void searchByCategoryIsSortedBySku() {
        catalogue().add(spec("BOK-000", "Patterns Primer", BOOKS, PHYSICAL, "50.00", 3));

        assertThat(skus(ProductQuery.all().inCategory(BOOKS)))
                .containsExactly("BOK-000", "BOK-001", "BOK-002", "DIG-001");
        assertThat(skus(ProductQuery.all()))
                .containsExactly("BOK-000", "BOK-001", "BOK-002", "DIG-001", "ELE-001", "HOM-001", "TOY-001");
        assertThat(skus(ProductQuery.all().inCategory(ELECTRONICS))).containsExactly("ELE-001");
    }

    @Test
    void searchCombinesCategoryPriceAndNameCriteria() {
        assertThat(skus(ProductQuery.all().inCategory(BOOKS).inCategory(TOYS).priceAtMost(money("250.00"))
                .nameContaining("PATTERN"))).containsExactly("BOK-001", "TOY-001");
        assertThat(skus(ProductQuery.all().priceAtMost(money("99.90")))).containsExactly("DIG-001", "HOM-001");
        assertThat(skus(ProductQuery.all().nameContaining("hub"))).containsExactly("ELE-001");
        assertThat(skus(ProductQuery.all().inCategory(HOME).nameContaining("book"))).isEmpty();
    }

    @Test
    void restockIncreasesStockOfPhysicalProduct() {
        ProductView restocked = catalogue().restock(sku("TOY-001"), 4);

        assertThat(restocked.stock()).isEqualTo(10);
        assertThat(kit().stock("TOY-001")).isEqualTo(10);
        assertThat(catalogue().restock(sku("TOY-001"), 1).stock()).isEqualTo(11);
    }

    @Test
    void restockRejectsDigitalProductAndNonPositiveQuantity() {
        CatalogueUseCase catalogue = catalogue();

        assertThatIllegalArgumentException().as("digital product")
                .isThrownBy(() -> catalogue.restock(sku("DIG-001"), 5));
        assertThatIllegalArgumentException().as("zero").isThrownBy(() -> catalogue.restock(sku("TOY-001"), 0));
        assertThatIllegalArgumentException().as("negative").isThrownBy(() -> catalogue.restock(sku("TOY-001"), -2));
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> catalogue.restock(sku("XXX-999"), 1))
                .withMessage("unknown product: XXX-999");

        assertThat(kit().stock("TOY-001")).isEqualTo(6);
        assertThat(kit().stock("DIG-001")).isZero();
    }
}
