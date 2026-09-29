# Spec: m01-oop-solid-uml — OOP, SOLID & UML

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 2 · Task: #11

## Objective

Give students the design vocabulary that every later pattern is justified with. After this module a student can
look at a piece of Java code, name the design problem in it (low cohesion, tight coupling, a broken substitution, a
fat interface, a hard-wired dependency, fragile inheritance), refactor it in small, test-protected steps, and draw the
before/after design as a UML class or sequence diagram in Mermaid. The module closes with a map of the 23 GoF
patterns and where each one is taught in this course.

## Learning outcomes

After this module a student can:

1. **Explain** the four OOP pillars (encapsulation, abstraction, inheritance, polymorphism) and **judge** coupling and
   cohesion in a given class.
2. **Identify** a violation of each SOLID principle in code and **refactor** it to a design that satisfies the
   principle, keeping behaviour unchanged (verified by a characterization test).
3. **Decide** between inheritance and composition, and **implement** a forwarding wrapper that avoids the fragile base
   class problem.
4. **Inject** dependencies through constructors (including `java.time.Clock`) so code can be tested without real
   side effects.
5. **Draw** UML class diagrams (association, composition, inheritance, realization, dependency) and sequence diagrams in
   Mermaid, and **read** the diagrams used in the rest of the course.
6. **Describe** the GoF catalog (creational / structural / behavioral) and locate each pattern in the course.

## Prerequisites

