package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An e-mail address, normalised once when it is built (trimmed, lower-cased with {@link Locale#ROOT}), so two
 * spellings of the same address are {@code equals}.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public record Email(String value) {

    private static final Pattern FORMAT = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    public Email {
        Objects.requireNonNull(value, "email");
        String normalised = value.strip().toLowerCase(Locale.ROOT);
        if (!FORMAT.matcher(normalised).matches()) {
            throw new IllegalArgumentException("email must look like name@domain.tld: \"" + value + "\"");
        }
        value = normalised;
    }

    @Override
    public String toString() {
        return value;
    }
}
