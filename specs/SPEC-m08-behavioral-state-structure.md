# Spec: m08-behavioral-state-structure — Behavioral III: State & Structure

> Status: **APPROVED** (owner, 2026-09-30) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 10 · Task: #46

## Objective

m06 put algorithms into objects and m07 decoupled objects that talk to each other. m08 closes the behavioral patterns
with three patterns whose classic GoF forms are the ones modern Java changes most. **State** makes behaviour depend on
the current state. Java can express a state machine as state objects, as an `enum` with a transition table, or as
`sealed` records that carry only the data valid in each state. **Visitor** adds operations to a fixed hierarchy
through double dispatch. Since Java 21 an exhaustive `switch` over a `sealed` hierarchy with record patterns does the
same job with far less code, and the compiler reports every place a new subtype must be handled. **Interpreter**
represents a small language as a tree and evaluates it. A sealed record AST, a recursive `switch` and a small
recursive-descent parser are the modern form, extending the evaluate + render expression Composite from m05. After
this module a student can model a state machine that rejects invalid transitions and test it pair by pair, choose
between a double-dispatch Visitor and pattern matching by naming the expression-problem trade-off, and write a
tokenizer, parser, evaluator and pretty printer for a tiny language. The capstone spec is due this week, so the rule
engine and the order state machine use the PatternShop domain.

## Learning outcomes

After this module a student can:

1. **Implement** State in three forms: classic GoF state objects behind a context, an `enum` state machine
   (`EnumSet` transition table, exhaustive `switch` expressions), and a `sealed` record state machine whose
   transition function is a pure `(State, Event) → Result` over record patterns. The student can also **decide**
   between throwing on an invalid transition and returning a rejection value.
2. **Test** a state machine systematically. That means every (state, event) pair, terminal states, event-log replay,
   and that a refused event leaves state and history unchanged. The student can also **explain** why a catch-all
   `case` or `default` hides the states a new transition forgets.
3. **Implement** classic Visitor with double dispatch (`<R> R accept(Visitor<R>)`) and **explain** why overloading
   alone cannot do it (static vs. dynamic dispatch). The student can then **refactor** the Visitor into exhaustive
   `switch` expressions over a sealed hierarchy with nested record patterns, `when` guards and unnamed patterns `_`.
4. **Explain** the expression problem (m01's OCP sidebar, m05's Composite quiz). Classic polymorphism makes new types
   cheap. Visitor and sealed + `switch` make new operations cheap. The student can **use** compiler exhaustiveness
   errors as a to-do list and **name** the runtime `MatchException` risk under separate compilation.
5. **Implement** Interpreter as a classic GoF class-per-rule `interpret(Context)` hierarchy and as a sealed record
   AST with a lexer, a recursive-descent parser (precedence, associativity, error positions), an evaluator, a pretty
   printer with minimal parentheses and a simplifier. The student can also **explain** where interpreters appear in
   the JDK (`java.util.regex`, `Predicate` composition) and when a hand-written parser is the wrong tool.

## Prerequisites

