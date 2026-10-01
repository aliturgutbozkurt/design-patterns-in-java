package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Category;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Product;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Sku;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import org.junit.jupiter.api.Test;

class CatalogValuesTest {

    @Test
    void moneyParsesAndPrintsTwoDecimals() {
        assertThat(Money.of("39.9")).isEqualTo(new Money(3990));
        assertThat(new Money(875)).hasToString("8.75");
        assertThat(Money.of("30.00").cents()).isEqualTo(3000);
    }

    @Test
    void valuesRejectInvalidInput() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Money(-1));
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of("1.234"));
        assertThatIllegalArgumentException().isThrownBy(() -> new Sku("book 1"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product(new Sku("BOOK-1"), " ", Category.BOOKS, new Money(1)));
    }

    @Test
    void demoRunsTheSameQueriesAgainstBothRepositories() {
        assertThat(Console.capture(() -> CatalogRepositoryDemo.main(new String[0]))).isEqualTo("""
                == InMemoryProductRepository
                all:                     [BOOK-1, BOOK-2, MUG-3, PEN-7]
                books up to 30.00:       [BOOK-2]
                "pattern" or stationery: [BOOK-1, MUG-3, PEN-7]
                not books:               [MUG-3, PEN-7]
                == FileProductRepository
                all:                     [BOOK-1, BOOK-2, MUG-3, PEN-7]
                books up to 30.00:       [BOOK-2]
                "pattern" or stationery: [BOOK-1, MUG-3, PEN-7]
                not books:               [MUG-3, PEN-7]
                after a restart the file still holds 4 products; BOOK-1 costs 39.90
                """);
    }
}
