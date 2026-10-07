package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.StockLow;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import org.junit.jupiter.api.Test;

class StockLevelsTest {

    private static PhysicalProduct withStock(int stock) {
        return new PhysicalProduct(new Sku("TOY-001"), "Puzzle", Category.TOYS, Money.of("1.00"), stock);
    }

    @Test
    void alertsOnlyWhenCrossingBelowTheThreshold() {
        assertThat(StockLevels.alert(withStock(5), withStock(4), 5)).contains(new StockLow(new Sku("TOY-001"), 4));
        assertThat(StockLevels.alert(withStock(6), withStock(5), 5)).isEmpty();
        assertThat(StockLevels.alert(withStock(4), withStock(3), 5)).as("already low").isEmpty();
        assertThat(StockLevels.alert(withStock(3), withStock(8), 5)).as("rising").isEmpty();
    }
}
