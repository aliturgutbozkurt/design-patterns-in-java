package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. One accepted event: the status before, the event, the status after. */
public record HistoryEntry(DocumentStatus from, WorkflowEvent event, DocumentStatus to) {

    public HistoryEntry {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(to, "to");
    }
}
