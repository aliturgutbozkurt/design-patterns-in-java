package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

import java.util.List;

/** GIVEN — do not modify. Named templates; every {@code create} returns a fresh copy. */
public interface ShapeRegistry {

    void register(String name, Shape template);

    /** @throws IllegalArgumentException for an unknown name */
    Shape create(String name);

    /** Registered names in alphabetical order. */
    List<String> names();
}
