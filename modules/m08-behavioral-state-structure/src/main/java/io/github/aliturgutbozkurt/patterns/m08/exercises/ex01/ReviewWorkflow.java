package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import java.util.List;
import java.util.Set;

/** Assignment 01 — your document-approval workflow (the State pattern). */
public class ReviewWorkflow implements DocumentWorkflow {

    private final String author;
    private final int requiredApprovals;

    public ReviewWorkflow(String author, int requiredApprovals) {
        // TODO(ex01): reject a blank author and requiredApprovals < 1 with IllegalArgumentException.
        this.author = author;
        this.requiredApprovals = requiredApprovals;
    }

    @Override
    public DocumentStatus status() {
        throw new UnsupportedOperationException("TODO(ex01): implement status()");
    }

    @Override
    public Outcome handle(WorkflowEvent event) {
        // TODO(ex01): one switch over (status, event) with record patterns and no default. Accepted events append a
        //  HistoryEntry; refused events change nothing. See the brief for the exact refusal reasons.
        throw new UnsupportedOperationException("TODO(ex01): implement handle(WorkflowEvent)");
    }

    @Override
    public Set<String> approvals() {
        throw new UnsupportedOperationException("TODO(ex01): implement approvals()");
    }

    @Override
    public List<HistoryEntry> history() {
        throw new UnsupportedOperationException("TODO(ex01): implement history()");
    }
}
