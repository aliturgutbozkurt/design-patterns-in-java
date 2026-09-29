package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

/**
 * A payment card (a fake: only the number, at least 12 digits).
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public record Card(String number) {

    public Card {
        if (number == null || !number.matches("\\d{12,19}")) {
            throw new IllegalArgumentException("card number must have 12 to 19 digits");
        }
    }

    public String lastFour() {
        return number.substring(number.length() - 4);
    }

    @Override
    public String toString() {
        return "Card[ending " + lastFour() + "]";
    }
}
