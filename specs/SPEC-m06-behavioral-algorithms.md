# Spec: m06-behavioral-algorithms — Behavioral I: Algorithms

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 8 · Task: #36

## Objective

m02–m05 were about *objects*: how to create them and how to put them together. m06 is the first behavioural module
and is about *algorithms*: how to swap one (Strategy), how to fix the skeleton of one and vary its steps (Template
Method), how to turn a request into an object that can be queued, logged and undone (Command), and how to walk a
collection without knowing how it is stored (Iterator). In modern Java many of these patterns shrink to a functional
interface and a lambda. After this module a student can tell when the lambda is enough and when the full class-based
pattern is still worth it. They can also build undo/redo and write their own iterators, spliterators and stream
gatherers.

## Learning outcomes

After this module a student can:

1. **Implement** Strategy as a class hierarchy, as lambdas / method references and as an enum of strategies, and
   **explain** why `Comparator` is the JDK's best-known Strategy.
2. **Implement** Template Method with a `final` skeleton and hook methods, **refactor** it to a higher-order function
   that takes the steps as arguments, and **compare** the two (inheritance vs. composition, shared state, protected
   hooks).
3. **Implement** Command with `execute`/`undo`, undo/redo stacks, macro commands and a job queue, and **model**
   commands as data with a sealed hierarchy of records and an exhaustive `switch`.
4. **Write** a custom `Iterator`/`Iterable` (fail-fast, `NoSuchElementException`, no recursion) and a `Spliterator`
   with the right characteristics, and **use** sequenced collections (`reversed()`, `getFirst()`, `getLast()`).
5. **Use** built-in stream gatherers (`windowSliding`, `windowFixed`, `scan`) and **write** a custom sequential
   `Gatherer` with state, an integrator and a finisher.
6. **Decide** when a pattern should become a lambda and when it should stay a class: it stays a class when it has
   state, needs several methods (such as undo), or needs a name.

## Prerequisites

