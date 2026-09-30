# Assignment 02 — Library Loans: DIP + LSP

> Module: m01-oop-solid-uml · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

Write the lending rules of a small library so that they depend **only on abstractions**: where loans are stored,
how members are notified and even *what day it is* all arrive through the constructor (Dependency Inversion). Because
of that, the tests can run your rules with fakes — a recording notifier, a silent lambda, a clock they move by hand —
and every implementation of an interface must work in place of any other (Liskov Substitution).

## What you are given

- `exercises/ex02/Book.java`, `Member.java`, `Loan.java` — records — **do not modify**
- `exercises/ex02/LoanStore.java`, `Notifier.java`, `LoanService.java` — interfaces — **do not modify**
- `exercises/ex02/InMemoryLoanStore.java`, `LibraryLoanService.java` — your code goes here (`TODO(ex02)` markers)

## Tasks

1. `InMemoryLoanStore implements LoanStore`: keep loans in the order they were saved; `remove` of an unknown loan does
   nothing; `activeLoansOf` returns only that member's loans.
2. `LibraryLoanService(LoanStore, Notifier, Clock)`, with these rules:

   | Rule | Detail |
   |---|---|
   | Loan period | due date = today (from the injected `Clock`) + 14 days |
   | Limit | at most 3 active loans per member; the 4th throws `IllegalStateException` |
   | Giving back | frees the slot |
   | Reminders | `remindOverdue()` sends **one** message per overdue loan and returns how many it sent |
   | Overdue | due date **before** today; a loan due today is not overdue yet |
   | Message | `"Overdue: <title> (due <yyyy-MM-dd>)"` |

3. The service must not create its own collaborators: no `new InMemoryLoanStore()`, no concrete notifier, no
   `LocalDate.now()` without the clock (use `LocalDate.now(clock)`).
4. Reject `null` constructor arguments, members and books with `NullPointerException`.

## Acceptance criteria

- [ ] `dueDateIsFourteenDaysAfterClockDate`
- [ ] `refusesFourthActiveLoan`
- [ ] `givingBackFreesASlot`
- [ ] `remindsOnlyOverdueLoans`
- [ ] `loanDueTodayIsNotOverdue`
- [ ] `reminderMessageFormat`
- [ ] `worksWithAnyNotifier` — a recording notifier and a no-op lambda (LSP)
- [ ] `storeContractHolds`
- [ ] `constructorDependsOnlyOnAbstractions` — every constructor parameter is an interface or `Clock`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m01-oop-solid-uml test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — today's date</summary>

`LocalDate.now(clock)` asks the injected clock. In production `main` would pass `Clock.systemDefaultZone()`; the tests
pass a clock they can move forward.

</details>

<details><summary>Hint 2 — "before today"</summary>

`dueDate.isBefore(today)` is `false` when both dates are equal — exactly the rule you need.

</details>

## Stretch goals (optional, not graded)

- Write a `main` that acts as the composition root: build a store, a notifier that prints, the system clock, and the
  service.
- Draw a Mermaid sequence diagram of `remindOverdue()`.
