# Assignment 01 — From Visitor to Data-Oriented Payroll

> Module: m09-functional-data-oriented · Difficulty: ★★☆ · Estimated time: 2–3 h

## Goal

A legacy payroll models employees as a class hierarchy and computes everything with **Visitors**. Replace it with
**data-oriented programming**: convert the legacy objects into sealed records once, at the boundary, then write every
operation as a plain function — an exhaustive `switch` with record patterns and no `default`. That includes an
immutable update (`withRaise`) and an aggregate (`summarize`) that are awkward to write as visitors. The given legacy
visitors stay as the test oracle: your numbers must match theirs.

## What you are given

- `exercises/ex01/legacy/LegacyEmployee.java` and `LegacySalaried`, `LegacyHourly`, `LegacyContractor`,
  `LegacyIntern` — the legacy classes with getters and `accept` — **do not modify**
- `exercises/ex01/legacy/EmployeeVisitor.java`, `MonthlyPayVisitor.java`, `BenefitsVisitor.java` — the working
  legacy rules (the oracle) — **do not modify**
- `exercises/ex01/Employee.java` — `sealed interface Employee permits Salaried, Hourly, Contractor, Intern` with the
  records `Salaried(id, name, annualSalaryCents)`, `Hourly(id, name, hourlyRateCents, hoursThisMonth)`,
  `Contractor(id, name, invoiceCents, vatRegistered)`, `Intern(id, name, stipendCents, universityFunded)`; compact
  constructors reject blank ids/names and negative amounts — **do not modify**
- `exercises/ex01/Kind.java` — `SALARIED`, `HOURLY`, `CONTRACTOR`, `INTERN` — **do not modify**
- `exercises/ex01/PayrollSummary.java` — record `PayrollSummary(long totalCents, Map<Kind, Long> totalByKind,
  List<String> highestPaidIds)` — **do not modify**
- `exercises/ex01/Payroll.java` — the six operations — **do not modify**
- `exercises/ex01/DataOrientedPayroll.java` — your code goes here (`TODO(ex01)` markers)

## Tasks

1. `fromLegacy`: map every legacy subclass to the matching record. Use type patterns on the legacy object and its
   getters; do **not** implement `EmployeeVisitor` and do **not** call `accept` (see Hint 1 for why).
2. `monthlyPayCents` (all amounts in cents, integer arithmetic):
   salaried `annual / 12`; hourly `rate × hours`, but hours above 160 are paid at `rate * 3 / 2` each;
   contractor `invoice`, or `invoice * 120 / 100` when VAT-registered; intern `stipend`, or `0` when
   university-funded.
3. `benefits`: salaried `"health, pension"`; hourly `"health"` when hours ≥ 80, otherwise `"none"`; contractor
   `"none"`; intern `"mentoring"`.
4. `kindOf`: the `Kind` of each record.
5. `withRaise(employee, percent)`: `percent` must be 0..100 (otherwise `IllegalArgumentException`). Return a **new**
   record whose salary, rate, invoice or stipend is multiplied by `(100 + percent) / 100` (integer arithmetic:
   `amount * (100 + percent) / 100`); every other component stays the same, and the original is unchanged.
6. `summarize(employees)`: `totalCents` of all monthly pay; `totalByKind` contains **every** `Kind` (0 for absent
   kinds) and is unmodifiable; `highestPaidIds` lists the ids of all employees with the maximum monthly pay, in input
   order, as an unmodifiable list. An empty list gives total 0, all kinds 0 and no ids.
7. Every method throws `NullPointerException` for a `null` argument.

Write the four operations over `Employee` as `switch` expressions with record patterns (`case Hourly(_, _, var rate,
var hours) -> …`) and **no `default`**, so that a fifth kind of employee would not compile until every operation
handles it.

## Acceptance criteria

- [ ] `convertsEveryLegacyKind`
- [ ] `monthlyPayMatchesLegacyVisitorForAllSamples`
- [ ] `benefitsMatchLegacyVisitorForAllSamples`
- [ ] `hourlyOvertimeIsPaidAtTimeAndAHalf`
- [ ] `vatAddedOnlyForRegisteredContractors`
- [ ] `universityFundedInternCostsNothing`
- [ ] `kindOfClassifiesEveryVariant`
- [ ] `withRaiseReturnsNewRecordAndLeavesOriginalUnchanged`
- [ ] `withRaiseRejectsPercentOutOfRange`
- [ ] `summaryTotalsByKindIncludeAbsentKinds`
- [ ] `summaryListsAllHighestPaidInInputOrder`
- [ ] `summaryOfEmptyListIsZero`
- [ ] `summaryMapIsUnmodifiable`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m09-functional-data-oriented test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — why no visitor?</summary>

A visitor fixes the set of *operations* as methods of one interface and makes each new operation a new class, with
state carried in fields. `withRaise` needs to rebuild a record and `summarize` needs to fold over a list; both are
simple functions over data, but clumsy as visitors. With sealed records, the compiler already knows every case, so an
exhaustive `switch` gives you the same safety as double dispatch without the `accept` plumbing.

</details>

<details><summary>Hint 2 — the boundary switch needs a default</summary>

`LegacyEmployee` is an ordinary abstract class, not a sealed one, so a `switch` over it cannot be exhaustive and needs
`default -> throw new IllegalArgumentException(...)`. That is exactly why you convert once at the boundary: inside the
core, every `switch` is over the sealed `Employee` and has no `default`.

</details>

<details><summary>Hint 3 — a total for every kind</summary>

Start from an `EnumMap<Kind, Long>` with every kind set to `0L`, `merge` the pay of each employee into it, and return
`Map.copyOf(...)` so the result is unmodifiable. Find the maximum first, then filter the list once more to keep the
input order.

</details>

## Stretch goals (optional, not graded)

- Add a fifth kind, `Commissioned(id, name, baseCents, salesCents, percent)`, to a copy of the model and watch the
  compiler point at every `switch` that must change. What would the same change cost in the Visitor design?
- Write `summarize` as one stream pipeline with `Collectors.teeing`.
