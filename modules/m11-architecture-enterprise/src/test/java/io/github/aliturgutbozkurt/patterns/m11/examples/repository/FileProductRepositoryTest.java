package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Category;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.FileProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.Sku;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileProductRepositoryTest extends ProductRepositoryContract {

    @TempDir
    Path directory;

    @Override
    protected ProductRepository newRepository() {
        return new FileProductRepository(directory.resolve("products.txt"));
    }

    @Test
    void newInstanceOnTheSamePathSeesEarlierSaves() {
        repository.save(PATTERNS);
        repository.save(PEN);
        ProductRepository afterRestart = newRepository();
        assertThat(afterRestart.findAll()).containsExactly(PATTERNS, PEN);
        assertThat(afterRestart.findById(new Sku("PEN-7"))).contains(PEN);
    }

    @Test
    void storesOneProductPerLineWithPipeSeparatedFields() throws IOException {
        repository.save(PEN);
        repository.save(PATTERNS);
        assertThat(Files.readAllLines(directory.resolve("products.txt")))
                .containsExactly("BOOK-1|Design Patterns|BOOKS|3990", "PEN-7|Fountain Pen|STATIONERY|1200");
    }

    @Test
    void nameContainingTheSeparatorIsRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repository.save(product("BOOK-3", "Good|Bad", Category.BOOKS, "1.00")))
                .withMessage("product name must not contain '|' or a line break: Good|Bad");
        assertThat(repository.count()).isZero();
    }

    @Test
    void missingFileIsAnEmptyRepository() {
        assertThat(repository.findAll()).isEmpty();
        assertThat(Files.exists(directory.resolve("products.txt"))).isFalse();
    }
}
