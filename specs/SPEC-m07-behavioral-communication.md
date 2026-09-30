# Spec: m07-behavioral-communication — Behavioral II: Communication

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 9 · Task: #41

## Objective

m06 put *algorithms* into objects. m07 is about how objects *talk to each other* without knowing each other:
announcing changes to whoever is interested (Observer), routing all conversation through one coordinator
(Mediator), passing a request along until someone takes it (Chain of Responsibility), and capturing state so it can
be restored later without breaking encapsulation (Memento). After this module a student can decouple senders from
receivers, choose between push listeners and a back-pressured `java.util.concurrent.Flow` stream, compose handler
chains as functions, and build undo/save features from immutable record snapshots. The capstone starts this week, so
the examples deliberately prepare its domain events and request pipelines.

## Learning outcomes

After this module a student can:

1. **Implement** Observer in its classic form (subject + observer interface) and its modern form (functional
   listeners returning an unsubscribe handle), and **explain** the lapsed-listener leak, notification order and
   re-entrancy pitfalls.
2. **Use** `java.util.concurrent.Flow` with `SubmissionPublisher`: **explain** demand (`request(n)`), back-pressure,
   buffering and dropping, and the `onSubscribe → onNext* → (onComplete | onError)` protocol — and **test** it
   deterministically.
3. **Implement** a Mediator that removes many-to-many references between colleagues, and **compare** it with an
   event bus (Observer) and with a Facade (m05).
4. **Implement** Chain of Responsibility both as linked handler objects and as composed functions (middleware),
   and **decide** between "first handler wins", "every handler runs" and "fail fast vs. collect all errors".
5. **Implement** Memento with an opaque classic memento and with record snapshots kept in a sequenced-collection
   history, and **compare** Memento-based undo with Command-based undo (m06).

## Prerequisites

