package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring;

import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Express;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Pickup;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Standard;
import java.math.BigDecimal;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/refactoring/ShippingRefactoringDemo.java} */
public final class ShippingRefactoringDemo {

    private ShippingRefactoringDemo() {}

    public static void main(String[] args) {
        var before = new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.before.ShippingCalculator();
        var after = new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingCalculator();

        show("STANDARD 2500 g 100 km", before.costCents("STANDARD", 2500, 100), after, new Standard(2500, 100));
        show("EXPRESS  1500 g 600 km", before.costCents("EXPRESS", 1500, 600), after, new Express(1500, 600));
        show("PICKUP", before.costCents("PICKUP", 900, 0), after, new Pickup());
        System.out.println("EXPRES (typo)          before " + price(before.costCents("EXPRES", 1500, 600))
                + " | after: does not compile — there is no such record");
    }

    private static void show(String label, long beforeCents,
            io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingCalculator after,
            ShippingMethod method) {
        System.out.println(String.format("%-22s", label) + " before " + price(beforeCents)
                + " | after " + price(after.costCents(method)));
    }

    private static String price(long cents) {
        return BigDecimal.valueOf(cents, 2).toPlainString();
    }
}
