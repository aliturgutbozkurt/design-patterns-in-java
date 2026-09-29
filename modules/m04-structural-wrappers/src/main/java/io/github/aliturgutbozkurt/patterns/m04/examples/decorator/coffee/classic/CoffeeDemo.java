package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

import java.math.BigDecimal;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/decorator/coffee/classic/CoffeeDemo.java} */
public final class CoffeeDemo {

    private CoffeeDemo() {}

    public static void main(String[] args) {
        print(new Espresso());
        print(new Syrup(new Milk(new Espresso())));                     // innermost first: Espresso, Milk, Syrup
        print(new Milk(new ExtraShot(new ExtraShot(new HouseBlend()))));
    }

    private static void print(Beverage beverage) {
        System.out.println(beverage.description() + ": " + BigDecimal.valueOf(beverage.priceInKurus(), 2) + " TL");
    }
}
