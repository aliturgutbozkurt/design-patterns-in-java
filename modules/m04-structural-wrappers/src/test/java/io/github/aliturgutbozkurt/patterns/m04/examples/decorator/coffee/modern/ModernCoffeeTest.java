package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.Espresso;
import io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.HouseBlend;
import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import org.junit.jupiter.api.Test;

class ModernCoffeeTest {

    @Test
    void samePricesAndDescriptionsAsTheClassicVersion() {
        var classic = new io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.Syrup(
                new io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.Milk(new Espresso()));
        Beverage modern = new Syrup(new Milk(Coffee.ESPRESSO));
        assertThat(modern.description()).isEqualTo(classic.description());
        assertThat(modern.priceInKurus()).isEqualTo(classic.priceInKurus());

        var classicShots = new io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.ExtraShot(
                new io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic.ExtraShot(
                        new HouseBlend()));
        Beverage modernShots = new ExtraShot(new ExtraShot(Coffee.HOUSE_BLEND));
        assertThat(modernShots.description()).isEqualTo(classicShots.description());
        assertThat(modernShots.priceInKurus()).isEqualTo(classicShots.priceInKurus());
    }

    @Test
    void twoEqualOrdersAreEqual() {
        Beverage first = new Syrup(new Milk(Coffee.ESPRESSO));
        Beverage second = new Syrup(new Milk(Coffee.ESPRESSO));
        assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(new Milk(new Syrup(Coffee.ESPRESSO)));
    }

    @Test
    void recordsPrintTheirWrappingStructure() {
        assertThat(new Milk(Coffee.ESPRESSO)).hasToString("Milk[inner=ESPRESSO]");
    }

    @Test
    void rejectsANullInnerBeverage() {
        assertThatNullPointerException().isThrownBy(() -> new Milk(null));
        assertThatNullPointerException().isThrownBy(() -> new Syrup(null));
        assertThatNullPointerException().isThrownBy(() -> new ExtraShot(null));
    }

    @Test
    void demoPrintsTheSameOrdersAsTheClassicDemo() {
        assertThat(Console.capture(() -> CoffeeDemo.main(new String[0]))).isEqualTo("""
                Espresso: 45.00 TL
                Espresso, Milk, Syrup: 57.50 TL
                House Blend, Extra Shot, Extra Shot, Milk: 75.00 TL
                same order twice is equal: true
                Syrup[inner=Milk[inner=ESPRESSO]]
                """);
    }
}
