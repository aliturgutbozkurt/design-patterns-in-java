package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import org.junit.jupiter.api.Test;

class ClassicCoffeeTest {

    @Test
    void priceIsTheBasePlusEveryCondiment() {
        Beverage order = new Syrup(new Milk(new Espresso()));
        assertThat(order.priceInKurus()).isEqualTo(4500 + 500 + 750);
    }

    @Test
    void sameCondimentTwiceCountsTwice() {
        Beverage order = new ExtraShot(new ExtraShot(new HouseBlend()));
        assertThat(order.priceInKurus()).isEqualTo(4000 + 1500 + 1500);
        assertThat(order.description()).isEqualTo("House Blend, Extra Shot, Extra Shot");
    }

    @Test
    void descriptionListsCondimentsInWrappingOrder() {
        assertThat(new Syrup(new Milk(new Espresso())).description()).isEqualTo("Espresso, Milk, Syrup");
        assertThat(new Milk(new Syrup(new Espresso())).description()).isEqualTo("Espresso, Syrup, Milk");
    }

    @Test
    void decoratedBeverageIsStillABeverageAndCanBeWrappedAgain() {
        Beverage withMilk = new Milk(new Espresso());
        Beverage wrappedAgain = new Syrup(withMilk);
        assertThat(wrappedAgain).isInstanceOf(Beverage.class).isInstanceOf(CondimentDecorator.class);
        assertThat(wrappedAgain.priceInKurus()).isEqualTo(withMilk.priceInKurus() + 750);
    }

    @Test
    void equalOrdersAreDifferentObjects() {
        assertThat(new Milk(new Espresso())).isNotEqualTo(new Milk(new Espresso()));
    }

    @Test
    void rejectsAMissingBeverage() {
        assertThatNullPointerException().isThrownBy(() -> new Milk(null));
    }

    @Test
    void demoPrintsOrdersWithPrices() {
        assertThat(Console.capture(() -> CoffeeDemo.main(new String[0]))).isEqualTo("""
                Espresso: 45.00 TL
                Espresso, Milk, Syrup: 57.50 TL
                House Blend, Extra Shot, Extra Shot, Milk: 75.00 TL
                """);
    }
}
