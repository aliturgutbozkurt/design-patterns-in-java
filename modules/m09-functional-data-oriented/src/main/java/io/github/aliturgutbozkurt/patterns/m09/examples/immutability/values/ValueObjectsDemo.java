package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.values;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/immutability/values/ValueObjectsDemo.java} */
public final class ValueObjectsDemo {

    private ValueObjectsDemo() {}

    public static void main(String[] args) {
        System.out.println("-- BigDecimal vs. Money");
        System.out.println("new BigDecimal(\"2.0\").equals(new BigDecimal(\"2.00\")) = "
                + new BigDecimal("2.0").equals(new BigDecimal("2.00")));
        System.out.println("Money.of(\"2.0\", \"EUR\").equals(Money.of(\"2.00\", \"EUR\")) = "
                + Money.of("2.0", "EUR").equals(Money.of("2.00", "EUR")));
        System.out.println("2.345 EUR -> " + Money.of("2.345", "EUR")
                + ", 2.355 EUR -> " + Money.of("2.355", "EUR") + " (HALF_EVEN)");
        System.out.println("150 JPY -> " + Money.of("150", "JPY") + " (no minor unit)");
        Money three = Money.of("19.99", "EUR").times(3);
        System.out.println("19.99 EUR x 3 = " + three + ", 15% of it = " + three.percent(15));

        System.out.println("-- DateRange on java.time.LocalDate");
        var sale = new DateRange(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 3, 10));
        var next = new DateRange(LocalDate.of(2027, 3, 10), LocalDate.of(2027, 3, 15));
        System.out.println("spring sale " + sale + ": " + sale.days() + " day(s)");
        System.out.println("overlaps " + next + ": " + sale.overlaps(next));
        System.out.println("extended to " + sale.withEnd(LocalDate.of(2027, 3, 11)).endInclusive()
                + ", shifted by " + Period.ofMonths(1) + ": " + sale.shiftedBy(Period.ofMonths(1)));
        System.out.println("original still " + sale);

        System.out.println("-- a mutable key breaks a HashSet");
        var key = new MutableKeyPitfall("SPRING10");
        Set<MutableKeyPitfall> codes = new HashSet<>();
        codes.add(key);
        key.setCode("SPRING20");
        System.out.println("after setCode: contains(key) = " + codes.contains(key) + ", size = " + codes.size());
    }
}
