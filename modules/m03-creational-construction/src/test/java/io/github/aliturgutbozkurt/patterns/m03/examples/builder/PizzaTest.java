package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.MenuItem;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza.Crust;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza.Size;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PizzaTest {

    @Test
    void buildsWithDefaults() {
        Pizza pizza = Pizza.builder(Size.MEDIUM).build();
        assertThat(pizza.crust()).isEqualTo(Crust.CLASSIC);
        assertThat(pizza.toppings()).isEmpty();
        assertThat(pizza.extraCheese()).isFalse();
        assertThat(pizza.describe()).isEqualTo("medium pizza, classic crust, no toppings: 10.00");
    }

    @Test
    void priceAddsSizeToppingsCheeseAndCrust() {
        Pizza pizza = Pizza.builder(Size.LARGE).topping("mushroom").topping("olives")
                .crust(Crust.STUFFED).extraCheese().build();
        assertThat(pizza.price()).isEqualTo(new BigDecimal("19.00"));
        assertThat(pizza.describe()).isEqualTo("large pizza, stuffed crust, mushroom + olives, extra cheese: 19.00");
    }

    @Test
    void isAMenuItemWhoseStateWasComputedBeforeSuper() {
        MenuItem item = Pizza.builder(Size.SMALL).topping("basil").build();
        assertThat(item.name()).isEqualTo("small pizza");
        assertThat(item.price()).isEqualTo(new BigDecimal("9.50"));
    }

    @Test
    void rejectsMoreThanFiveToppings() {
        var builder = Pizza.builder(Size.SMALL);
        for (String topping : new String[] {"a", "b", "c", "d", "e", "f"}) {
            builder.topping(topping);
        }
        assertThatIllegalStateException().isThrownBy(builder::build).withMessage("at most 5 toppings, got 6");
    }

    @Test
    void rejectsDuplicateToppings() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Pizza.builder(Size.SMALL).topping("olives").topping("olives").build())
                .withMessage("duplicate topping: olives");
    }

    @Test
    void builtPizzaIsImmutableAndIndependentOfTheBuilder() {
        var builder = Pizza.builder(Size.SMALL).topping("basil");
        Pizza first = builder.build();
        builder.topping("ham");
        assertThat(first.toppings()).containsExactly("basil");
        assertThatThrownBy(() -> first.toppings().add("x")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void sizeIsRequired() {
        assertThatNullPointerException().isThrownBy(() -> Pizza.builder(null));
    }

    @Test
    void demoPrintsPizzasAndAValidationError() {
        assertThat(Console.capture(() -> PizzaDemo.main(new String[0]))).isEqualTo("""
                medium pizza, classic crust, no toppings: 10.00
                large pizza, stuffed crust, mushroom + olives, extra cheese: 19.00
                invalid order: duplicate topping: ham
                """);
    }
}
