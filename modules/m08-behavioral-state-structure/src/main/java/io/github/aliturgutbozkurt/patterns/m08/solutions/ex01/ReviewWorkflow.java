package io.github.aliturgutbozkurt.patterns.m08.solutions.ex01;

import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.APPROVED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.ARCHIVED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.CHANGES_REQUESTED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.DRAFT;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.IN_REVIEW;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.PUBLISHED;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentWorkflow;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.HistoryEntry;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Outcome;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Outcome.Accepted;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Outcome.Refused;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Approve;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Archive;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Publish;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.RequestChanges;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Revise;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Submit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Reference solution for assignment 01: the transition table is one {@code switch} over the event with
 * {@code when} guards on the status. The last case names every remaining event type instead of using
 * {@code default}, so a new event type is a compile error here.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public final class ReviewWorkflow implements DocumentWorkflow {

    private final String author;
    private final int requiredApprovals;
    private final Set<String> approvals = new LinkedHashSet<>();
    private final List<HistoryEntry> history = new ArrayList<>();
    private DocumentStatus status = DRAFT;

    public ReviewWorkflow(String author, int requiredApprovals) {
        Objects.requireNonNull(author, "author");
        if (author.isBlank()) {
            throw new IllegalArgumentException("author must not be blank");
        }
        if (requiredApprovals < 1) {
            throw new IllegalArgumentException("requiredApprovals must be at least 1: " + requiredApprovals);
        }
        this.author = author;
        this.requiredApprovals = requiredApprovals;
    }

    @Override
    public DocumentStatus status() {
        return status;
    }

    @Override
    public Outcome handle(WorkflowEvent event) {
        Objects.requireNonNull(event, "event");
        if (status == ARCHIVED) {
            return new Refused("document is archived");
        }
        return switch (event) {
            case Archive _ -> move(event, ARCHIVED);
            case Submit(var by) when status == DRAFT ->
                    by.equals(author) ? move(event, IN_REVIEW) : new Refused("only the author can submit");
            case Approve(var by) when status == IN_REVIEW -> approve(event, by);
            case RequestChanges _ when status == IN_REVIEW -> {
                approvals.clear();
                yield move(event, CHANGES_REQUESTED);
            }
            case Revise(var by) when status == CHANGES_REQUESTED ->
                    by.equals(author) ? move(event, IN_REVIEW) : new Refused("only the author can revise");
            case Publish _ when status == APPROVED -> move(event, PUBLISHED);
            case Submit _, Approve _, RequestChanges _, Revise _, Publish _ ->
                    new Refused(event.getClass().getSimpleName() + " not allowed in " + status);
        };
    }

    @Override
    public Set<String> approvals() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(approvals));
    }

    @Override
    public List<HistoryEntry> history() {
        return List.copyOf(history);
    }

    private Outcome approve(WorkflowEvent event, String reviewer) {
        if (reviewer.equals(author)) {
            return new Refused("author cannot approve own document");
        }
        if (approvals.contains(reviewer)) {
            return new Refused("already approved by " + reviewer);
        }
        approvals.add(reviewer);
        return move(event, approvals.size() >= requiredApprovals ? APPROVED : IN_REVIEW);
    }

    private Outcome move(WorkflowEvent event, DocumentStatus next) {
        history.add(new HistoryEntry(status, event, next));
        status = next;
        return new Accepted(next);
    }
}
