package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * GIVEN — do not modify. A stock-keeping unit: three capital letters, a dash and three digits ({@code BOK-001}).
 *
 * @param value the code
 * @see "capstone brief §2.2 — Catalogue"
 */
public record Sku(String value) {

    private static final Pattern FORMAT = Pattern.compile("[A-Z]{3}-[0-9]{3}");

    public Sku {
        Objects.requireNonNull(value, "value");
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("malformed SKU: " + value);
        }
    }
}
