package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. What happened to an event: accepted (with the new status) or refused (with a reason). */
public sealed interface Outcome {

    record Accepted(DocumentStatus status) implements Outcome {
        public Accepted {
            Objects.requireNonNull(status, "status");
        }
    }

    record Refused(String reason) implements Outcome {
        public Refused {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
