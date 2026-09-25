package io.github.aliturgutbozkurt.patterns.m00.examples.records;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/records/PercentageDemo.java} */
public final class PercentageDemo {

    private PercentageDemo() {}

    public static void main(String[] args) {
        Percentage discount = new Percentage("15%");
        System.out.println("\"15%\" -> " + discount);
        Money price = Money.of("20", "EUR");
        System.out.println(discount.value() + "% of " + price + " = " + discount.of(price));
        for (String input : new String[] {"120%", "abc"}) {
            try {
                new Percentage(input);
            } catch (IllegalArgumentException e) {
                System.out.println("\"" + input + "\" rejected: " + e.getMessage());
            }
        }
    }
}
