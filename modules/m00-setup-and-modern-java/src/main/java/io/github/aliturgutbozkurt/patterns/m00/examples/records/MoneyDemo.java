package io.github.aliturgutbozkurt.patterns.m00.examples.records;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/records/MoneyDemo.java} */
public final class MoneyDemo {

    private MoneyDemo() {}

    public static void main(String[] args) {
        Money price = Money.of("12.50", "EUR");
        Money shipping = Money.of("4.99", "EUR");
        System.out.println("price    = " + price);
        System.out.println("shipping = " + shipping);
        System.out.println("total    = " + price.plus(shipping));
        System.out.println("3 × price = " + price.times(3));
        System.out.println(price + " equals 12.5 EUR? " + price.equals(Money.of("12.5", "EUR")));
        try {
            price.plus(Money.of("1.00", "USD"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
