package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.holder.CurrencyTable;
import java.math.BigDecimal;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/singleton/HolderSingletonDemo.java}
 *
 * @see "m02 lesson, section Singleton"
 */
public final class HolderSingletonDemo {

    private HolderSingletonDemo() {}

    public static void main(String[] args) {
        var table = CurrencyTable.getInstance();
        System.out.println("100 USD = " + table.toEur(new BigDecimal("100"), "USD") + " EUR");
        System.out.println("1000 TRY = " + table.toEur(new BigDecimal("1000"), "TRY") + " EUR");
        System.out.println("same instance? " + (table == CurrencyTable.getInstance()));
        System.out.println("instances created: " + CurrencyTable.creations());
    }
}
