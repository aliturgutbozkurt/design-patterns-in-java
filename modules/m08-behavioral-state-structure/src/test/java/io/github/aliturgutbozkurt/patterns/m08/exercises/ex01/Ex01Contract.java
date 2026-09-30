package io.github.aliturgutbozkurt.patterns.m08.exercises.ex01;

import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.APPROVED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.ARCHIVED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.CHANGES_REQUESTED;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.DRAFT;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.IN_REVIEW;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.DocumentStatus.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Outcome.Accepted;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.Outcome.Refused;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Approve;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Archive;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Publish;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.RequestChanges;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Revise;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex01.WorkflowEvent.Submit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/** Assignment 01 — document-approval workflow. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    private static final String AUTHOR = "ada";

    /** A new workflow for a document written by {@code author}. */
    protected abstract DocumentWorkflow newWorkflow(String author, int requiredApprovals);

    private static final List<WorkflowEvent> ONE_OF_EACH = List.of(new Submit(AUTHOR), new Approve("bob"),
            new RequestChanges("bob", "typo"), new Revise(AUTHOR), new Publish("eve"), new Archive("eve"));

    /** A workflow (one approval required) driven into {@code status} by accepted events only. */
    private DocumentWorkflow in(DocumentStatus status) {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 1);
        List<WorkflowEvent> path = switch (status) {
            case DRAFT -> List.of();
            case IN_REVIEW -> List.of(new Submit(AUTHOR));
            case CHANGES_REQUESTED -> List.of(new Submit(AUTHOR), new RequestChanges("bob", "typo"));
            case APPROVED -> List.of(new Submit(AUTHOR), new Approve("bob"));
            case PUBLISHED -> List.of(new Submit(AUTHOR), new Approve("bob"), new Publish("eve"));
            case ARCHIVED -> List.of(new Archive("eve"));
        };
        path.forEach(event -> assertThat(workflow.handle(event)).isInstanceOf(Accepted.class));
        assertThat(workflow.status()).isEqualTo(status);
        return workflow;
    }

    static Stream<Arguments> everyStatusAndEvent() {
        return Arrays.stream(DocumentStatus.values())
                .flatMap(status -> ONE_OF_EACH.stream().map(event -> Arguments.of(status, event)));
    }

    @Test
    void newDocumentIsDraft() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        assertThat(workflow.status()).isEqualTo(DRAFT);
        assertThat(workflow.approvals()).isEmpty();
        assertThat(workflow.history()).isEmpty();
    }

    @Test
    void authorSubmitsDraftForReview() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        assertThat(workflow.handle(new Submit(AUTHOR))).isEqualTo(new Accepted(IN_REVIEW));
        assertThat(workflow.status()).isEqualTo(IN_REVIEW);
    }

    @Test
    void onlyAuthorCanSubmit() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        assertThat(workflow.handle(new Submit("bob"))).isEqualTo(new Refused("only the author can submit"));
        assertThat(workflow.status()).isEqualTo(DRAFT);
    }

    @Test
    void singleApprovalApprovesWhenOneRequired() {
        DocumentWorkflow workflow = in(IN_REVIEW);
        assertThat(workflow.handle(new Approve("bob"))).isEqualTo(new Accepted(APPROVED));
        assertThat(workflow.approvals()).containsExactly("bob");
    }

    @Test
    void staysInReviewUntilEnoughDistinctApprovals() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 3);
        workflow.handle(new Submit(AUTHOR));
        assertThat(workflow.handle(new Approve("bob"))).isEqualTo(new Accepted(IN_REVIEW));
        assertThat(workflow.handle(new Approve("cy"))).isEqualTo(new Accepted(IN_REVIEW));
        assertThat(workflow.approvals()).containsExactly("bob", "cy");
        assertThat(workflow.handle(new Approve("dee"))).isEqualTo(new Accepted(APPROVED));
        assertThat(workflow.approvals()).containsExactly("bob", "cy", "dee");
    }

    @Test
    void sameReviewerCannotApproveTwice() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        workflow.handle(new Submit(AUTHOR));
        workflow.handle(new Approve("bob"));
        assertThat(workflow.handle(new Approve("bob"))).isEqualTo(new Refused("already approved by bob"));
        assertThat(workflow.status()).isEqualTo(IN_REVIEW);
        assertThat(workflow.approvals()).containsExactly("bob");
    }

    @Test
    void authorCannotApproveOwnDocument() {
        DocumentWorkflow workflow = in(IN_REVIEW);
        assertThat(workflow.handle(new Approve(AUTHOR))).isEqualTo(new Refused("author cannot approve own document"));
        assertThat(workflow.status()).isEqualTo(IN_REVIEW);
        assertThat(workflow.approvals()).isEmpty();
    }

    @Test
    void requestChangesClearsApprovals() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        workflow.handle(new Submit(AUTHOR));
        workflow.handle(new Approve("bob"));
        assertThat(workflow.handle(new RequestChanges("cy", "needs a diagram")))
                .isEqualTo(new Accepted(CHANGES_REQUESTED));
        assertThat(workflow.approvals()).isEmpty();
        workflow.handle(new Revise(AUTHOR));
        assertThat(workflow.handle(new Approve("bob"))).isEqualTo(new Accepted(IN_REVIEW));
    }

    @Test
    void onlyAuthorCanRevise() {
        DocumentWorkflow workflow = in(CHANGES_REQUESTED);
        assertThat(workflow.handle(new Revise("bob"))).isEqualTo(new Refused("only the author can revise"));
        assertThat(workflow.status()).isEqualTo(CHANGES_REQUESTED);
        assertThat(workflow.handle(new Revise(AUTHOR))).isEqualTo(new Accepted(IN_REVIEW));
    }

    @Test
    void approvedDocumentCanBePublished() {
        DocumentWorkflow workflow = in(APPROVED);
        assertThat(workflow.handle(new Publish("eve"))).isEqualTo(new Accepted(PUBLISHED));
        assertThat(workflow.status()).isEqualTo(PUBLISHED);
    }

    @Test
    void publishBeforeApprovalIsRefused() {
        assertThat(in(IN_REVIEW).handle(new Publish("eve"))).isEqualTo(new Refused("Publish not allowed in IN_REVIEW"));
        assertThat(in(DRAFT).handle(new Publish("eve"))).isEqualTo(new Refused("Publish not allowed in DRAFT"));
        assertThat(in(PUBLISHED).handle(new Publish("eve")))
                .isEqualTo(new Refused("Publish not allowed in PUBLISHED"));
    }

    @ParameterizedTest
    @EnumSource(value = DocumentStatus.class, names = "ARCHIVED", mode = EnumSource.Mode.EXCLUDE)
    void archiveIsAllowedFromEveryOtherState(DocumentStatus status) {
        DocumentWorkflow workflow = in(status);
        assertThat(workflow.handle(new Archive("eve"))).isEqualTo(new Accepted(ARCHIVED));
        assertThat(workflow.status()).isEqualTo(ARCHIVED);
    }

    @Test
    void everyEventIsRefusedWhenArchived() {
        DocumentWorkflow workflow = in(ARCHIVED);
        for (WorkflowEvent event : ONE_OF_EACH) {
            assertThat(workflow.handle(event)).isEqualTo(new Refused("document is archived"));
        }
        assertThat(workflow.status()).isEqualTo(ARCHIVED);
    }

    @Test
    void refusedEventChangesNothing() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 2);
        workflow.handle(new Submit(AUTHOR));
        workflow.handle(new Approve("bob"));
        var history = workflow.history();
        var approvals = workflow.approvals();
        for (WorkflowEvent refused : List.of(new Approve("bob"), new Approve(AUTHOR), new Submit(AUTHOR),
                new Revise(AUTHOR), new Publish("eve"))) {
            assertThat(workflow.handle(refused)).isInstanceOf(Refused.class);
        }
        assertThat(workflow.status()).isEqualTo(IN_REVIEW);
        assertThat(workflow.history()).isEqualTo(history);
        assertThat(workflow.approvals()).isEqualTo(approvals);
    }

    @Test
    void historyRecordsAcceptedTransitionsInOrder() {
        DocumentWorkflow workflow = newWorkflow(AUTHOR, 1);
        var submit = new Submit(AUTHOR);
        var changes = new RequestChanges("bob", "typo");
        var revise = new Revise(AUTHOR);
        var approve = new Approve("bob");
        var publish = new Publish("eve");
        for (WorkflowEvent event : List.of(submit, changes, new Revise("bob"), revise, approve, publish)) {
            workflow.handle(event);
        }
        assertThat(workflow.history()).containsExactly(
                new HistoryEntry(DRAFT, submit, IN_REVIEW),
                new HistoryEntry(IN_REVIEW, changes, CHANGES_REQUESTED),
                new HistoryEntry(CHANGES_REQUESTED, revise, IN_REVIEW),
                new HistoryEntry(IN_REVIEW, approve, APPROVED),
                new HistoryEntry(APPROVED, publish, PUBLISHED));
    }

    @Test
    void historyAndApprovalsAreUnmodifiable() {
        DocumentWorkflow workflow = in(APPROVED);
        assertThatThrownBy(() -> workflow.history().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> workflow.approvals().add("mallory"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(workflow.history()).hasSize(2);
        assertThat(workflow.approvals()).containsExactly("bob");
    }

    @ParameterizedTest(name = "{0} + {1}")
    @MethodSource("everyStatusAndEvent")
    void everyStatusEventPairHasADefinedOutcome(DocumentStatus status, WorkflowEvent event) {
        DocumentWorkflow workflow = in(status);
        Outcome outcome = workflow.handle(event);
        switch (outcome) {
            case Accepted(var next) -> assertThat(workflow.status()).isEqualTo(next);
            case Refused(var reason) -> {
                assertThat(reason).isNotBlank();
                assertThat(workflow.status()).isEqualTo(status);
            }
        }
    }

    @Test
    void rejectsInvalidConstructorArgumentsAndNullEvents() {
        assertThatIllegalArgumentException().isThrownBy(() -> newWorkflow(AUTHOR, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> newWorkflow(" ", 1));
        assertThatNullPointerException().isThrownBy(() -> newWorkflow(AUTHOR, 1).handle(null));
    }
}
