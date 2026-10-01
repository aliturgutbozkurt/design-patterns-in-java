package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/composition/pricing/PricePipelineDemo.java} */
public final class PricePipelineDemo {

    private PricePipelineDemo() {}

    public static void main(String[] args) {
        var price = new Price(100_00);
        Function<Price, Price> tenOff = PriceRules.percentOff(10);
        Function<Price, Price> tax = PriceRules.addTax(20);

        System.out.println("-- one rule at a time, composed with andThen");
        System.out.println(price + " -> 10% off -> " + tenOff.apply(price) + " -> +20% tax -> "
                + tenOff.andThen(tax).apply(price));

        System.out.println("-- order matters");
        Function<Price, Price> tenEuros = PriceRules.amountOff(10_00);
        System.out.println("amountOff(10.00).andThen(addTax(20)) = " + tenEuros.andThen(tax).apply(price));
        System.out.println("addTax(20).andThen(amountOff(10.00)) = " + tax.andThen(tenEuros).apply(price));

        System.out.println("-- a list of rules folded into one function (reduce(identity, andThen))");
        Function<Price, Price> springSale = PricePipeline.of(List.of(
                PriceRules.percentOff(20), PriceRules.amountOff(30_00), PriceRules.floorAt(10_00)));
        for (long cents : new long[] {40_00, 120_00}) {
            var before = new Price(cents);
            System.out.println(String.format(Locale.ROOT, "spring sale on %6s = %s", before, springSale.apply(before)));
        }

        System.out.println("-- eligibility rules combined with and / or / not");
        for (Customer c : List.of(new Customer("Ada", true, 0, false), new Customer("Grace", false, 7, false),
                new Customer("Linus", false, 1, false), new Customer("Mallory", true, 12, true))) {
            System.out.println(String.format(Locale.ROOT, "%-8s freeShipping=%-5s newcomer=%s", c.name(),
                    Eligibility.freeShipping().test(c), Eligibility.newcomer().test(c)));
        }
    }
}
