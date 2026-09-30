package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza.Crust;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.Pizza.Size;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/builder/PizzaDemo.java} */
public final class PizzaDemo {

    private PizzaDemo() {}

    public static void main(String[] args) {
        System.out.println(Pizza.builder(Size.MEDIUM).build().describe());
        Pizza deluxe = Pizza.builder(Size.LARGE)
                .topping("mushroom")
                .topping("olives")
                .crust(Crust.STUFFED)
                .extraCheese()
                .build();
        System.out.println(deluxe.describe());
        try {
            Pizza.builder(Size.SMALL).topping("ham").topping("ham").build();
        } catch (IllegalStateException e) {
            System.out.println("invalid order: " + e.getMessage());
        }
    }
}