m06 (Strategy as lambdas, Command with undo/redo), m05 (Facade, for the Mediator comparison), m03 (immutable
records with compact constructors), m00 (records, sealed types, `switch` pattern matching, lambdas).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Observer (Gözlemci) | `Subject` with `attach`/`detach`/`notifyObservers`, `Observer.update(...)`; `java.util.Observable` (deprecated since 9) | functional listeners (`Consumer<E>`) returning a `Subscription` handle; typed event bus over a `sealed` event hierarchy; `java.beans.PropertyChangeSupport`; `Flow` + `SubmissionPublisher` with back-pressure | lambdas, records (395), sealed (409), `switch` patterns (441), `Flow` (JDK 9) |
| Mediator (Arabulucu) | `Mediator` interface + `Colleague`s that call `mediator.notify(this, event)` | colleagues send `sealed` request records; the mediator routes them with an exhaustive `switch`; `javax.swing.ButtonGroup` as a JDK mediator | sealed, records, record patterns (440) |
| Chain of Responsibility (Sorumluluk Zinciri) | abstract `Handler` with `setNext` and `handle` | handlers as functions composed with a default `orElse`; middleware as `Handler → Handler` (like `com.sun.net.httpserver.Filter` / servlet `Filter`); validation chains (fail-fast vs. collect-all) | lambdas, default methods, `Optional.or`, sealed results |
| Memento (Hatıra) | `Originator.save()` returns an opaque `Memento`; `Caretaker` keeps a stack | immutable `record` snapshots (`List.copyOf` in the compact constructor); history in a `Deque` / `LinkedHashMap` via sequenced-collection methods (`addLast`, `removeLast`, `getLast`, `reversed`, `putLast`, `lastEntry`, `pollFirstEntry`) | records, sequenced collections (431) |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m07.examples`. Every demo prints deterministic output and runs
with `java <File>.java`. Asynchronous code is made deterministic as described in
[Deterministic `Flow`](#deterministic-flow-verified-on-jdk-27) below.

Task **M07-2a** (#42) — Observer & Mediator:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `observer.classic` — `StockTicker` (subject: `attach`, `detach`, `setPrice`), `StockObserver` (interface), `PriceDisplay`, `PriceAlert(threshold)` | `StockTickerDemo` | stock ticker | Classic GoF Observer: the subject knows only the `StockObserver` interface; observers are pushed the symbol and new price | observers notified in attach order; detached observer receives nothing; no notification when the price is unchanged; alert fires only when the price *crosses* the threshold (not on every update above it); an observer that detaches itself during notification causes no `ConcurrentModificationException` |
| `observer.modern` — `Ticker` with `Subscription onPriceChange(Consumer<? super PriceChange>)`; record `PriceChange(String symbol, BigDecimal oldPrice, BigDecimal newPrice)`; `Subscription` (`close()` without checked exception) | `ModernTickerDemo` | stock ticker | Functional listeners and an unsubscribe handle usable in try-with-resources — the fix for the lapsed-listener leak; one failing listener must not silence the others | `close()` stops delivery and is idempotent; try-with-resources unsubscribes; listener exception is passed to the injected error handler (`Consumer<RuntimeException>`) and the remaining listeners still get the event; listeners added during delivery receive only later events |
| `observer.beans` — `Thermostat` bean using `PropertyChangeSupport` | `ThermostatDemo` | smart home | The JDK's built-in Observer for JavaBeans properties (Swing and IDE tooling use it) | listener receives property name, old and new value; setting an equal non-null value fires nothing; a listener registered for `"target"` does not see `"mode"` changes |
| `observer.eventbus` — `EventBus` with `<E extends ShopEvent> Subscription subscribe(Class<E>, Consumer<? super E>)` and `publish(ShopEvent)`; `sealed interface ShopEvent permits OrderPlaced, PaymentFailed, OrderShipped` (records); `AuditLog` (a `Consumer<ShopEvent>` with the exhaustive `switch`); `Subscription` is reused from `observer.modern` | `EventBusDemo` | online shop (bridge to the capstone "PatternShop") | Typed publish/subscribe: publishers and handlers never reference each other; a handler for `ShopEvent` sees everything; handlers switch exhaustively over the sealed type | handler receives only its event type; supertype handler receives all events; delivery in subscription order; events without a subscriber are kept in `deadEvents()`; an event published *from inside* a handler is queued and delivered after the current event finishes (no re-entrant recursion — delivery order asserted) |
| `observer.flow` — `TemperatureFeed` (wraps a `SubmissionPublisher<Reading>` with an injected `Executor` and buffer size), `BatchSubscriber(batchSize)` (a `Flow.Subscriber` requesting `batchSize` at a time), `ManualSubscriber` (a slow subscriber that requests only when its owner calls `request(n)`), `CelsiusToFahrenheit` (a `Flow.Processor` extending `SubmissionPublisher`); records `Reading(String sensor, int sequence, double celsius)` and `FahrenheitReading(String sensor, int sequence, double fahrenheit)` (the processor's output) | `FlowDemo` | IoT sensor readings | Reactive Streams in the JDK: demand-driven delivery, back-pressure, processor stages, completion and error signals; `offer` with a drop handler for a slow subscriber | with `Runnable::run`: `onSubscribe` then all `onNext` in order then `onComplete` after `close()`; batch subscriber issues `request(batchSize)` on subscribe and after every full batch, i.e. ⌊n / batchSize⌋ + 1 times (= ⌈n / batchSize⌉ when n is not a multiple of batchSize: it cannot know that the stream has ended); subscriber with no remaining demand and a full buffer (capacity 2) causes `offer` to drop and the drop count is exact; exception thrown in `onNext` → `onError` with that exception and the subscriber is removed; `request(0)` → `onError(IllegalArgumentException)`; processor output in °F; one asynchronous test on the default `ForkJoinPool` awaits `consume(...)` and checks the received list |
| `mediator.chat` — `ChatRoom` (mediator: `join`, `leave`, `send`), `Participant` (colleague with an inbox) | `ChatRoomDemo` | chat room | Canonical Mediator: participants talk only to the room, never to each other; the room owns the routing rules | broadcast reaches everyone except the sender; `@name` direct message reaches only that participant; unknown `@name` returns a notice to the sender only; after `leave` nothing is delivered; `Participant` declares no field of type `Participant` (reflection check) |
| `mediator.atc` — `ControlTower(Consumer<String> radio)` (mediator; the radio hears every request and answer), `Aircraft` (colleague); `sealed interface TowerRequest permits RequestLanding, RequestTakeoff, DeclareEmergency, RunwayVacated` (records) | `AirTrafficDemo` | airport with one runway | The mediator holds the shared resource and the queue; colleagues send typed requests handled by one exhaustive `switch` | at most one aircraft is cleared for the runway at a time; waiting aircraft are cleared FIFO when the runway is vacated; an emergency jumps the queue; every aircraft receives exactly the messages addressed to it (clearance / hold with queue position) |
| `mediator.form` — `SignUpForm` (mediator), widgets `TextField`, `Checkbox`, `Button` extending `abstract sealed class Widget` (each calls `form.changed(this)`) | `SignUpFormDemo` | sign-up dialog | GUI Mediator (like `javax.swing.ButtonGroup`): widget interdependencies live in one class instead of widgets referencing each other | "Submit" disabled initially and enabled only when e-mail is valid, password ≥ 8 chars and terms accepted; checking "business account" enables the company field, unchecking clears and disables it; company name becomes required only for business accounts |

Task **M07-2b** (#43) — Chain of Responsibility & Memento:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `chain.support.classic` — abstract `SupportHandler` (`linkTo(next)`, `handle(Ticket)`), `Helpdesk`, `TechnicalSupport`, `Engineering`; `enum Topic`; record `Ticket(String id, Topic topic, int severity)`, record `Resolution(String ticketId, String handledBy, List<String> escalationPath)` | `SupportEscalationDemo` (one demo in `chain`, runs both chains on the same tickets) | support-ticket escalation | Classic GoF chain: each handler handles or forwards to its successor; the sender knows only the head | each ticket resolved by the first capable level; escalation path lists every level visited in order; a ticket nobody can handle yields `Optional.empty()` (end of chain reached) |
| `chain.support.modern` — functional `TicketHandler` (`Optional<Resolution> handle(Ticket)`) with `default TicketHandler orElse(TicketHandler next)` and `default List<String> levels()` (the composition needs the level names to rebuild the escalation path); `SupportLevels` (static factories `level(name, predicate)`, `helpdesk()`, `technicalSupport()`, `engineering()`, `standardChain()`) | `SupportEscalationDemo` | support-ticket escalation | The same chain as composed functions: `helpdesk.orElse(technical).orElse(engineering)` using `Optional.or` | same resolution as the classic chain for every ticket in a shared table (parameterised test); handlers after the one that decided are never invoked (call counter) |
| `chain.middleware` — records `Request(String method, String path, Map<String,String> headers)` and `Response(int status, String body)`; functional `Handler` and `Middleware` (`Handler wrap(Handler next)`); `Pipeline.of(List<Middleware>, Handler endpoint)`; `Middlewares.logging(List<String> log)`, `authentication(Set<String> tokens)`, `errorBoundary()`, `requestId(Supplier<String>)` | `MiddlewareDemo` | HTTP-style server pipeline | Chain of Responsibility where every handler may act before *and* after its successor or short-circuit — the model behind servlet `Filter` / `FilterChain.doFilter` and the JDK's `com.sun.net.httpserver.Filter.Chain` | middlewares run in declared order on the way in and reverse order on the way out (log asserted); missing/invalid token → 401 and the endpoint is not invoked; `errorBoundary` turns an endpoint exception into 500 with the message; `requestId` adds a deterministic header (injected supplier); a pipeline with no middleware equals the endpoint |
| `chain.validation` — functional `Validator<T>` returning `sealed interface ValidationResult permits Valid, Invalid` (`Invalid(List<String> errors)`); factory `Validator.rule(predicate, error)`, combinators `and` (collect all) and `andThen` (fail fast), `ValidationResult.merge`; records `Valid()` / `Invalid(List<String>)`; record `SignUp(String email, String password, int age)`; `SignUpRules` (the rules and both chains) | `ValidationChainDemo` | user sign-up | A chain where every link runs (collect-all) vs. the first failure stops it (fail-fast) — the two policies side by side | collect-all reports every error in chain order; fail-fast reports only the first and does not invoke later validators; all-valid input gives `Valid`; `Invalid` errors list is immutable |
| `memento.classic` — `TextDocument` (originator: `type`, `moveCursor`, `save()`, `restore(Memento)`), `TextDocument.Memento` (public static nested class, **no public accessors**, private final fields), `History` (caretaker: `push`, `pop`) | `ClassicMementoDemo` | text editor | Classic GoF Memento: only the originator can read its snapshot; the caretaker stores it without looking inside | restore brings back exact text and cursor; a saved memento is unaffected by later edits; `Memento` exposes no public methods beyond `Object`'s (reflection check); `History.pop` on empty returns `Optional.empty()` |
| `memento.editor` — `Editor` with `record EditorSnapshot(String text, int cursor, int selectionStart, int selectionEnd)`; `UndoHistory(capacity)` over two `ArrayDeque<EditorSnapshot>` (undo and redo; `peekUndo()` uses `getLast`); `Editor(int historyCapacity)` | `EditorUndoDemo` | text editor with undo/redo | Modern Memento: immutable record snapshots can be shared safely; history uses sequenced-collection methods (`addLast`/`removeLast`/`getLast`, `removeFirst` to drop the oldest, `reversed()` for a newest-first view) | undo then redo round-trips; a new edit clears the redo stack; capacity 3 keeps only the 3 newest snapshots; `history()` lists newest first; undo/redo on empty history returns `false` and leaves state unchanged; snapshot validation (cursor inside text) in the compact constructor |
| `memento.game` — `Game` (originator), records `Position(int x, int y)` and `GameState(int level, int health, Position position, List<String> inventory)` (compact constructor: `List.copyOf`), `SaveSlots(maxSlots)` (caretaker over a `SequencedMap<String, GameState>` backed by `LinkedHashMap`; `save` returns the evicted slot name, if any) | `GameSaveDemo` | game save/restore | Snapshots of a whole game as deeply immutable records; save slots as a `SequencedMap`: `putLast` makes a re-saved slot the most recent, `lastEntry()` implements "Continue", `pollFirstEntry()` evicts the oldest slot | save → play → load restores the exact state; changing the game's inventory after saving does not change the snapshot; `continueLatest()` loads the most recently *written* slot, including an overwritten older slot; saving into a full set of slots evicts the least recently written one; loading an unknown slot throws with the known slot names |

### Deterministic `Flow` (verified on JDK 27)

All facts below were checked by compiling and running scratch programs with JDK 27+35 (outside the repo,
2026-09-29). The examples and tests rely on them:

1. `new SubmissionPublisher<>(Runnable::run, bufferSize)` (a caller-runs `Executor`) delivers **synchronously on the
   calling thread**: `subscribe(...)` has called `onSubscribe` before it returns, each `submit`/`offer` has called
   `onNext` (if there is demand) before it returns, and `close()` has called `onComplete` before it returns. Demos and
   ordering tests therefore need no sleeps or latches.
2. `close()` with items still buffered does **not** skip them: `onComplete` is signalled only after the buffered items
   were delivered (on later `request(n)` calls).
3. The buffer capacity is rounded up to a power of two (`getMaxBufferCapacity()`: 4 → 4, 5 → 8); tests use powers of
   two so the drop counts are exact.
4. With no remaining demand and a full buffer, `offer(item, onDrop)` calls the drop handler and returns a negative
   number; buffered items are still delivered later. `submit(item)` instead **blocks** the publishing thread until
   demand arrives — with a caller-runs executor that is a self-deadlock if the same thread must call `request`. Rule
   for the examples: bounded-demand demos use `offer` with a drop handler, never `submit`.
5. An exception thrown from `onNext` cancels that subscription and is delivered to the same subscriber's `onError`
   (`getNumberOfSubscribers()` drops by one); `request(0)` leads to `onError(IllegalArgumentException)`; subscribing
   after `closeExceptionally(e)` gives `onSubscribe` followed by `onError(e)`.
6. With the default executor (`ForkJoinPool.commonPool()`), delivery is asynchronous; the one asynchronous test waits
   on the `CompletableFuture` returned by `consume(...)` (completes after `close()`) with a 5 s timeout and asserts
   only the received list, never thread names or timing.

Also verified: `PropertyChangeSupport.firePropertyChange` fires nothing when old and new values are equal and
non-null (it does fire for `null → null`); `LinkedHashMap.put` on an existing key keeps its position while `putLast`
moves it to the end; `ArrayDeque.reversed()` is a live `Deque` view; `java.util.Observable` is
`@Deprecated(since = "9")` (not for removal); `com.sun.net.httpserver.Filter` and `Filter.Chain` exist in module
`jdk.httpserver`. `java.beans` (module `java.desktop`) resolves with the source launcher without extra flags.

## Assignments

### ex01 — Live auction notifications (Observer)

- **Goal:** build a subject with typed, unsubscribable listeners and well-defined delivery rules.
- **Given (do not modify):** record `Bid(String bidder, long amountCents)`; `sealed interface AuctionEvent permits
  BidPlaced, BidRejected, Sold, Unsold` with records `BidPlaced(Bid bid)`, `BidRejected(Bid bid, String reason)`,
  `Sold(Bid winningBid)`, `Unsold()`; interface `Subscription` (`void close()`, extends `AutoCloseable`); interface `Auction`
  (`Subscription subscribe(Consumer<? super AuctionEvent>)`, `<E extends AuctionEvent> Subscription
  subscribe(Class<E> type, Consumer<? super E>)`, `void placeBid(Bid)`, `void close()`, `Optional<Bid>
  highestBid()`).
- **Rules:** the first accepted bid must be ≥ the starting price, every later one ≥ highest + minimum increment;
  otherwise `BidRejected` with reason `"below starting price"` / `"below minimum increment"`; after `close()` every bid
  is rejected with `"auction closed"`. `close()` publishes `Sold(highest)` or `Unsold()` exactly once (a second
  `close()` does nothing). Listeners are notified in subscription order. Subscribing or unsubscribing *during* a
  delivery takes effect from the next event. A listener that throws does not stop the others; its exception goes to
  the error handler given to the constructor. `Subscription.close()` is idempotent.
- **Student writes:** `LiveAuction implements Auction` with constructor
  `LiveAuction(long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler)`.
- **Acceptance criteria (contract tests):** `acceptsFirstBidAtStartingPrice`, `rejectsBidBelowStartingPrice`,
  `rejectsBidBelowMinimumIncrement`, `highestBidReflectsAcceptedBidsOnly`, `listenersNotifiedInSubscriptionOrder`,
  `typedSubscriptionReceivesOnlyItsEventType`, `closedSubscriptionReceivesNothing`,
  `closingASubscriptionTwiceIsHarmless`, `unsubscribingDuringDeliveryTakesEffectFromTheNextEvent`,
  `subscribingDuringDeliveryTakesEffectFromTheNextEvent`,
  `failingListenerDoesNotStopOthers`, `closeWithBidsPublishesSold`, `closeWithoutBidsPublishesUnsold`,
  `closeIsPublishedOnlyOnce`, `bidsAfterCloseAreRejected`, `rejectsNullArguments`.

### ex02 — Expense approval chain (Chain of Responsibility)

- **Goal:** route a request along a chain where each link decides or passes it on, and record the path it took.
- **Given (do not modify):** `enum Category { TRAVEL, MEALS, EQUIPMENT, TRAINING }`; record
  `Expense(String id, String employee, Category category, long amountCents)`; `sealed interface Decision permits
  Approved, Rejected` with records `Approved(String approver)` and `Rejected(String approver, String reason)`; record
  `ApprovalResult(String expenseId, Decision decision, List<String> trail)`; interface `Approver` (`String name()`,
  `Optional<Decision> review(Expense)` — empty means "pass it on"); interface `ApprovalChain`
  (`ApprovalResult submit(Expense)`).
- **Rules:** `PolicyCheck` (first link) rejects non-positive amounts and `MEALS` above 100.00, otherwise passes;
  `TeamLead` approves up to 500.00 except `EQUIPMENT`; `Manager` approves up to 5 000.00; `Director` approves up to
  20 000.00; boundaries are inclusive. If no link decides, the result is `Rejected("chain", "no approver could
  decide")`. The trail lists the names of every approver that reviewed the expense, in order. The chain stops at the
  first decision.
- **Student writes:** `PolicyCheck` (`"policy check"`; reasons `"amount must be positive"`, `"meals above 100.00"`), `TeamLead` (`"team lead"`), `Manager` (`"manager"`), `Director` (`"director"`) (each `implements Approver`; the starters already return these names) and
  `ApprovalChains` with static factories `standard()` (policy → team lead → manager → director) and
  `of(List<Approver>)` (any approvers, in the given order).
- **Acceptance criteria (contract tests):** `smallTravelExpenseApprovedByTeamLead`, `equipmentSkipsTeamLead`,
  `mediumExpenseEscalatesToManager`, `largeExpenseEscalatesToDirector`, `tooLargeExpenseRejectedAtEndOfChain`,
  `policyCheckRejectsExpensiveMealsBeforeAnyApprover`, `policyCheckRejectsNonPositiveAmounts`,
  `boundaryAmountsBelongToTheLowerApprover`, `trailListsReviewersInOrder`, `chainStopsAtFirstDecision` (test-only
  counting approver), `customChainUsesOnlyGivenApprovers`, `emptyChainRejects`, `trailIsImmutable`,
  `rejectsNullExpense`.

## Quiz topics

Push vs. pull Observer; why `java.util.Observable` was deprecated; the lapsed-listener memory leak and how an
unsubscribe handle / try-with-resources fixes it; notification order and re-entrant publishing; what `request(n)`
means and what back-pressure protects; `submit` vs. `offer` on `SubmissionPublisher`; the `onSubscribe → onNext* →
onComplete | onError` protocol; Mediator vs. Observer (event bus) vs. Facade; the "god mediator" smell; classic
linked chain vs. composed functions; middleware ordering (in vs. out); fail-fast vs. collect-all validation; what
happens when nobody handles a request; why a memento should be opaque and how records trade opacity for safe
sharing; deep immutability of snapshots (`List.copyOf`); Memento-based undo vs. Command-based undo (m06); memory cost
of snapshot histories and why they are bounded.

## Out of scope

Reactive libraries (Reactor, RxJava, Akka Streams) beyond a mention; writing a spec-compliant `Flow.Publisher` from
scratch (the Reactive Streams TCK); distributed messaging (Kafka, JMS); Swing/JavaFX GUIs (the form mediator is
headless); serialisation of mementos to disk; Command-based undo (m06); domain events in a hexagonal architecture
(m11); preview features.

## Decisions (owner, 2026-09-29)

All questions below were answered **yes**: the recommended defaults apply.

1. **Assignment coverage:** ex01 grades Observer and ex02 grades Chain of Responsibility; Mediator and Memento are
   practised through the examples and the quiz only (Memento-style undo overlaps with m06's Command undo exercise).
   *Recommended default:* keep two assignments as above; swap ex02 for a bounded undo/redo Memento exercise only if
   m06's assignments do not already cover undo.
2. **Flow determinism:** all `Flow` demos and ordering tests use a caller-runs executor (`Runnable::run`), and exactly
   one test uses the default asynchronous `ForkJoinPool` and awaits `consume(...)` with a 5 s timeout (see
   "Deterministic `Flow`"). *Recommended default:* accept; no latches or sleeps anywhere else.
3. **Capstone bridge:** `observer.eventbus` uses PatternShop events (`OrderPlaced`, `PaymentFailed`, `OrderShipped`)
   and `chain.middleware` models a request pipeline, so students can reuse both ideas in the capstone that starts this
   week; m11 later revisits domain events. *Recommended default:* accept.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m07-behavioral-communication verify` green
- [ ] Lesson EN + TR + PDFs (with sequence diagrams for Observer/`Flow` and Chain of Responsibility); `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