m05 (Composite: trees that m06 iterates), m01 (OCP, composition over inheritance, DIP), m00 (lambdas, method
references, streams, records, sealed types, pattern matching for `switch`).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Strategy (Strateji) | strategy interface + one class per algorithm, injected into a context | functional interface + lambdas / method refs; enum strategies; `Comparator` combinators (`comparing`, `thenComparing`, `reversed`, `nullsLast`) | lambdas, method refs, records, enums, switch expressions (361) |
| Template Method (Şablon Metot) | abstract class with a `final` template method, abstract steps and optional hooks | higher-order function: the steps passed as `Function`/`Predicate`/`Consumer`; JDK template methods: `AbstractList` (`get` + `size` → everything else), `InputStream` (`read()` → `read(byte[],…)`, `readAllBytes`, `transferTo`) | lambdas, records, sealed (409) |
| Command (Komut) | `Command` interface with `execute()`/`undo()`, invoker, receiver, undo/redo stacks, macro command | commands as data: sealed interface of records + exhaustive `switch` computing the inverse; `Runnable`/`Callable` + `ExecutorService` (virtual threads) as the JDK's Command + invoker | records (395), sealed (409), pattern matching for `switch` (441), record patterns (440), virtual threads (444), `Deque` |
| Iterator (Yineleyici) | `Iterator` with `hasNext`/`next`, `Iterable` for for-each | `Spliterator` (`tryAdvance`, `trySplit`, characteristics) + `StreamSupport`; sequenced collections; **Stream Gatherers** (built-in and custom) | sequenced collections (431), Stream Gatherers (485), unnamed variables `_` (456) |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m06.examples`. Every demo prints deterministic output. Concurrency
demos print results in submission order, never in completion order. `src/main` has no dependencies: the JSON Lines
importer uses a small hand-written parser for flat objects, and the lesson says a real project would use a JSON
library.

Task **M06-2a** (#37): Strategy & Template Method.

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `strategy.shipping.classic`: `ShippingStrategy` (interface), `FlatRate`, `WeightBased`, `FreeOverThreshold`; `Parcel` (record); `ShippingCalculator` (context) | `ShippingDemo` | web shop | Classic GoF Strategy: the context holds a strategy object and can switch it at run time; adding a strategy changes no existing class (OCP) | cost per strategy for fixed parcels; the free-shipping threshold is inclusive (boundary); swapping the strategy changes the next quote; negative weight is rejected by `Parcel` |
| `strategy.shipping.modern`: `ShippingRule` (`@FunctionalInterface`), `ShippingRules` (static factories returning lambdas), `enum ShippingOption implements ShippingRule` | `ShippingDemo` | web shop | The same strategies as lambdas and as enum constants; composing rules (`cheapestOf(rules…)`) with higher-order functions | same quotes as the classic version for every parcel; `cheapestOf` picks the minimum; `ShippingOption.valueOf` round-trips each option |
| `strategy.compression`: `record Codec(String name, UnaryOperator<byte[]> compress, UnaryOperator<byte[]> decompress)`; `Codecs.runLength()`, `Codecs.gzip()` (`java.util.zip`), `Codecs.identity()`; `Archiver` (context) | `CompressionDemo` | file archiving | A strategy with **two** operations that must match: a pair of functions in a record instead of two unrelated lambdas | every codec round-trips (`decompress(compress(x)) == x`); exact run-length output for `"AAAABBBCC"`; run-length makes repetitive input smaller and random-looking input larger; gzip output is smaller for repetitive input |
| `strategy.sorting`: `Student` (record), `StudentOrderings` (named `Comparator` constants) | `ComparatorDemo` | course roster | `Comparator` as the JDK's Strategy: `comparing(...).reversed().thenComparing(...)`, `nullsLast`; `List.sort` is stable | exact order for "by GPA desc, then name"; students without an advisor sort last; sorting by a second key keeps earlier order for equal keys (stability) |
| `templatemethod.importer.classic`: `abstract DataImporter` with `public final ImportReport importData(String input)`: `parse` (abstract), `validate` (hook, default accepts all), `save` (abstract), `onError` (hook); `CsvProductImporter`, `JsonLinesProductImporter`; `Product`, `ImportReport` (records); `InMemoryProductStore` | `ImporterDemo` | product catalogue import | Classic Template Method: the fixed skeleton parse → validate → save, varied by subclasses; hooks with defaults | CSV and JSON Lines inputs with the same data give the same `ImportReport`; invalid rows are skipped and reported with their line numbers; the steps run in order (a recording subclass traces them); `importData` is `final` (reflection check) |
| `templatemethod.importer.functional`: `Importer` (record of `Function<String, List<RawRow>> parser`, `Predicate<Product> validator`, `Consumer<Product> sink`), `Importers.csv()`, `Importers.jsonLines()` | `ImporterDemo` | product catalogue import | The same skeleton as a higher-order function: steps are passed in, not inherited; a new validator is a lambda, not a subclass | same reports as the classic version for the same inputs; a custom validator lambda (price > 0) rejects the expected rows; no subclass needed to change one step |
| `templatemethod.jdk`: `Countdown extends AbstractList<Integer>` (only `get`, `size`), `AlphabetStream extends InputStream` (only `read()`) | `JdkTemplateMethodDemo` | JDK internals | The JDK's own template methods: implement one or two primitive steps and inherit everything else | `Countdown` supports for-each, `contains`, `indexOf`, `subList` and `equals(List.of(...))`; `add` throws `UnsupportedOperationException`; `AlphabetStream.readAllBytes()` and `transferTo` work with only `read()` implemented |

Task **M06-2b** (#38): Command & Iterator.

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `command.spreadsheet.classic`: `Command` (`execute`, `undo`, `label`), `SetCellCommand`, `ClearCellCommand`, `Sheet` (receiver), `UndoManager` (invoker, two `Deque`s) | `SpreadsheetDemo` | spreadsheet editing | Classic Command with undo/redo: each command remembers what it needs to undo itself (the previous cell value) | undo restores the previous value (including "was empty"); redo re-applies it; a new command clears the redo stack; undo/redo on empty history return `false`; `undoLabels()` lists labels newest first |
| `command.spreadsheet.modern`: `sealed interface SheetEdit permits SetCell, ClearCell, Batch` (records); `SheetEditor` (applies an edit and returns its **inverse** via an exhaustive `switch` with record patterns) | `SpreadsheetDemo` | spreadsheet editing | Commands as data: an edit is a value that can be logged, compared and replayed; undo = apply the inverse; `Batch` is a macro command | the inverse of the inverse equals the original edit; undoing a `Batch` restores all its cells in one step; replaying a logged list of edits on an empty sheet rebuilds the same sheet; the `switch` has no `default` |
| `command.remote`: receivers `Light`, `Thermostat`, `GarageDoor`; `RemoteControl` (invoker with numbered slots); `Command` (`execute`/`undo`); `MacroCommand`; `NoCommand` (Null Object) | `RemoteControlDemo` | smart home | The GoF invoker/receiver split: the remote knows only `Command`; simple commands are lambdas, undoable ones are small classes; a "movie night" macro | pressing a slot calls the right receiver; an empty slot does nothing; a macro runs its commands in order and undoes them in reverse order; "undo" undoes the last button pressed; the thermostat undo restores the previous temperature |
| `command.jobs`: `sealed interface Job permits SendEmail, ResizeImage, GenerateReport` (records); `JobQueue` (FIFO); `JobRunner` (`Callable<JobResult>` per job on a virtual-thread executor, `invokeAll`); `JobResult` (record) | `JobQueueDemo` | background jobs | Commands as queued requests: jobs are data (can be logged and retried); `Runnable`/`Callable` + `ExecutorService` are the JDK's Command + invoker | jobs run in FIFO order when drained sequentially; with 100 jobs on virtual threads, results come back in submission order (`invokeAll`); a failing job becomes a `FAILED` result with its message instead of stopping the run; a failed job is retried up to the given limit |
| `iterator.basics`: `IntRange implements Iterable<Integer>` (hand-written `Iterator`), `Playlist` (backed by a `SequencedSet<Song>`) | `IteratorBasicsDemo` | numbers; music playlist | The canonical Iterator; for-each is syntax sugar for `iterator()`; sequenced collections give `reversed()`, `getFirst()`, `getLast()` without writing a reverse iterator | `IntRange` yields `start..end-1` with a step; `next()` past the end throws `NoSuchElementException`; iterators are independent; `Playlist.reversed()` is a live view; adding a song while iterating throws `ConcurrentModificationException` (fail-fast) |
| `iterator.tree`: `record Node<T>(T value, List<Node<T>> children)` (immutable n-ary tree), `TreeTraversals.depthFirst(node)` / `breadthFirst(node)` returning `Iterable<T>` | `TreeIteratorDemo` | company org chart | Several traversals of one structure without exposing its representation; iterative (explicit `Deque`), so deep trees do not overflow the stack | exact pre-order and level-order sequences for the demo org chart; a 100 000-level-deep chain is traversed without `StackOverflowError`; `remove()` is unsupported |
| `iterator.spliterator`: `PagedCustomerSource` (fake paged API, 3 per page, counts page fetches), `PagedSpliterator` (`tryAdvance` fetches the next page lazily), `IntRangeSpliterator` (`SIZED`, `SUBSIZED`, splits in halves) | `SpliteratorDemo` | customer API; number ranges | `Spliterator` as the stream-aware iterator: laziness, characteristics, and `trySplit` for parallel streams | `stream().limit(4)` fetches only 2 pages; characteristics are `ORDERED \| NONNULL` for the paged source and `ORDERED \| SIZED \| SUBSIZED` for the range; the parallel sum equals the sequential sum; `trySplit` of a 1 000-element range gives 500/500 |
| `iterator.gatherers`: `SensorReading` (record); `ReadingAnalytics` using `Gatherers.windowSliding`, `windowFixed`, `scan`; custom `SessionGatherer` (`Gatherer.ofSequential` with state, integrator, finisher: splits click events into sessions when the gap exceeds N seconds) | `GatherersDemo` | IoT sensors; web analytics | Stream Gatherers: new intermediate operations, both built-in and custom; a gatherer iterates *with state*, which `map`/`filter` cannot | 3-point moving averages for a fixed series; `windowFixed(2)` on 5 items gives a last partial window; `scan` gives running totals; `SessionGatherer` splits the demo clicks into the expected sessions; the finisher emits the last session; it stops early on an infinite stream with `limit`; `andThen` composes two gatherers |

**JDK behaviour this spec relies on (verified 2026-09-29 on JDK 27+35 with `javac -Xlint:all -Werror` and the source
launcher):**

- `Stream.of(1,2,3,4,5).gather(Gatherers.windowSliding(3))` → `[[1,2,3],[2,3,4],[3,4,5]]`. When the stream is shorter than
  the window, **one partial window** is emitted: `Stream.of(1,2)` with window 3 → `[[1,2]]`. The moving-average test
  covers this case, and the lesson lists it under pitfalls. `windowFixed(2)` on 5 items → `[[1,2],[3,4],[5]]`. Window
  lists are unmodifiable. `Gatherers.scan(() -> 0, Integer::sum)` on 1..4 → `[1,3,6,10]`.
- A generic `Gatherer.ofSequential(initializer, integrator, finisher)` with a local state class compiles cleanly under
  `-Xlint:all -Werror`. `Gatherer.andThen` composes gatherers.
- `Stream.iterate(0, i -> i + 1).gather(custom).limit(3)` terminates (only 31 elements were pulled). It terminates
  **even if the integrator ignores `downstream.push(...)`'s result**, because the pipeline checks cancellation itself.
  So the "stops early" tests prove termination, not that the integrator is well-behaved. The lesson still teaches
  `return downstream.push(...)`.
- `ArrayList.spliterator()` is `ORDERED | SIZED | SUBSIZED` (not `NONNULL`) and splits 1 000 elements 500/500. The
  default `Iterable.spliterator()` has characteristics `0` and estimated size `Long.MAX_VALUE`. That is why
  `IntRange` and the tree provide their own spliterators. `StreamSupport.stream(sp, false).spliterator()` returns
  the same spliterator (characteristics kept). After a `map`, `NONNULL` is dropped.
- `ArrayList.reversed()` is a live view (later additions show up); `LinkedHashSet` is a `SequencedSet`;
  `LinkedHashMap.sequencedKeySet().reversed()` and `firstEntry()` work as documented. Adding to an `ArrayList` during
  for-each throws `ConcurrentModificationException`, and `List.of(...).iterator().remove()` throws
  `UnsupportedOperationException`.
- An `AbstractList` subclass implementing only `get`/`size` supports `contains`, `indexOf`, `subList`, `equals` and
  `toString`, and `add` throws `UnsupportedOperationException`. An `InputStream` implementing only `read()` supports
  `readAllBytes()`.
- `ExecutorService.invokeAll` on `Executors.newVirtualThreadPerTaskExecutor()` returns futures in **task order**,
  even when the first task finishes last. A failed task's `Future.state()` is `FAILED` and `exceptionNow()` returns
  the cause.
- `GZIPOutputStream` writes mtime `0` and OS byte `255`, so its output is repeatable. Tests still check round-trips and
  "smaller than the input" rather than exact gzip bytes, which depend on the zlib build.
- An iterative in-order iterator over a sealed generic `BinaryTree<T>` (`case Empty<T> _ -> …`, `case Branch<T> b -> …`,
  no `default`) walks a 100 000-deep left spine without `StackOverflowError`.

## Assignments

### ex01 — Text editor with Command-based undo/redo

- **Goal:** implement undo/redo with command objects, including a macro (group) that undoes as one step and a
  bounded history.
- **Given (do not modify):** `sealed interface Edit permits Insert, Delete, Replace` with records
  `Insert(int position, String text)`, `Delete(int position, int length)`,
  `Replace(int position, int length, String text)`; interface `Editor` (`String text()`, `void apply(Edit edit)`,
  `boolean undo()`, `boolean redo()`, `boolean canUndo()`, `boolean canRedo()`,
  `void group(Consumer<Editor> edits)`, `List<Edit> undoHistory()` (the applied edits, most recent first)).
- **Rules:** `apply` checks the position and length against the current text. An invalid edit throws
  `IndexOutOfBoundsException` and changes nothing. Applying a new edit clears the redo history. `undo`/`redo` return
  `false` when there is nothing to undo or redo. A `Delete`/`Replace` must restore the exact removed text on undo.
  All edits made inside `group(...)` undo and redo as **one** step; nested groups join the outer group. If the
  consumer throws, every edit the group already made is rolled back and the exception is rethrown. The history keeps
  at most `maxHistory` steps and drops the oldest. `null` arguments throw `NullPointerException`.
- **Student writes:** `CommandEditor implements Editor` with constructor `CommandEditor(String initialText, int
  maxHistory)`, plus one command class per edit type (`execute`/`undo`). The contract creates editors only through
  `Editor newEditor(String initialText, int maxHistory)`.
- **Acceptance criteria (contract tests):** `appliesInsertDeleteAndReplace`, `undoRestoresPreviousText`,
  `undoRestoresExactDeletedText`, `redoReappliesUndoneEdit`, `newEditClearsRedo`,
  `undoAndRedoOnEmptyHistoryReturnFalse`, `invalidEditThrowsAndLeavesTextUnchanged`, `groupUndoesAsOneStep`,
  `nestedGroupsJoinTheOuterGroup`, `failedGroupIsRolledBackAndRethrown`, `historyIsBoundedToMaxSteps`,
  `undoHistoryListsMostRecentFirst`, `rejectsNullArguments`.

### ex02 — In-order binary tree iterator and a custom Gatherer

- **Goal:** write an iterator that works without recursion, expose it as a correct `Stream`, and write a stateful
  custom `Gatherer`.
- **Given (do not modify):** `sealed interface BinaryTree<T> permits Empty, Branch` with records `Empty<T>()` and
  `Branch<T>(BinaryTree<T> left, T value, BinaryTree<T> right)` and static helpers `empty()`, `leaf(T)`,
  `branch(BinaryTree<T>, T, BinaryTree<T>)`; interface `TreeTools` with
  `<T> Iterator<T> inOrder(BinaryTree<T> tree)`, `<T> Stream<T> stream(BinaryTree<T> tree)` and
  `<T> Gatherer<T, ?, List<T>> runs(BiPredicate<? super T, ? super T> sameRun)`.
- **Rules:** `inOrder` is lazy and iterative: it uses an explicit stack, keeps O(height) memory and does not
  recurse. `next()` past the end throws `NoSuchElementException`, and `remove()` is unsupported. `stream` is
  sequential and its spliterator reports `ORDERED | NONNULL`. `runs` groups **consecutive** elements while
  `sameRun.test(previous, current)` holds (for example, with `(a, b) -> b == a + 1`: `1,2,3,7,8,10` →
  `[[1,2,3],[7,8],[10]]`). It emits each run as an unmodifiable list, emits the last run from the finisher, emits
  nothing for an empty stream, and returns `downstream.push(...)`'s result.
- **Student writes:** `InOrderIterator<T> implements Iterator<T>`, `RunsGatherer` (or a factory method using
  `Gatherer.ofSequential`), and `DefaultTreeTools implements TreeTools`. The contract uses only `TreeTools` and the
  given tree types.
- **Acceptance criteria (contract tests):** `inOrderVisitsBinarySearchTreeInSortedOrder`, `emptyTreeHasNoElements`,
  `nextAfterEndThrowsNoSuchElement`, `removeIsUnsupported`, `independentIteratorsDoNotInterfere`,
  `deepTreeDoesNotOverflowTheStack` (100 000-deep left spine; the test never calls the records'
  `toString`/`equals`/`hashCode`, which would recurse), `streamMatchesIterator`, `streamReportsOrderedAndNonNull`,
  `runsGroupsConsecutiveElements`, `runsEmitsTheLastRun`, `runsOfEmptyStreamIsEmpty`, `runsListsAreUnmodifiable`,
  `runsStopsEarlyOnInfiniteStream`, `treeStreamGatheredIntoRuns` (in-order stream of a BST → ranges of consecutive
  keys), `rejectsNullArguments`.

## Quiz topics

Strategy vs. a plain `if`/`switch` on a type code; when a Strategy should be a lambda and when it should be a class
(state, several methods, a name); why `Comparator` is a Strategy and how `thenComparing` composes strategies;
Template Method vs. Strategy (inheritance vs. composition); why the template method is `final` and what a hook is;
turning a Template Method into a higher-order function; which JDK classes are template methods (`AbstractList`,
`InputStream`, `HttpServlet.service` as a mention); the parts of Command (command, receiver, invoker, client); what
a command must remember to undo itself; why a new command clears the redo stack; commands as data (sealed records)
vs. commands as objects; `Runnable`/`Callable` + `ExecutorService` as Command; external vs. internal iteration;
fail-fast iterators and `ConcurrentModificationException`; what `Spliterator` characteristics tell the stream
framework; what `windowSliding` does with a short stream; `Gatherer` parts (initializer, integrator, finisher,
combiner) and when a gatherer is needed instead of `map`/`filter`/`collect`.

## Out of scope

Observer, Mediator, Chain of Responsibility and Memento (m07; the undo history here stores commands, not
snapshots); State and Visitor (m08); parallel gatherers with a combiner (`Gatherer.of`) beyond a lesson mention
(m09); reactive streams / `Flow` (m07); persistent job queues and retries with back-off (m10/m11 mention only);
JSON libraries (a hand-written flat JSON Lines parser only); `HttpServlet` and other Jakarta EE template methods in
code (lesson mention only); preview features (none are needed in this module).

## Decisions (owner, 2026-09-29)

All questions below were answered **yes**: the recommended defaults apply.

1. **Undo/redo example domain.** Issue #38 lists "text editor undo/redo" as an example, but ex01 (issue #40) is also
   a text editor with undo/redo, so a worked example would give away the assignment. **Recommended default:** the
   examples use a *spreadsheet* (classic `Command` objects + sealed `SheetEdit` records), and the text editor stays
   the assignment.
2. **Template Method importer formats.** `src/main` has no dependencies, so JSON has to be parsed by hand.
   **Recommended default:** CSV plus *JSON Lines of flat objects* (strings and numbers only), parsed by a ~30-line
   hand-written parser. The lesson says a real project would use a JSON library.
3. **Scope of ex02.** ex02 covers an iterator, a spliterator-backed stream **and** a custom gatherer. That is more
   than ex01, but it matches issue #40's "In-order binary tree iterator + custom Gatherer". **Recommended default:**
   keep all three, and give the hint in the brief that `stream` can be built from `inOrder` with
   `Spliterators.spliteratorUnknownSize(..., ORDERED | NONNULL)` + `StreamSupport.stream`, so the main work is the
   iterator and the gatherer.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m06-behavioral-algorithms verify` green
- [ ] Lesson EN + TR + PDFs, with class diagrams for every pattern and a sequence diagram for Command undo/redo; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
