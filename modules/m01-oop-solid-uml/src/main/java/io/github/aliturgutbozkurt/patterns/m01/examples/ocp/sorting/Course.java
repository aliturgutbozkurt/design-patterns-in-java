package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting;

import java.util.Objects;

/**
 * A course in the catalog; shared by the before and after versions.
 *
 * @see "m01 lesson, section OCP"
 */
public record Course(String code, String title, int credits) {

    public Course {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(title, "title");
        if (credits <= 0) {
            throw new IllegalArgumentException("credits must be positive: " + credits);
        }
    }

    /** The letters of the code: {@code "CENG101"} → {@code "CENG"}. */
    public String department() {
        return code.replaceAll("[0-9]", "");
    }
}