m00: records, sealed interfaces, exhaustive `switch`, lambdas. Java basics: classes, interfaces, collections, exceptions.

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| OOP pillars, coupling & cohesion | classes, `private` fields, abstract classes | records as encapsulated values; interfaces with default methods | records (395) |
| SRP — Single Responsibility (Tek Sorumluluk İlkesi) | split a god class into collaborators | small final classes + records for data | records |
| OCP — Open/Closed (Açık/Kapalı İlkesi) | subclass per variant | rules as lambdas / functional interface; **sidebar:** `sealed` is a deliberate choice to be *closed* (expression problem) | lambdas, sealed (409), switch patterns (441) |
| LSP — Liskov Substitution (Liskov Yerine Geçme İlkesi) | Rectangle/Square, strengthened preconditions | immutable records remove the mutable-setter trap; validation before `super(...)` | records, sealed, flexible constructor bodies (513) |
| ISP — Interface Segregation (Arayüz Ayrımı İlkesi) | one fat interface | small role interfaces; a class implements several | interfaces |
| DIP — Dependency Inversion (Bağımlılığın Tersine Çevrilmesi İlkesi) | `new` inside business logic | constructor injection, composition root in `main`, `java.time.Clock` | lambdas as test doubles |
| Composition over inheritance (kalıtım yerine bileşim) | subclassing a concrete collection | forwarding wrapper; composing records of small strategies | generics, records, sealed |
| UML in Mermaid | class & sequence diagrams | Mermaid `classDiagram` / `sequenceDiagram` in Markdown, rendered to PDF | — |
| GoF catalog overview | 23 patterns, 3 families | which patterns became language features (preview of m09) | — |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m01.examples`. Every demo prints deterministic output.
Principles are taught as **before → after refactors**: `before` and `after` are separate sub-packages and both run.
Money amounts are `BigDecimal`, scale 2, `RoundingMode.HALF_EVEN` (same as m00).

Task **M01-2a** (#12) — SRP, OCP, LSP:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `srp.before.InvoiceService` | `srp.SrpDemo` | invoicing | God class: computes totals, formats text, stores in a map, "emails" (prints) — four reasons to change | characterization: produces the reference invoice text for a fixed input |
| `srp.after` — `Invoice`, `InvoiceLine`, `InvoiceTotals` (records), `InvoiceCalculator`, `InvoiceFormatter`, `InvoiceRepository` + `InMemoryInvoiceRepository`, `InvoiceMailer`, `InvoiceWorkflow` | `srp.SrpDemo` | invoicing | Each class has one reason to change; `InvoiceWorkflow` only coordinates | output identical to `before`; subtotal, 20 % VAT, total; formatter tested in isolation with a hand-built `Invoice`; repository stores and finds by number |
| `srp.gradebook.before.GradeBook` | `srp.GradeBookDemo` | university grades | One class parses CSV lines, computes averages, maps to letter grades and prints the report | characterization: reference report text for a fixed CSV input |
| `srp.gradebook.after` — `StudentScores` (record), `ScoreParser`, `GradingScale`, `GradeReport` | `srp.GradeBookDemo` | university grades | Parsing, grading policy and presentation change for different reasons, so they live apart | output identical to `before`; letter-grade boundaries (e.g. 89.99 → BA, 90.00 → AA); parser rejects malformed lines with the line number |
| `ocp.before.PriceCalculator` | `ocp.OcpDemo` | shop discounts | `if/else` on a customer-type `String`; a new discount means editing the class | regular / student / VIP prices; unknown type is rejected |
| `ocp.after` — `DiscountRule` (functional interface), `DiscountRules` (static factories: `percentOff`, `fixedOff`, `minimumSpend`), `Checkout` | `ocp.OcpDemo` | shop discounts | New rules are added as values (lambda) without touching `Checkout` | rules applied in order; price never below zero; a rule defined only in the test works unchanged; same results as `before` for the three existing types |
| `ocp.sorting.before.CourseSorter` | `ocp.CourseSortDemo` | course catalog | `switch` on a sort-key `String`; every new ordering edits the sorter | sorts by code / credits / title; unknown key rejected |
| `ocp.sorting.Course` (record, shared by both versions); `ocp.sorting.after.CourseCatalog.sorted(Comparator<? super Course>)` | `ocp.CourseSortDemo` | course catalog | The JDK is itself open for extension: new orderings are composed with `Comparator.comparing`/`thenComparing`/`reversed`, not added to the catalog | same orderings as `before`; composed ordering (credits desc, then code); an ordering defined only in the test works unchanged |
| `lsp.before` — `Rectangle` (mutable, setters), `Square extends Rectangle` | `lsp.RectangleDemo` | geometry | Classic violation: `resize(rect, 5, 4)` expects area 20, a `Square` gives 16. `Square` validates its side *before* `super(...)` (JEP 513) | the test **documents** the violation (area is 16, not 20) and the validation |
| `lsp.after` — `sealed interface Shape permits Rectangle, Square` (records) | `lsp.RectangleDemo` | geometry | Immutable values: `withWidth` returns a new `Rectangle`; there is no `setWidth` to break | area/perimeter; `withWidth` does not change the original; all shapes usable through `Shape` |
| `lsp.accounts.before` — `Account`, `FixedDepositAccount extends Account` | `lsp.AccountDemo` | banking | Subclass strengthens a precondition: `withdraw` throws for a subtype, so client code that works for `Account` breaks | test shows the client (`payBill`) fails for the subtype |
| `lsp.accounts.after` — `Account`, `Withdrawable` capability, `CheckingAccount`, `FixedDepositAccount` | `lsp.AccountDemo` | banking | Model the capability instead of throwing; the client asks for `Withdrawable` (leads into ISP) | `payBill` works for every `Withdrawable`; `FixedDepositAccount` is not `Withdrawable`; balance never negative |

Task **M01-2b** (#13) — ISP, DIP, composition:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `isp.before` — `MultiFunctionDevice` (print, scan, fax), `BasicPrinter` | `isp.IspDemo` | office devices | Fat interface forces `BasicPrinter` to throw `UnsupportedOperationException` | test shows `scan` on `BasicPrinter` throws |
| `isp.after` — `Printer`, `DocumentScanner`, `Fax` role interfaces; `BasicPrinter`, `OfficeMachine`; clients `PrintQueue`, `ArchiveService` | `isp.IspDemo` | office devices | Clients depend on the smallest role they need; one class may play several roles | `PrintQueue` accepts any `Printer`; `BasicPrinter` is not a `DocumentScanner`; `OfficeMachine` plays all three roles |
| `isp.store.Product` (record, shared); `isp.store.before` — `ProductStore` (find, list, save, delete, importAll), `ReadOnlyProductStore` | `isp.StoreDemo` | product catalog | A read-only view forced to implement writes throws `UnsupportedOperationException` — same smell as `List.of(...).add` | test shows writes on the read-only view throw |
| `isp.store.after` — `ProductReader`, `ProductWriter`, `InMemoryProducts` (implements both), `CatalogPage` (needs only `ProductReader`) | `isp.StoreDemo` | product catalog | Split by client need; a read-only client cannot even see write methods | `CatalogPage` renders from a lambda/reader-only fake; `InMemoryProducts` round-trips save → find |
| `dip.Customer`, `dip.Channel` (shared); `dip.before` — `NotificationService`, `EmailSender` | `dip.DipDemo` | order notifications | Business logic calls `new EmailSender()` — high-level code depends on a detail | behaviour (captured `System.out`) is correct but only testable by capturing output |
| `dip.after` — `MessageSender` (owned by the high-level package), `EmailSender`, `SmsSender`, `NotificationService` | `dip.DipDemo` | order notifications | Constructor injection; the demo's `main` is the composition root | service sends through a recording test double; chooses channel by customer preference; works with a lambda sender |
| `dip.clock` — `SessionPolicy`, `Session` (record) | `dip.ClockDemo` | login sessions | `java.time.Clock` as the JDK's own inverted dependency: no `Instant.now()` in logic | fixed/offset clock decides expired vs. active at the exact boundary |
| `composition.before.CountingSet extends HashSet` | `composition.CountingSetDemo` | collections | Fragile base class: `addAll` is counted twice because the parent calls the overridden `add` | the test **documents** the over-count (3 elements, count 6) |
| `composition.after` — `ForwardingSet<E> implements Set<E>`, `CountingSet<E>` | `composition.CountingSetDemo` | collections | Forwarding wrapper (preview of Decorator, m04): works with any `Set`, immune to parent internals | exact counts for `add`/`addAll`; wraps `TreeSet` and `HashSet` alike; `Set` contract (equals/hashCode) preserved |
| `composition.vehicles.before` — `sealed abstract class Vehicle`, `PetrolManualCar`, `PetrolAutomaticCar`, `DieselManualCar`, `DieselAutomaticCar` | `composition.VehicleDemo` | vehicles | Class explosion: one subclass per engine × gearbox combination | the demo counts the subclasses needed (engines × gearboxes) |
| `composition.vehicles.after` — `record Vehicle(Engine engine, Gearbox gearbox)`, `sealed interface Engine` (records `Petrol`, `Diesel`, `Electric`), `enum Gearbox` | `composition.VehicleDemo` | vehicles | Compose independent parts; a new engine adds one type, not N | every combination constructible; `describe()` text; range per engine via exhaustive `switch` |

Each after-example gets a Mermaid **class diagram** (before and after) in the lesson; SRP (`InvoiceWorkflow`) and DIP
(`NotificationService`) also get a **sequence diagram**.

## Assignments

### ex01 — Sales report: SRP + OCP refactoring

- **Goal:** break up a god class safely using a characterization test, then add a format without editing the service.
- **Given (do not modify):** `Sale` record (`region`, `product`, `BigDecimal amount`); `SalesSummary` record
  (`SortedMap<String, BigDecimal> totalsByRegion`, `BigDecimal grandTotal`); interfaces `SalesSummarizer`
  (`SalesSummary summarize(List<Sale>)`), `ReportFormat` (`@FunctionalInterface`, `String render(SalesSummary)`),
  `ReportGenerator` (`String generate(List<Sale>)`); `LegacyReport` — the god class that does everything with a
  `String` format flag (`"text"` / `"csv"`). Its output is the reference.
- **Student writes:** `RegionSummarizer implements SalesSummarizer`, `TextReportFormat` and `CsvReportFormat`
  implementing `ReportFormat`, and `ReportService implements ReportGenerator` taking a `SalesSummarizer` and a
  `ReportFormat` in its constructor. No format `if`/`switch` inside `ReportService`.
- **Acceptance criteria (contract tests):** `summarizesTotalsByRegionInAlphabeticalOrder`, `grandTotalIsSumOfRegions`,
  `emptySalesGiveZeroTotal`, `textFormatMatchesLegacyOutput`, `csvFormatMatchesLegacyOutput`,
  `newFormatPlugsInWithoutChangingService` (the test passes a lambda `ReportFormat`), `amountsUseScaleTwo`,
  `rejectsNullArguments`.

### ex02 — Library loans: DIP + LSP

- **Goal:** write business logic that depends only on abstractions, with time injected, so it is fully testable.
- **Given (do not modify):** records `Book` (`isbn`, `title`), `Member` (`id`, `name`), `Loan` (`Book`, `Member`,
  `LocalDate dueDate`); interfaces `LoanStore` (`save`, `remove`, `activeLoansOf(Member)`, `allActive()`),
  `Notifier` (`@FunctionalInterface`, `void send(Member, String)`), `LoanService` (`Loan borrow(Member, Book)`,
  `void giveBack(Loan)`, `int remindOverdue()`).
- **Rules:** loan period 14 days from the injected `Clock`'s date; at most 3 active loans per member
  (`IllegalStateException` on the 4th); `remindOverdue()` sends exactly one message per overdue loan,
  `"Overdue: <title> (due <yyyy-MM-dd>)"`, and returns how many were sent. A loan due *today* is not overdue.
- **Student writes:** `InMemoryLoanStore implements LoanStore` and `LibraryLoanService implements LoanService` with
  constructor `(LoanStore, Notifier, Clock)`. The service never calls `LocalDate.now()` without a clock or `new` on a
  store/notifier.
- **Acceptance criteria (contract tests):** `dueDateIsFourteenDaysAfterClockDate`, `refusesFourthActiveLoan`,
  `givingBackFreesASlot`, `remindsOnlyOverdueLoans`, `loanDueTodayIsNotOverdue`, `reminderMessageFormat`,
  `worksWithAnyNotifier` (recording notifier and a no-op lambda — LSP), `storeContractHolds`
  (save/remove/activeLoansOf on the student's store), `constructorDependsOnlyOnAbstractions` (reflection: every
  constructor parameter type is an interface or `Clock`), `rejectsNullArguments`.

## Quiz topics

Cohesion vs. coupling on a given class; "reason to change" in SRP; why `sealed` + exhaustive `switch` is a deliberate
trade-off against OCP; why Square-extends-Rectangle breaks LSP only when shapes are mutable; preconditions and
postconditions in subtypes; ISP and `UnsupportedOperationException` in JDK collections; who owns the interface in DIP;
fragile base class; Mermaid arrows (inheritance, realization, composition, aggregation, dependency); GoF family of a
given pattern.

## Out of scope

GRASP, UML diagrams other than class and sequence, DI frameworks (Spring, Guice — m11 shows manual DI only), ArchUnit
rules (m11), JPMS modules, mocking libraries (hand-written test doubles only).

## Decisions (owner, 2026-09-29)

1. Every principle gets **two** before/after scenarios (SRP: invoice + gradebook; OCP: discounts + course sorting;
   LSP: shapes + accounts; ISP: office devices + product store; DIP: notifications + clock; composition: counting set +
   vehicles). Principles are held to the same "≥ 2 examples" bar as patterns.
2. Invoice VAT rate is 20 % (current Turkish KDV rate).

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m01-oop-solid-uml verify` green
- [ ] Lesson EN + TR + PDFs, with class diagrams for every example and sequence diagrams for SRP and DIP; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
