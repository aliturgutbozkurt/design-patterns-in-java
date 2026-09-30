package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A library member. */
public record Member(String id, String name) {

    public Member {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
    }
}
