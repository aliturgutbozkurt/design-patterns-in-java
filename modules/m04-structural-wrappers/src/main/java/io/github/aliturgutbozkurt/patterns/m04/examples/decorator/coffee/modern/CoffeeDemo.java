package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

import java.math.BigDecimal;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/decorator/coffee/modern/CoffeeDemo.java} */
public final class CoffeeDemo {

    private CoffeeDemo() {}

    public static void main(String[] args) {
        print(Coffee.ESPRESSO);
        print(new Syrup(new Milk(Coffee.ESPRESSO)));
        print(new Milk(new ExtraShot(new ExtraShot(Coffee.HOUSE_BLEND))));

        Beverage order = new Syrup(new Milk(Coffee.ESPRESSO));
        Beverage sameOrder = new Syrup(new Milk(Coffee.ESPRESSO));
        System.out.println("same order twice is equal: " + order.equals(sameOrder));   // records: value equality
        System.out.println(order);                                                     // records: free toString
    }

    private static void print(Beverage beverage) {
        System.out.println(beverage.description() + ": " + BigDecimal.valueOf(beverage.priceInKurus(), 2) + " TL");
    }
}
