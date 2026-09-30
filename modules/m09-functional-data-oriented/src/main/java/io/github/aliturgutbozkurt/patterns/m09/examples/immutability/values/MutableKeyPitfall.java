package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.values;

import java.util.Objects;

/**
 * The pitfall: a mutable promotion-code key. Its hash code depends on a field that can change, so after
 * {@link #setCode} a {@code HashSet} looks in the wrong bucket and can no longer find it. A record key cannot do this.
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public final class MutableKeyPitfall {

    private String code;

    public MutableKeyPitfall(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MutableKeyPitfall that && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
