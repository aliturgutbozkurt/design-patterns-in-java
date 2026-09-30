# Assignment 01 — Document-Approval Workflow

> Module: m08-behavioral-state-structure · Difficulty: ★★☆ · Estimated time: 2–3 h

## Goal

A document goes from draft to review, collects approvals, may be sent back for changes, is published, and can be
archived at any time. Build this as a **State** machine. What the document may do depends on its current status.
Some states carry data (the reviewers who have approved so far). An invalid event is **refused with a reason**
instead of throwing, and it changes nothing, so the history stays auditable.

## What you are given

- `exercises/ex01/DocumentStatus.java` — `DRAFT, IN_REVIEW, CHANGES_REQUESTED, APPROVED, PUBLISHED, ARCHIVED` —
  **do not modify**
- `exercises/ex01/WorkflowEvent.java` — sealed: `Submit(by)`, `Approve(by)`, `RequestChanges(by, comment)`,
  `Revise(by)`, `Publish(by)`, `Archive(by)`. All names and the comment must be non-blank. **Do not modify**
- `exercises/ex01/Outcome.java` — sealed: `Accepted(DocumentStatus status)`, `Refused(String reason)` —
  **do not modify**
- `exercises/ex01/HistoryEntry.java` — record `HistoryEntry(DocumentStatus from, WorkflowEvent event,
  DocumentStatus to)` — **do not modify**
- `exercises/ex01/DocumentWorkflow.java` — `status()`, `Outcome handle(WorkflowEvent)`, `Set<String> approvals()`,
  `List<HistoryEntry> history()` — **do not modify**
- `exercises/ex01/ReviewWorkflow.java` — your code goes here (`TODO(ex01)` markers), constructor
  `ReviewWorkflow(String author, int requiredApprovals)`

## Tasks

1. Constructor: the author is fixed. A blank author or `requiredApprovals < 1` throws `IllegalArgumentException`. A
   new document is `DRAFT`, with no approvals and an empty history.
2. Transitions (every other pair is refused):

   | Status | Event | Result |
   |---|---|---|
   | `DRAFT` | `Submit` by the author | `IN_REVIEW` |
   | `IN_REVIEW` | `Approve` | records the reviewer; `APPROVED` once `requiredApprovals` distinct reviewers approved, otherwise stays `IN_REVIEW` |
   | `IN_REVIEW` | `RequestChanges` | `CHANGES_REQUESTED`, and the approvals are cleared |
   | `CHANGES_REQUESTED` | `Revise` by the author | `IN_REVIEW` |
   | `APPROVED` | `Publish` | `PUBLISHED` |
   | any except `ARCHIVED` | `Archive` | `ARCHIVED` |

3. Refusal reasons, exactly:
   - `"only the author can submit"` / `"only the author can revise"`
   - `"author cannot approve own document"`
   - `"already approved by <name>"`
   - `"document is archived"` for **every** event in `ARCHIVED`
   - `"<EventName> not allowed in <STATUS>"` otherwise, e.g. `"Publish not allowed in IN_REVIEW"` (the event's record
     name).
4. A refused event changes nothing: status, approvals and history stay as they were. Every accepted event appends a
   `HistoryEntry`, including an approval that keeps the status `IN_REVIEW`.
5. `approvals()` and `history()` return read-only views in order. A `null` event throws `NullPointerException`.

## Acceptance criteria

- [ ] `newDocumentIsDraft`
- [ ] `authorSubmitsDraftForReview`
- [ ] `onlyAuthorCanSubmit`
- [ ] `singleApprovalApprovesWhenOneRequired`
- [ ] `staysInReviewUntilEnoughDistinctApprovals`
- [ ] `sameReviewerCannotApproveTwice`
- [ ] `authorCannotApproveOwnDocument`
- [ ] `requestChangesClearsApprovals`
- [ ] `onlyAuthorCanRevise`
- [ ] `approvedDocumentCanBePublished`
- [ ] `publishBeforeApprovalIsRefused`
- [ ] `archiveIsAllowedFromEveryOtherState`
- [ ] `everyEventIsRefusedWhenArchived`
- [ ] `refusedEventChangesNothing`
- [ ] `historyRecordsAcceptedTransitionsInOrder`
- [ ] `historyAndApprovalsAreUnmodifiable`
- [ ] `everyStatusEventPairHasADefinedOutcome`
- [ ] `rejectsInvalidConstructorArgumentsAndNullEvents`

## Run the tests

```bash
./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — one switch, no default</summary>

Switch over the **event** with record patterns and put the status in a `when` guard:
`case Submit(var by) when status == DRAFT -> …`. Handle `ARCHIVED` first, then `Archive`. Finish with a case that
lists the remaining event types by name (`case Submit _, Approve _, … ->`) instead of `default`. Then a new event type
is a compile error, and not a silent refusal. The lesson's section "State — Modern Java 27" shows why.

</details>

<details><summary>Hint 2 — refused means untouched</summary>

Decide first, change afterwards. Check the author and the duplicate reviewer *before* you add anything to the
approvals, and append to the history only in the code path that returns `Accepted`. A small helper
`move(event, next)` that appends the entry, sets the status and returns `new Accepted(next)` keeps this in one place.

</details>

<details><summary>Hint 3 — read-only views</summary>

`List.copyOf(history)` is already read-only. For the approvals you need insertion order: keep a `LinkedHashSet` and
return `Collections.unmodifiableSet(new LinkedHashSet<>(approvals))`.

</details>

## Stretch goals (optional, not graded)

- Rewrite the internal state as sealed records (`Draft`, `InReview(Set<String> approvals)`, …) so that approvals can
  exist only while the document is in review. What happens to `approvals()` in the other states?
- Add a `Withdraw(by)` event (author only, from `IN_REVIEW` back to `DRAFT`) to a copy of the GIVEN types. Count the
  places the compiler makes you touch.
