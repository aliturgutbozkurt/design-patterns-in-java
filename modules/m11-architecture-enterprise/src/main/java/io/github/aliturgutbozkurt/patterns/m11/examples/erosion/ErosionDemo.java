package io.github.aliturgutbozkurt.patterns.m11.examples.erosion;

import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.domain.Invoice;
import java.math.BigDecimal;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/erosion/ErosionDemo.java}
 *
 * @see "m11 lesson, section Architecture rules"
 */
public final class ErosionDemo {

    private ErosionDemo() {}

    public static void main(String[] args) {
        System.out.println(new Invoice("INV-1", new BigDecimal("99.00")).save());
        System.out.println("compiles and runs — the broken dependency direction is invisible to javac");
    }
}
