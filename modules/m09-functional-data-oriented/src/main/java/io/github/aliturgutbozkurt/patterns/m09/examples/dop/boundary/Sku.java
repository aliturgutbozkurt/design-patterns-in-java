package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A stock-keeping unit such as {@code MUG-0001}. If you hold a {@code Sku}, it is valid: the constructor is the only
 * way in.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public record Sku(String value) {

    private static final Pattern FORMAT = Pattern.compile("[A-Z]{3}-\\d{4}");

    public Sku {
        Objects.requireNonNull(value, "sku");
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("sku must match AAA-9999: \"" + value + "\"");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
