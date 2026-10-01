package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.AgeAtLeast;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.CountryIs;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Customer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.HasTag;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.Promotion;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules.SpentAtLeast;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/interpreter/PromotionRulesDemo.java}
 *
 * @see "m08 lesson, section Interpreter"
 */
public final class PromotionRulesDemo {

    private PromotionRulesDemo() {}

    /** The client builds each sentence (a rule tree) from combinators. */
    public static List<Promotion> promotions() {
        return List.of(
                new Promotion("WELCOME", new AgeAtLeast(18).and(new CountryIs("TR").or(new HasTag("vip")))),
                new Promotion("BIGSPENDER", new SpentAtLeast(100_000).and(new HasTag("employee").negate())),
                new Promotion("YOUTH", new AgeAtLeast(18).negate()),
                new Promotion("NEWCOMER", new HasTag("vip").or(new SpentAtLeast(100_000)).negate()));
    }

    public static List<Customer> customers() {
        return List.of(
                new Customer("C-1", 25, "TR", 5_000, Set.of()),
                new Customer("C-2", 17, "TR", 200_000, Set.of("vip")),
                new Customer("C-3", 40, "DE", 150_000, Set.of("vip")),
                new Customer("C-4", 30, "DE", 120_000, Set.of("employee")));
    }

    public static void main(String[] args) {
        List<Promotion> promotions = promotions();
        promotions.forEach(p -> System.out.println(String.format(Locale.ROOT, "%-11s %s", p.code(),
                p.eligibility().render())));
        System.out.println();

        var header = new StringBuilder(cell("customer"));
        promotions.forEach(p -> header.append(cell(p.code())));
        System.out.println(header.toString().strip());
        for (Customer customer : customers()) {
            var row = new StringBuilder(cell(customer.id()));
            promotions.forEach(p -> row.append(cell(p.isEligible(customer) ? "yes" : "-")));
            System.out.println(row.toString().strip());
        }
    }

    private static String cell(String text) {
        return String.format(Locale.ROOT, "%-12s", text);
    }
}
