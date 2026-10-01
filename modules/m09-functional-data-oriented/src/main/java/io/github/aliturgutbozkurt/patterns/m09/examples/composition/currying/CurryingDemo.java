package io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying;

import io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying.LogFormat.Level;
import io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying.ShippingRates.Zone;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/composition/currying/CurryingDemo.java} */
public final class CurryingDemo {

    private CurryingDemo() {}

    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> minus = (a, b) -> a - b;
        System.out.println("-- curry / uncurry / partial / flip");
        System.out.println("minus(10, 3)          = " + minus.apply(10, 3));
        System.out.println("curry(minus)(10)(3)   = " + Curry.curry(minus).apply(10).apply(3));
        System.out.println("partial(minus, 10)(3) = " + Curry.partial(minus, 10).apply(3));
        System.out.println("flip(minus)(10, 3)    = " + Curry.flip(minus).apply(10, 3));

        System.out.println("-- zone -> weight -> price: fix the zone once, reuse the tariff");
        for (Zone zone : Zone.values()) {
            Function<Integer, Long> tariff = ShippingRates.forZone(zone);
            var line = new StringBuilder(String.format(Locale.ROOT, "%-10s", zone + ":"));
            for (int grams : new int[] {500, 1500, 2500}) {
                long cents = tariff.apply(grams);
                line.append(String.format(Locale.ROOT, "%4d g -> %d.%02d   ", grams, cents / 100, cents % 100));
            }
            System.out.println(line.toString().stripTrailing());
        }

        System.out.println("-- level -> component -> message");
        Function<String, String> cartWarnings = LogFormat.of(Level.WARN).apply("cart");
        System.out.println(cartWarnings.apply("stock low for MUG-0001"));
        System.out.println(cartWarnings.apply("2 carts expired"));
        System.out.println(LogFormat.curried().apply(Level.INFO).apply("checkout").apply("order A-1 paid"));
    }
}
