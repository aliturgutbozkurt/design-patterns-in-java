package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config.ShopCompositionRoot;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileBackedShopTest extends ShopUseCaseContract {

    @TempDir
    Path directory;

    @Override
    protected ShopCompositionRoot newRoot() {
        return ShopCompositionRoot.fileBacked(directory.resolve("orders.txt"));
    }

    @Test
    void newRootOnTheSameFileSeesEarlierOrdersAndContinuesTheIds() {
        shop.cli().handle("place alice BOOK-1:2 PEN-7:1");
        ShopCompositionRoot restarted = newRoot();
        assertThat(restarted.orders().findById(new OrderId("order-1"))).isPresent();
        assertThat(restarted.cli().handle("place erin MUG-3:1")).isEqualTo("PLACED order-2 total 8.75");
    }
}
