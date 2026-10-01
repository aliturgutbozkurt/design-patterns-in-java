package io.github.aliturgutbozkurt.patterns.m10.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A unit of work; ids are unique and order the outcomes. */
public record Job(long id, String payload) {

    public Job {
        Objects.requireNonNull(payload, "payload");
    }
}
