package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import java.util.List;
import java.util.Set;

/** GIVEN — do not modify. A document moving through draft → review → approval → publication (the State context). */
public interface DocumentWorkflow {

    DocumentStatus status();

    /** Applies {@code event} if the current status allows it; a refused event changes nothing. */
    Outcome handle(WorkflowEvent event);

    /** The reviewers who have approved in the current review round, in approval order (read-only). */
    Set<String> approvals();

    /** Every accepted event, oldest first (read-only). */
    List<HistoryEntry> history();
}