m07 (Chain of Responsibility and sealed request records routed by `switch`), m06 (Command as sealed data with an
exhaustive `switch` computing the inverse; Iterator over trees), m05 (Composite as sealed records, in particular
`composite.expression` with `Num`/`Add`/`Mul`/`Neg`, evaluate + render), m01 (OCP and the "sealed is deliberately
closed" sidebar, i.e. the expression problem), m00 (records, sealed types, record patterns, pattern-matching
`switch`, `_`, text blocks).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| State (Durum) | `Context` holding a `State` reference; one class per state implementing every operation; states switch the context's state | `enum` with an `EnumSet` transition table and exhaustive `switch` expressions; `sealed` record states carrying per-state data, with a pure transition function over `record Step(State, Event)` and nested record patterns; rejection as a sealed result vs. an exception; `Thread.State` as the JDK's own state enum | enums, sealed (409), records (395), record patterns (440), pattern matching for `switch` (441), unnamed variables (456) |
| Visitor (Ziyaretçi) | `Element.accept(Visitor)` + `Visitor.visitX(X)` per concrete element (double dispatch); generic return type `Visitor<R>` | exhaustive `switch` over a sealed hierarchy (no `accept`, no visitor interface); nested record patterns, `when` guards, `case A _, B _ ->`; compiler "missing patterns" errors when a subtype is added; `java.nio.file.FileVisitor` / `Files.walkFileTree`; versioned `javax.lang.model` visitors as evidence of Visitor's weak side | sealed (409), records, record patterns (440), `switch` patterns (441), `_` (456), generics |
| Interpreter (Yorumlayıcı) | abstract expression with `interpret(Context)`; terminal and non-terminal expression classes; client builds the tree | sealed record AST; `enum` operators with precedence; sealed `Token` mixing an `enum` and records; recursive-descent parser; evaluator/printer/simplifier as recursive `switch` functions; programs given as text blocks; `Math.*Exact` for defined overflow | sealed, records, record patterns, `switch` patterns, `_`, text blocks (378), switch expressions (361) |
| Expression problem (cross-cutting) | adding a type = one new class; adding an operation = edit every class (or a new Visitor) | sealed + `switch`: adding an operation = one new function; adding a type = compiler errors at every `switch`; the choice is a design decision, not a language limitation | sealed, `switch` patterns |
| Primitive patterns (sidebar only) | — | JEP 532 (`case int i when i > 0`) is **preview** in JDK 27: mentioned in a sidebar with the exact compiler message, no graded code | — |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m08.examples`. Every demo prints deterministic output and runs
with `java <File>.java`. One exception to "all in memory": `visitor.files` writes to a temporary directory (see Open
question 2). Every sealed hierarchy nests its records inside the sealed interface (`permits` inferred) or puts each
record in its own file, because `-Xlint:all -Werror` rejects auxiliary top-level classes used from another file (see
the verified facts). No `switch` over a sealed type or a state `enum` has a `default` branch. The only catch-all
cases are the explicitly discussed `case Step(var s, var e) -> reject(...)` lines in the sealed state machine.

Task **M08-2a** (#47) — State:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `state.vending`: `VendingMachine` (context: `insertCoin(int cents)`, `select(String slot)`, `refund()`, `restock(String slot, int count)`, `stateName()`), `VendingState` (interface with the same operations taking the machine), `IdleState`, `HasCreditState`, `SoldOutState`; record `Product(String name, int priceCents)`; `Dispensed` result record (product + change) | `VendingMachineDemo` | snack vending machine | Minimal canonical GoF State: the context delegates every call to its current state object. States decide the next state, so the context has no `if (state == …)` | coin in `Idle` → `HasCredit`; selecting with enough credit dispenses and returns exact change, then back to `Idle`; not enough credit → message with the missing amount, state unchanged; `select` in `Idle` is refused; `refund` returns the whole credit; selling the last item of the last stocked slot → `SoldOut`; `SoldOut` returns inserted coins; `restock` leaves `SoldOut`; the sequence of `stateName()` values for a scripted session is asserted exactly |
| `state.order.enumfsm`: `enum OrderStatus { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }` with `Set<OrderStatus> next()` (`EnumSet`, built by an exhaustive `switch` expression), `canMoveTo`, `isTerminal`; `Order` (`moveTo(OrderStatus)`, `history()`); `IllegalTransitionException`; `StateDiagram.mermaid()` | `EnumOrderDemo` | PatternShop order lifecycle | State as an `enum`: the whole transition table is visible in one `switch`. Invalid transitions throw. The demo prints the table as a Mermaid `stateDiagram-v2`, which the lesson embeds, so the diagram is generated from the code | a parameterised test over **all 25** (from, to) pairs matches the expected table; invalid move throws with message `"cannot move order O-1 from SHIPPED to PAID"` and leaves status and history unchanged; `DELIVERED` and `CANCELLED` are terminal (`next()` empty); `history()` lists transitions in order and is unmodifiable; `mermaid()` output is asserted exactly |
| `state.order.sealed`: `sealed interface OrderState` with records `Draft(List<Line> lines)`, `Placed(List<Line> lines, long totalCents)`, `Paid(long totalCents, String paymentId)`, `Shipped(String paymentId, String trackingNo)`, `Delivered(String trackingNo)`, `Cancelled(String reason, boolean refunded)`; `sealed interface OrderEvent` with records `AddLine(Line)`, `Place()`, `Pay(String paymentId, long amountCents)`, `Ship(String trackingNo)`, `Deliver()`, `Cancel(String reason)`; `sealed interface Transition permits Moved, Rejected`; `OrderMachine` (`apply(OrderState, OrderEvent)`, `replay(List<OrderEvent>)`) | `SealedOrderDemo` | PatternShop order lifecycle | State as data: each state carries only the fields that exist in that state (a `Draft` has no tracking number; that is a compile-time fact). The transition function is one `switch` over `new Step(state, event)` with nested record patterns and `when` guards. Invalid transitions return `Rejected` instead of throwing. `replay` folds an event log into the current state | the happy path from `Draft` to `Delivered` produces the exact state records; `Pay` with the wrong amount → `Rejected("amount 900 does not match total 1000")`; `Place` on an empty `Draft` is rejected; `Cancel` from `Placed` → `Cancelled(reason, false)` and from `Paid` → `Cancelled(reason, true)`; `Cancel` from `Shipped` is rejected; `Rejected` never changes the state; `replay` of a stored log equals step-by-step application; replay stops at the first rejection and reports its index; lines are copied with `List.copyOf` |
| `state.jdk`: `ThreadStates` (helpers that start a platform or virtual thread parked on a `CountDownLatch` or sleeping, and poll `getState()` with a 5 s deadline) | `ThreadStateDemo` | JDK: `java.lang.Thread.State` | The JDK's own state enum (`NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED`) observed on real platform **and** virtual threads. The printed lines are deterministic because the demo waits for each state instead of sleeping | unstarted thread is `NEW`; a thread parked on a latch reaches `WAITING`; a sleeping thread reaches `TIMED_WAITING`; after `join` it is `TERMINATED` (each for platform and virtual threads, bounded polling, no `Thread.sleep` in the test thread) |

Task **M08-2b** (#48) — Visitor & Interpreter:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `visitor.cart.classic`: `CartItem` (interface: `<R> R accept(CartVisitor<R>)`), records `Book(String title, long priceCents)`, `Electronics(String name, long priceCents, int weightGrams)`, `Grocery(String name, long pricePerKgCents, int grams)`; `CartVisitor<R>` (`visitBook`, `visitElectronics`, `visitGrocery`); `TaxVisitor`, `ShippingWeightVisitor`, `ReceiptVisitor`; `OverloadTrap` | `ClassicCartDemo` | PatternShop cart | Minimal canonical GoF Visitor with a generic result type: each operation is one visitor class, and `accept` performs the second dispatch. `OverloadTrap` shows why this is needed: `describe(CartItem)` / `describe(Book)` overloads are chosen by the **static** type | tax totals exact per category (book 0 %, electronics 20 %, grocery 10 %, rounded half-up per line); shipping weight sums only physical items (books count 400 g); receipt text asserted exactly; a visitor defined only in the test (item count per type) works without touching the items; `OverloadTrap.describe(item)` for a `Book` held in a `CartItem` variable returns the generic text, while `accept` reaches `visitBook` |
| `visitor.cart.modern`: `sealed interface CartItem` with the same three records (no `accept`); `CartOperations` (static `tax`, `shippingWeight`, `receipt`, `describe`) written as exhaustive `switch` expressions | `ModernCartDemo` | PatternShop cart | The same operations without double dispatch: one `switch` per operation, record patterns deconstruct the fields, `case Book _ ->` where a field is not needed. The lesson quotes the "missing patterns" error from adding `GiftCard` | for a shared cart (parameterised) every operation gives the same result as `visitor.cart.classic`; `describe` of a `Book` held as `CartItem` gives the book-specific text (dynamic type is matched); a guard `case Electronics e when e.weightGrams() > 20_000` produces the "bulky" surcharge line |
| `visitor.document`: `sealed interface Block` (`Heading(int level, String text)`, `Paragraph(List<Inline> content)`, `BulletList(List<List<Inline>> items)`, `CodeBlock(String language, String code)`); `sealed interface Inline` (`Text`, `Emphasis(List<Inline>)`, `Code(String)`, `Link(String label, String url)`); `record Document(List<Block> blocks)`; `DocumentRenderers` (`toHtml`, `toPlainText`, `outline`, `links`, `wordCount`) | `DocumentDemo` | rich-text document (Markdown-like) | Realistic "Visitor over a Composite" (deferred from m05). Two nested sealed hierarchies, and operations recurse through both with nested record patterns (`case Emphasis(List<Inline> inner)`, `case Heading(var level, var text) when level > 3`). Five operations and no visitor interface | exact HTML for a sample document, with `<`, `>`, `&` escaped; plain text strips markup; `outline` lists headings with indentation by level and treats levels > 3 as "minor"; `links` returns links in document order, including links inside emphasis inside list items; `wordCount` ignores code blocks; records copy their lists (`List.copyOf`); heading level outside 1–6 rejected |
| `visitor.files`: `DiskUsage` (a `SimpleFileVisitor<Path>`: bytes and file count per extension in a `TreeMap`, `SKIP_SUBTREE` for directories named in an ignore set, failures collected from `visitFileFailed`); `DiskUsageReport` (record) | `DiskUsageDemo` | JDK: `Files.walkFileTree` | The JDK's classic Visitor: `FileVisitor` has one callback per event (`preVisitDirectory`, `visitFile`, `visitFileFailed`, `postVisitDirectory`) and the return value (`FileVisitResult`) steers the walk. The demo creates a fixed tree in a temporary directory, walks it, prints a sorted report and deletes the directory in `finally` | on a `@TempDir` tree: totals per extension and file count exact; files under an ignored `build/` directory are not counted (`SKIP_SUBTREE`); `TERMINATE` after a byte budget stops the walk; the report does not depend on directory iteration order (sorted maps) |
| `interpreter.rules`: `Rule` (interface: `boolean interpret(Customer)`, `String render()`, default `and`, `or`, `negate`), terminal rules `AgeAtLeast(int)`, `CountryIs(String)`, `SpentAtLeast(long cents)`, `HasTag(String)`, non-terminal `AllOf(List<Rule>)`, `AnyOf(List<Rule>)`, `Not(Rule)`; record `Customer(String id, int age, String country, long spentCents, Set<String> tags)`; `Promotion(String code, Rule eligibility)` | `PromotionRulesDemo` | PatternShop promotion eligibility | Minimal canonical GoF Interpreter: one class per grammar rule, each with `interpret(context)`. The client builds the sentence (the rule tree) from combinators, the same design as `java.util.function.Predicate.and/or/negate` | eligibility for a table of customers × promotions; `AllOf`/`AnyOf` short-circuit (a counting test rule is not evaluated after the decision); `Not(Not(r))` is equivalent to `r` on every sample customer; De Morgan equivalence (`not(a and b)` = `not a or not b`) over the table; `render()` gives `age >= 18 and (country = TR or tag vip)` with parentheses only where needed; empty `AllOf` is true and empty `AnyOf` is false |
| `interpreter.calc`: `sealed interface Expr` (`Num(long)`, `Var(String)`, `Neg(Expr)`, `Binary(Op op, Expr left, Expr right)`, `Let(String name, Expr value, Expr body)`); `enum Op { ADD, SUB, MUL, DIV }` (symbol, precedence); `sealed interface Token` (`enum Symbol implements Token`, records `Number`, `Ident`, `Keyword`, `End`, all with a column); `Lexer`, `Parser` (recursive descent), `ParseException` (column), `Evaluator` (`long` with `Math.*Exact`), `EvalException`, `Printer`, `Simplifier`, `Program` (runs a multi-line text block of `let` lines plus a final expression) | `CalculatorDemo` | tiny arithmetic language | Realistic modern Interpreter that extends m05's `composite.expression` (`Num`/`Add`/`Mul`/`Neg`, evaluate + render) with variables, `let … in …` scoping, subtraction/division, **parsing from text** and a new operation (`Simplifier`) added without touching the AST. Text → tokens → AST → value / text | precedence and left associativity (`8 - 3 - 2` = 3, `2 + 3 * 4` = 14, `-2 * 3` = −6); `Parser.parse(Printer.print(e)).equals(e)` for a table of ASTs (structural record equality); the printer keeps only necessary parentheses (`a - (b - c)` kept, `(a - b) - c` printed `a - b - c`); `let x = 2 in let x = x + 1 in x * x` = 9 and the inner binding does not leak; unknown variable → `EvalException("unknown variable: y")`; division by zero → `EvalException`; overflow → `ArithmeticException`; parse errors report the column (`"expected ')' at column 8"`); nesting deeper than 200 → `ParseException` (guards the recursive parser and evaluator against `StackOverflowError`); the simplifier folds constants and applies `x * 1`, `x + 0`, `x * 0` rules; a text-block program evaluates to the expected value |

**JDK behaviour verified on JDK 27+35 (2026-09-30, scratch files outside the repo, `javac -Xlint:all -Werror`):**

- Adding a record to a sealed interface makes every exhaustive `switch` over it fail to compile with
  `error: the switch expression does not cover all possible input values` followed by `missing patterns:` and the
  missing case, e.g. `Var _`. A `switch` **statement** with pattern labels over a sealed type is also required to be
  exhaustive (`the switch statement does not cover …`, `missing patterns: Square _`).
- A switch over a pair record `switch (new Step(state, event))` with nested record patterns is checked for
  exhaustiveness across both components. Omitting one state reports `missing patterns: Step(Cancelled _,Event _)`.
  A final `case Step(var s, var e)` makes it exhaustive, and it also silently accepts future states. That is the
  pitfall discussed in the lesson.
- An **old-style** `switch` statement over an `enum` (`case RED -> …` with no patterns) that omits a constant compiles
  **without any error or lint warning** even under `-Xlint:all -Werror`. Enum state machines therefore use `switch`
  *expressions*, which must be exhaustive.
- Separate compilation: recompiling only the sealed interface with a new record, then passing that record to the old
  compiled `switch`, throws `java.lang.MatchException` at run time.
- An unguarded `case Circle c` followed by `case Circle c when …` is a compile error:
  `this case label is dominated by a preceding case label`. The guarded case must come first.
- Nested record patterns with guards and `_` (`case Mul(Num(var a), _) when a == 0`, `case Neg(Neg(var inner))`),
  multiple patterns without bindings (`case Add _, Mul _ ->`), qualified enum constants mixed with record patterns
  in one `switch` over a sealed interface (`case Token.Op.PLUS ->`, where the enum implements the sealed interface),
  and generic sealed records (`case Result.Ok<T>(var v)`, and the inferred `case Result.Ok(var v)` on
  `Result<? extends Number>`) all compile with no `default`.
- Top-level records declared next to their sealed interface in one file and used from another file fail the build:
  `warning: [auxiliaryclass] auxiliary class Num in Expr.java should not be accessed from outside its own source file`
  with `-Werror`. Records nested in the sealed interface (with `permits` omitted) compile cleanly.
- Primitive patterns are preview in JDK 27: `case int i when i > 0` fails with
  `primitive patterns are a preview feature and are disabled by default`. That is sidebar material only.
- A cold (first-call) recursive `switch` evaluator over a left-deep 10 000-node `Add` chain hit `StackOverflowError`
  with the default stack. After warm-up the same depth succeeded, so the depth limit is not predictable. Example tests
  stay at depth ≤ 1 000, and the parser enforces a nesting limit of 200.
- `Thread.State.values()` is `[NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED]`. Both platform and
  virtual threads report `NEW` before start, `WAITING` while parked on `CountDownLatch.await()`, `TIMED_WAITING` in
  `Thread.sleep`, and `TERMINATED` after `join`. Output was identical across runs.
- `java.nio.file.FileVisitor` declares `preVisitDirectory`, `visitFile`, `visitFileFailed`, `postVisitDirectory`.
  `FileVisitResult` is `CONTINUE, TERMINATE, SKIP_SUBTREE, SKIP_SIBLINGS`.
- `javax.lang.model.util` ships versioned visitors (`SimpleElementVisitor6/7/8/9/14`, `…Preview`,
  `ElementScanner6…14`). `ElementVisitor` has an abstract `visitUnknown` and added `visitModule` and
  `visitRecordComponent` as **default** methods. `com.sun.source.tree.TreeVisitor` has
  `visitDeconstructionPattern`, `visitAnyPattern` and `visitSwitchExpression`. These are the JDK's own evidence that
  adding an element type to a Visitor breaks implementors. The lesson cites them as such.
- A text block ending with a line break keeps a trailing `\n`, and `lines()` drops it. `Program` relies on this.

**Real-world usage the lesson cites (no code, no network):** State: `Thread.State`, `java.util.concurrent.FutureTask`
state constants, `Future.State` (`RUNNING, SUCCESS, FAILED, CANCELLED`, since 19), TCP connection states,
workflow/BPM engines. Visitor: `Files.walkFileTree` / `FileVisitor`, `javax.lang.model` element/type visitors
(annotation processors), `com.sun.source.tree.TreeVisitor` (javac plugins), ASM's `ClassVisitor`. Interpreter:
`java.util.regex.Pattern` (compiles a pattern into a node graph that `Matcher` interprets), `java.text.MessageFormat`,
`Predicate`/`Comparator` combinators, SpEL/expression languages, SQL `WHERE` clauses. The lesson also says when *not*
to hand-write a parser (use a parser generator or an existing library in production). Library names are mentioned
only; no dependency.

## Assignments

### ex01 — Document-approval workflow (State)

- **Goal:** implement a state machine whose states carry data (collected approvals), reject invalid transitions with a
  reason instead of an exception, and keep an auditable history.
- **Given (do not modify):** `enum DocumentStatus { DRAFT, IN_REVIEW, CHANGES_REQUESTED, APPROVED, PUBLISHED,
  ARCHIVED }`; `sealed interface WorkflowEvent` with records `Submit(String by)`, `Approve(String by)`,
  `RequestChanges(String by, String comment)` (comment non-blank), `Revise(String by)`, `Publish(String by)`,
  `Archive(String by)` (all names non-blank); `sealed interface Outcome` with records `Accepted(DocumentStatus
  status)` and `Refused(String reason)`; record `HistoryEntry(DocumentStatus from, WorkflowEvent event,
  DocumentStatus to)`; interface `DocumentWorkflow` (`DocumentStatus status()`, `Outcome handle(WorkflowEvent)`,
  `Set<String> approvals()`, `List<HistoryEntry> history()`).
- **Rules:** the author is fixed at construction. Transitions:
  - `DRAFT` + `Submit` (author only) → `IN_REVIEW`.
  - `IN_REVIEW` + `Approve` records the reviewer and moves to `APPROVED` once `requiredApprovals` distinct reviewers
    have approved (the status stays `IN_REVIEW` until then).
  - `IN_REVIEW` + `RequestChanges` → `CHANGES_REQUESTED` and clears the approvals.
  - `CHANGES_REQUESTED` + `Revise` (author only) → `IN_REVIEW`.
  - `APPROVED` + `Publish` → `PUBLISHED`.
  - `Archive` from any state except `ARCHIVED` → `ARCHIVED`.

  Refusal reasons are exact:
  - `"only the author can submit"` / `"only the author can revise"`.
  - `"author cannot approve own document"`.
  - `"already approved by <name>"`.
  - `"document is archived"` for every event in `ARCHIVED`.
  - `"<EventName> not allowed in <STATUS>"` otherwise, e.g. `"Publish not allowed in IN_REVIEW"`.

  A refused event changes nothing, including history and approvals. `history()` and `approvals()` are unmodifiable
  and list entries in order. `null` events throw `NullPointerException`. `requiredApprovals < 1` or a blank author
  throws `IllegalArgumentException`.
- **Student writes:** `ReviewWorkflow implements DocumentWorkflow` with constructor
  `ReviewWorkflow(String author, int requiredApprovals)`. The internal representation is free: an enum plus fields,
  or sealed state records. The brief recommends a `switch` over `(status, event)` with record patterns and no
  `default`. The tests check behaviour only.
- **Acceptance criteria (contract tests):** `newDocumentIsDraft`, `authorSubmitsDraftForReview`,
  `onlyAuthorCanSubmit`, `singleApprovalApprovesWhenOneRequired`, `staysInReviewUntilEnoughDistinctApprovals`,
  `sameReviewerCannotApproveTwice`, `authorCannotApproveOwnDocument`, `requestChangesClearsApprovals`,
  `onlyAuthorCanRevise`, `approvedDocumentCanBePublished`, `publishBeforeApprovalIsRefused`,
  `archiveIsAllowedFromEveryOtherState`, `everyEventIsRefusedWhenArchived`, `refusedEventChangesNothing`,
  `historyRecordsAcceptedTransitionsInOrder`, `historyAndApprovalsAreUnmodifiable`,
  `everyStatusEventPairHasADefinedOutcome` (parameterised over all reachable statuses × all six event types: the
  result is `Accepted` or `Refused` and nothing throws), `rejectsInvalidConstructorArgumentsAndNullEvents`.

### ex02 — Mini expression language: sealed AST, evaluator, pretty printer (Interpreter)

- **Goal:** write the operations of an interpreter over a given sealed AST with two value types. That covers typed
  evaluation, short-circuiting, a printer whose output parses back to the same tree, and a tree query. The example
  `interpreter.calc` is integer-only and untyped, so this assignment is not a copy of it (see Open question 1).
- **Given (do not modify):**
  - `sealed interface Expr` with records `Num(long value)`, `Bool(boolean value)`, `Var(String name)`,
    `Unary(UnaryOp op, Expr operand)`, `Binary(BinaryOp op, Expr left, Expr right)`,
    `If(Expr condition, Expr then, Expr otherwise)`.
  - `enum UnaryOp { NEG, NOT }` and `enum BinaryOp { OR, AND, EQ, LT, LE, ADD, SUB, MUL, DIV }`, each with
    `symbol()` and `precedence()` (OR lowest, then AND, then comparisons, then ADD/SUB, then MUL/DIV; all binary
    operators left-associative; comparisons are non-associative).
  - `sealed interface Value` with records `NumValue(long)` and `BoolValue(boolean)`, and `EvalException`.
  - `Parser.parse(String)`, a complete recursive-descent parser for
    `if c then a else b`, `not`, unary `-`, `and`, `or`, `=`, `<`, `<=`, `+ - * /`, parentheses, identifiers and
    `true`/`false`. It is given so that the contract can build inputs from text and check round-trips.
  - interface `Language` (`Value evaluate(Expr, Map<String, Value> env)`, `String print(Expr)`,
    `Set<String> freeVariables(Expr)`).
- **Rules:**
  - Arithmetic uses `Math.addExact`/`subtractExact`/`multiplyExact` (overflow → `ArithmeticException`). `DIV`
    truncates toward zero, and division by zero → `EvalException("division by zero")`.
  - Type errors → `EvalException("type error: <OP> expects <NUM|BOOL> but got <NUM|BOOL>")`. `EQ` accepts two
    numbers or two booleans, and mixed types are a type error. A non-boolean `If` condition is a type error.
  - `AND`/`OR` short-circuit and `If` evaluates only the chosen branch, so `false and 1 / 0 = 1` is `false`.
  - An unknown variable → `EvalException("unknown variable: <name>")`.
  - `print` uses single spaces around binary operators, `-x`, `not x`, `if c then a else b`, and parentheses **only**
    where precedence or associativity needs them. `Parser.parse(print(e))` must equal `e` for every tree in the
    contract.
  - `freeVariables` returns every variable name in the tree (the language has no binders).
  - `null` arguments throw `NullPointerException`.
- **Student writes:** `MiniLanguage implements Language`. The brief asks for exhaustive `switch` expressions over the
  sealed types with record patterns and no `default`. The tests check behaviour only.
- **Acceptance criteria (contract tests):** `evaluatesIntegerArithmetic`, `respectsPrecedenceAndLeftAssociativity`,
  `integerDivisionTruncatesTowardZero`, `divisionByZeroIsAnEvalError`, `overflowThrowsArithmeticException`,
  `comparesNumbersAndBooleans`, `mixedTypeEqualityIsATypeError`, `arithmeticOnBooleansIsATypeError`,
  `ifRequiresABooleanCondition`, `andOrShortCircuit`, `ifEvaluatesOnlyTheChosenBranch`,
  `looksUpVariablesInTheEnvironment`, `unknownVariableIsAnEvalError`, `printsWithMinimalParentheses`,
  `printsNestedIfAndUnaryOperators`, `printedTextParsesBackToTheSameTree` (parameterised over ≥ 15 trees),
  `collectsFreeVariables`, `rejectsNullArguments`.

## Quiz topics

State:
- State vs. Strategy (same structure, different intent: who changes the object, the client or the object itself).
- Where transition logic lives: in the state objects or in a central table.
- `enum` vs. sealed-record state machines (per-state data, compile-time impossibility of invalid fields).
- Throwing vs. returning a rejection.
- Why a catch-all `case` or `default` weakens exhaustiveness, and why old-style enum `switch` statements are not
  checked.
- Testing every (state, event) pair, and replay of an event log.

Visitor:
- Double dispatch and why overloading is resolved statically.
- `accept`/`visit` roles.
- Generic `Visitor<R>` vs. `void` visitors with accumulated state.
- Visitor vs. sealed + `switch`: what each makes easy (the expression problem, linked back to m01 and m05).
- `MatchException` under separate compilation.
- `when` guards and dominance order.
- Why `javax.lang.model` has versioned visitors and `visitUnknown`.
- `FileVisitResult` as a way to steer the traversal.

Interpreter:
- Terminal vs. non-terminal expressions.
- Grammar → class hierarchy vs. grammar → sealed records.
- Precedence and associativity in a recursive-descent parser.
- Why `parse(print(e)) == e` is a strong test.
- Short-circuit evaluation.
- Recursion depth limits.
- Interpreter vs. Composite (m05) vs. Command (m06).
- When a real parser generator or an existing library is the better choice.
- Regex as an interpreter.

Sidebar question: what JEP 532 primitive patterns would add, and why the course does not use them yet.

## Out of scope

- Primitive types in patterns (JEP 532, preview): sidebar only, no code.
- Parser generators (ANTLR, JavaCC) and parser-combinator libraries: mention only, since they would be a new
  dependency.
- Bytecode compilation or a JIT for the interpreter.
- Type inference beyond ex02's runtime type checks.
- Hierarchical/statechart features (nested states, history states, parallel regions) and state-machine frameworks
  (Spring Statemachine): mention only.
- Persisting state machines or event logs (event sourcing in depth → m11).
- Acyclic and reflective Visitor variants: mention only.
- Annotation processors or javac plugins in code: the `javax.lang.model` and `com.sun.source` visitors are cited only.
- Data-oriented programming as a whole design style (m09 builds on this module's sealed ASTs).
- Concurrency in state machines (m10).

## Decisions (owner, 2026-09-30)

All questions below were answered **yes**: the recommended defaults apply (including any build or dependency change they describe).

1. **ex02 vs. the calculator example.** Issue #48 asks for an "arithmetic language with record AST, evaluator and
   pretty printer" as an *example*, and issue #50 asks for a "mini expression language: sealed AST, evaluator, pretty
   printer" as an *assignment*. A worked example of the same language would give the assignment away.
   **Recommended default:** keep `interpreter.calc` as the integer-only language with `let`, a lexer/parser and a
   simplifier. ex02 becomes a *typed* language (numbers + booleans, comparisons, `and`/`or`/`not`, `if … then … else`)
   with a given parser. The student writes the evaluator (type errors, short-circuit), the printer (round-trip) and
   `freeVariables`.
2. **`visitor.files` uses the real file system.** Every other example in the course so far stays in memory, but
   `Files.walkFileTree` is the JDK's clearest Visitor and needs real files. **Recommended default:** keep it. The demo
   creates a fixed tree under a fresh `Files.createTempDirectory` and deletes it in `finally`. Tests use JUnit
   `@TempDir`. The printed report uses paths relative to the temp root and sorted maps, so the output is
   deterministic on every OS. Alternative: cite `Files.walkFileTree` in the lesson only and drop the example.
   Visitor would still have three examples.
3. **Capstone bridge.** The capstone spec (C1, #66) is due this week. **Recommended default:** the order examples
   (`state.order.enumfsm`, `state.order.sealed`) and `interpreter.rules` use PatternShop vocabulary (order statuses,
   promotions, customers). The capstone brief can then list "order lifecycle as a sealed state machine" and
   "promotion rules as an Interpreter" as candidate patterns without m08 prescribing the capstone design.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m08-behavioral-state-structure verify` green
- [ ] No `default` branch in any `switch` over a sealed type or a state `enum` in `examples`/`solutions` (reviewed; catch-all `case Step(var s, var e)` lines only where the spec lists them)
- [ ] Lesson EN + TR + PDFs, with a state diagram per state machine (the enum one generated by `StateDiagram.mermaid()`), class diagrams for classic Visitor and Interpreter, the expression-problem comparison table, and the JEP 532 preview sidebar; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
