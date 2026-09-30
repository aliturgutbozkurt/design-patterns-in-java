# Assignment 02 — Expense Approval Chain

> Module: m07-behavioral-communication · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

Employees submit expense claims; who may approve one depends on its amount and category. Instead of one method full
of nested `if`s, build a **Chain of Responsibility**: each approver either decides or passes the expense on, the chain
stops at the first decision, and the result records every approver the expense passed through.

## What you are given

- `exercises/ex02/Category.java` — `TRAVEL`, `MEALS`, `EQUIPMENT`, `TRAINING` — **do not modify**
- `exercises/ex02/Expense.java` — record `Expense(String id, String employee, Category category, long amountCents)` —
  **do not modify**
- `exercises/ex02/Decision.java` — sealed: `Approved(String approver)`, `Rejected(String approver, String reason)` —
  **do not modify**
- `exercises/ex02/ApprovalResult.java` — record `ApprovalResult(String expenseId, Decision decision, List<String> trail)`
  — **do not modify**
- `exercises/ex02/Approver.java` — `String name()`, `Optional<Decision> review(Expense)` (empty = pass it on) —
  **do not modify**
- `exercises/ex02/ApprovalChain.java` — `ApprovalResult submit(Expense)` — **do not modify**
- `exercises/ex02/PolicyCheck.java`, `TeamLead.java`, `Manager.java`, `Director.java`, `ApprovalChains.java` — your
  code goes here (`TODO(ex02)` markers)

## Tasks

1. `PolicyCheck` (`"policy check"`): rejects non-positive amounts (`"amount must be positive"`) and `MEALS` above
   100.00 (`"meals above 100.00"`); otherwise passes the expense on.
2. `TeamLead` (`"team lead"`) approves up to 500.00 except `EQUIPMENT`; `Manager` (`"manager"`) approves up to
   5 000.00; `Director` (`"director"`) approves up to 20 000.00. All limits are **inclusive**.
3. `ApprovalChains.of(List<Approver>)`: asks the approvers in order, stops at the first decision, and records the name
   of every approver that reviewed the expense in `trail`. If nobody decides, the decision is
   `Rejected("chain", "no approver could decide")`. A `null` expense throws `NullPointerException`.
4. `ApprovalChains.standard()`: policy check → team lead → manager → director.

## Acceptance criteria

- [ ] `smallTravelExpenseApprovedByTeamLead`
- [ ] `equipmentSkipsTeamLead`
- [ ] `mediumExpenseEscalatesToManager`
- [ ] `largeExpenseEscalatesToDirector`
- [ ] `tooLargeExpenseRejectedAtEndOfChain`
- [ ] `policyCheckRejectsExpensiveMealsBeforeAnyApprover`
- [ ] `policyCheckRejectsNonPositiveAmounts`
- [ ] `boundaryAmountsBelongToTheLowerApprover`
- [ ] `trailListsReviewersInOrder`
- [ ] `chainStopsAtFirstDecision`
- [ ] `customChainUsesOnlyGivenApprovers`
- [ ] `emptyChainRejects`
- [ ] `trailIsImmutable`
- [ ] `rejectsNullExpense`

## Run the tests

```bash
./mvnw -pl modules/m07-behavioral-communication test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — amounts in cents</summary>

500.00 is `500_00` and 20 000.00 is `20_000_00` cents. Using `long` cents avoids every floating-point surprise at the
boundaries.

</details>

<details><summary>Hint 2 — the chain is a loop</summary>

The approvers do not need a `next` field: `of(...)` can keep the list and loop over it, adding each name to the trail
before calling `review`, and returning as soon as a review is present. Compare with the linked `SupportHandler` in the
lesson — both are Chain of Responsibility.

</details>

## Stretch goals (optional, not graded)

- Write `ApprovalChains.of` with streams (`map`, `flatMap(Optional::stream)`, `findFirst`). How do you still build the
  trail without side effects?
- Add a `FraudCheck` that rejects two claims with the same id — which state does it need, and where in the chain should
  it go?
