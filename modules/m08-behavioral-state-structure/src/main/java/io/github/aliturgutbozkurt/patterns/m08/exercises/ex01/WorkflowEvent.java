package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import java.util.Objects;

/**
 * GIVEN — do not modify. Everything a person can do to a document. Every event names who did it ({@code by}, never
 * blank); whether the event is allowed is decided by the workflow, not by the event.
 */
public sealed interface WorkflowEvent {

    /** Who triggered the event. */
    String by();

    record Submit(String by) implements WorkflowEvent {
        public Submit {
            requireText(by, "by");
        }
    }

    record Approve(String by) implements WorkflowEvent {
        public Approve {
            requireText(by, "by");
        }
    }

    record RequestChanges(String by, String comment) implements WorkflowEvent {
        public RequestChanges {
            requireText(by, "by");
            requireText(comment, "comment");
        }
    }

    record Revise(String by) implements WorkflowEvent {
        public Revise {
            requireText(by, "by");
        }
    }

    record Publish(String by) implements WorkflowEvent {
        public Publish {
            requireText(by, "by");
        }
    }

    record Archive(String by) implements WorkflowEvent {
        public Archive {
            requireText(by, "by");
        }
    }

    private static void requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
