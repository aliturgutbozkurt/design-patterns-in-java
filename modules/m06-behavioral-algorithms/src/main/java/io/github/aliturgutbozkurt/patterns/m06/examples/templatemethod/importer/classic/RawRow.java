package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.math.BigDecimal;
import java.util.Map;

/**
 * One parsed record before it is checked: its line number in the input and its fields as text. Parsing is
 * format-specific; turning a row into a {@link Product} is the same for every format.
 *
 * @see "m06 lesson, section Template Method"
 */
public record RawRow(int line, Map<String, String> fields) {

    public RawRow {
        fields = Map.copyOf(fields);
    }

    /** Converts the text fields; throws {@link IllegalArgumentException} with a readable reason if a field is bad. */
    public Product toProduct() {
        String price = field("price");
        BigDecimal amount;
        try {
            amount = new BigDecimal(price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("price is not a number: " + price, e);
        }
        return new Product(field("sku"), field("name"), amount);
    }

    private String field(String name) {
        String value = fields.get(name);
        if (value == null) {
            throw new IllegalArgumentException("missing field: " + name);
        }
        return value;
    }
}
