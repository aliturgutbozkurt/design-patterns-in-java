package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.util.Objects;

/**
 * GIVEN — do not modify. Identifies a cart; the shop generates {@code cart-1}, {@code cart-2}, … per instance.
 *
 * @param value a non-blank id
 * @see "capstone brief §2.2 — Money and ids"
 */
public record CartId(String value) {

    public CartId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank cart id");
        }
    }
}
