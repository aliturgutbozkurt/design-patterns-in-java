package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * GIVEN — do not modify. Identifies a placed order: {@code order-<n>} with n ≥ 1, ordered by n (so {@code order-2}
 * comes before {@code order-10}).
 *
 * @param value the id, e.g. {@code order-1}
 * @see "capstone brief, Business rules — Money and ids"
 */
public record OrderId(String value) implements Comparable<OrderId> {

    private static final Pattern FORMAT = Pattern.compile("order-[1-9][0-9]{0,17}");

    public OrderId {
        Objects.requireNonNull(value, "value");
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("malformed order id: " + value);
        }
    }

    /** {@code order-<number>}. */
    public static OrderId of(long number) {
        return new OrderId("order-" + number);
    }

    /** The n of {@code order-<n>}. */
    public long number() {
        return Long.parseLong(value.substring("order-".length()));
    }

    @Override
    public int compareTo(OrderId other) {
        return Long.compare(number(), other.number());
    }
}
