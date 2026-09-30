# Spec: m11-architecture-enterprise — Architecture & Enterprise Patterns

> Status: **APPROVED** (owner, 2026-09-30) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 13 · Task: #61

## Objective

m02–m10 taught patterns inside a handful of classes. m11 zooms out to the shape of a whole application: who creates
the objects (Dependency Injection by hand, in one composition root), where persistence hides behind a collection-like
interface (Repository), how the domain core is kept independent of the CLI, files and payment providers around it
(Ports & Adapters / hexagonal architecture), how an aggregate announces what happened without calling its listeners
directly (domain events, dispatched only after the change is committed), what goes wrong when patterns are missing or
overused (anti-patterns) and how to get out of it safely (refactoring to patterns under characterization tests), how
to test all of this without a mocking library (hand-written test doubles), and how to stop the architecture from
eroding (executable ArchUnit rules). After this module a student can structure the capstone "PatternShop" order
processing system (SPEC.md §1, §2, §9) the way the reference solution is structured: a domain core with ports,
in-memory and file adapters, one composition root, events after commit, and architecture tests that fail the build
when a dependency points the wrong way.

## Learning outcomes

After this module a student can:

1. **Wire** an application by hand in a composition root with explicit lifetimes (application, per-request,
   transient), **explain** how a reflective DI container works and **compare** it with manual wiring (compile-time vs.
   run-time errors), constructor vs. setter injection, and Service Locator.
2. **Implement** a Repository for an aggregate with an in-memory and a file-backed implementation that pass the same
   abstract contract test, and **use** Specifications and optimistic versioning with it.
3. **Structure** a use case as Ports & Adapters: domain core, inbound/outbound ports, application service, adapters,
   composition root — and **swap** adapters without touching the core.
4. **Implement** domain events recorded by aggregates and dispatched after commit, and **explain** why dispatching
   before commit (or inside the aggregate) is wrong and what a transactional outbox adds.
5. **Recognise** god class, anaemic domain model, Singleton/Service Locator abuse and "patternitis", and **refactor**
   them to patterns in small behaviour-preserving steps protected by characterization tests.
6. **Choose** between dummy, stub, fake, spy and mock, **write** each by hand, and **enforce** layering, cycle and
   naming rules with ArchUnit.

## Prerequisites

m03 (composition root with `production()` / `forTests(...)`, `Clock` injection — m11 builds on it, it does not
repeat it), m04 (Adapter — the payment adapter reuses it), m06 (Strategy, Command), m07 (Observer, typed event bus
over a sealed `ShopEvent` hierarchy, Chain of Responsibility — domain events here add *when* to publish), m08 (enum /
sealed state machines — the order lifecycle), m09 (immutable records, sealed result types), m01 (DIP, coupling /
cohesion, SOLID).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Dependency Injection (Bağımlılık Enjeksiyonu), manual | `new` inside classes, factories and singletons everywhere; framework containers (Spring, Guice, CDI) | constructor injection of interfaces and functions (`Supplier`, `Clock`); one composition root with explicit lifetimes; a 100-line reflective container to demystify frameworks; `AutoCloseable` resources closed in reverse creation order | records, lambdas, try-with-resources, reflection (`java.lang.reflect.Constructor`) |
| Repository (Depo) | DAO per table with `find*`/`update*` methods returning mutable entities | collection-like interface per aggregate returning `Optional` and immutable `List`s; Specification as a functional interface with `and`/`or`/`not` default methods; optimistic versioning; one abstract contract test for every implementation | records (395), `Optional` as return type, default methods, sequenced collections (431) |
| Ports & Adapters (Portlar ve Adaptörler) / Altıgen Mimari | layered architecture (UI → service → DAO) where the domain depends on persistence | inbound ports (use-case interfaces), outbound ports (interfaces owned by the core), adapters at the edge, composition root in `config`; sealed result types instead of exceptions for business outcomes | sealed (409), records, `switch` patterns (441), record patterns (440) |
| Domain events | Observer called from inside the entity; events published before the transaction commits | aggregate records immutable event records (sealed hierarchy) and hands them out with `pullEvents()`; a unit of work commits, *then* dispatches; transactional outbox with at-least-once relay and an idempotent consumer | sealed, records, `switch` patterns, `ArrayDeque` |
| Anti-patterns (anti-kalıp) & refactoring to patterns | god class, anaemic domain model, Singleton / Service Locator abuse, speculative generality ("patternitis") | *before* and *after* packages side by side, the same characterization test run against both; replace type-code conditionals with a sealed type + exhaustive `switch`; replace hard-wired calls with events | sealed, records, `switch` patterns, unnamed variables `_` (456) |
| Test doubles (test ikizi: dummy, stub, fake, spy, mock) | mocking libraries (Mockito) | hand-written doubles as small classes or lambdas; a fake `Clock`; a mock that verifies expectations; when to use which | lambdas, records, `java.time.Clock` |
| Architecture rules | wiki pages and code reviews | executable rules in tests: ArchUnit onion/layered architecture, no cycles, naming, "only `config` touches adapters"; a dependency-free mini checker on the Class-File API to show how such tools read bytecode | Class-File API `java.lang.classfile` (JEP 484, final in 24 — see open question 3); ArchUnit 1.5.1 (test scope, see open question 1) |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m11.examples`. Every demo prints deterministic output and runs
with `java <File>.java` (the multi-file source launcher resolves the other packages of the example from the same
source tree — verified, see [Verified on JDK 27](#verified-on-jdk-27)). Demos that touch files create a temporary
directory, never print its path, and delete it before exiting. Ids and time come from injected generators and clocks.
`src/main` has zero external dependencies; ArchUnit appears only in `src/test`. Demos of `di.*`, `repository.*`,
`events.*`, `antipatterns.*`, `refactoring.*` and `testdoubles.*` live in the parent package (e.g. `di.LifetimesDemo`,
`repository.CatalogRepositoryDemo`); the hexagon demos live in their `config` package. File demos use
`examples.support.TempDirectory` (an `AutoCloseable` record that deletes the directory in try-with-resources).

Task **M11-2a** (#62) — Dependency Injection, Repository, Ports & Adapters:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `di.lifetimes` — `ShopCompositionRoot` (`production(Clock, Consumer<String> disk)` — `disk` stands in for the file system the audit files write to —, `priceList()`, `beginRequest()`, `requestLog()`, `close()`), `RequestScope` (`beginRequest()` returns an `AutoCloseable` scope), collaborators `PriceList` (application lifetime), `Basket` (per request), `RequestLog` (transient), two `AuditFile`s (`orders.audit`, `access.audit`: `AutoCloseable` resources written through the injected clock) | `LifetimesDemo` | shop requests | Manual DI beyond m03: three lifetimes expressed with plain fields, `Supplier`s and scopes; the root owns what it creates and closes it in reverse order | application-lifetime object is the same instance in every request; per-request object is shared within one request and new in the next; transient object is new on every lookup; `close()` closes resources in reverse creation order and is idempotent; using the root after `close()` throws `IllegalStateException` |
| `di.styles` — `InvoiceNumberer` in three variants: `ConstructorInjected(Clock, Supplier<Long>)`, `SetterInjected` (`setClock`, `setSequence`), `HiddenDependencies` (creates `Clock.systemDefaultZone()` and its own counter inside — an instance field, because mutable static state is allowed only in `antipatterns.globalstate.before`, Decision 2) | `InjectionStylesDemo` | invoice numbering | Why constructor injection is the default: dependencies are visible, required and final; setter injection allows half-built objects (temporal coupling); hidden dependencies cannot be replaced in a test | constructor variant rejects `null` at construction; with a fixed clock and sequence it produces exactly `INV-2026-09-0001`, `…0002`; setter variant used before both setters throws `IllegalStateException("clock not set")`; the hidden variant's constructor declares no parameters (reflection check — the dependency is invisible) |
| `di.container` — `MiniContainer` (`bind(Class<T>, Class<? extends T>)`, `bindInstance`, `singleton(Class<?>)`, `get(Class<T>)`): resolves the single public constructor recursively; sample graph `ReportService → ReportRepository, ReportFormatter → Clock` | `MiniContainerDemo` | reporting | How Spring/Guice-style containers work underneath (reflection over constructors, scopes, cycle detection) — and why errors move from compile time to run time | resolves a three-level graph; `singleton` returns the same instance, unbound concrete classes are created fresh each time; missing binding for an interface throws `IllegalStateException` naming the type and the resolution path; a cycle `A → B → A` throws with the message `dependency cycle: A -> B -> A`; a class with two public constructors is rejected; no unchecked casts (`Class.cast`) — compiles under `-Xlint:all -Werror` |
| `repository.catalog` — record `Product(Sku sku, String name, Category category, Money price)`; `ProductRepository` (`save`, `findById → Optional`, `findAll`, `findMatching(Specification<Product>)`, `delete`, `count`); functional `Specification<T>` with default `and`/`or`/`not`; `ProductSpecs` (`inCategory`, `priceAtMost`, `nameContains`); `InMemoryProductRepository`; `FileProductRepository(Path)` (one product per line, fields separated by `|`) | `CatalogRepositoryDemo` | PatternShop catalogue | Repository as a collection-like interface; Specification for queries instead of a `findBy…` method explosion; two implementations held to one abstract `ProductRepositoryContract` (the same pattern as the course's exercise contracts) | *contract, run for both implementations:* save then `findById` returns an equal product; saving the same SKU again replaces it; `findAll` is sorted by SKU; returned lists are immutable and do not change after later saves; `and`/`or`/`not` specifications select exactly the expected SKUs; `delete` of an unknown SKU returns `false`; `null` arguments rejected. *File only:* a new repository instance on the same path sees earlier saves (survives "restart"); a name containing `|` is rejected with `IllegalArgumentException` |
| `repository.orders` — `Order` aggregate (id, lines, `version`), `OrderRepository` (`nextId()`, `save`, `findById`), `InMemoryOrderRepository`, `ConcurrentUpdateException` | `OptimisticLockingDemo` | two clerks editing one order | Repository of an aggregate: `findById` returns an isolated copy, `save` checks the version (optimistic concurrency) — the in-memory stand-in for what JPA's `@Version` does | edits to a loaded copy are invisible until `save`; save increments the version; the second of two concurrent saves from the same version throws `ConcurrentUpdateException` with both versions in the message and the stored order is the first one; `nextId()` yields `order-1`, `order-2`, … |
| `hexagonal.transfer` — `domain`: `Account` (rich: `withdraw`, `deposit`, non-negative balance), records `AccountId`, `Money`; `application`: inbound port `TransferMoneyUseCase`, record `TransferCommand`, sealed `TransferResult permits Transferred, Rejected`, outbound ports `LoadAccountPort`, `SaveAccountPort`, `TransferService`; `adapter.inbound.TextTransferController`; `adapter.outbound.InMemoryAccountStore`; `config.TransferApp` (record) and `config.TransferDemo` | `TransferDemo` | bank money transfer (issue #62) | Minimal canonical hexagon: the use case is an interface, persistence is two narrow outbound ports the core owns, the controller only translates text ↔ command/result | successful transfer moves the amount and saves both accounts; insufficient funds → `Rejected("insufficient funds")` and neither account is saved (spy store); same source and target → rejected; amount above the per-transfer limit (injected) → rejected; unknown account → rejected with its id; zero amount → `Rejected("amount must be positive")`; the service is tested with hand-written fakes only (no adapter on the test path); controller maps `transfer A-1 A-2 25.00` to `OK` / `REJECTED <reason>` |
| `hexagonal.shop` — `domain`: `Order` aggregate, records `Sku`, `Money`, `OrderLine`, `OrderId`, sealed `OrderEvent`; `application`: `port.inbound.PlaceOrderUseCase`, record `PlaceOrderCommand(String customer, List<LineRequest> lines)`, sealed `PlaceOrderResult permits Placed, Rejected`, `port.outbound.{ProductCatalog, PaymentPort, OrderRepository, EventPublisher, OrderIds}`, `PlaceOrderService`; `adapter.inbound.cli.CommandLineAdapter`; `adapter.outbound.memory.{InMemoryOrderRepository, InMemoryProductCatalog, SequentialOrderIds}`; `adapter.outbound.file.FileOrderRepository`; `adapter.outbound.payment.LegacyPaymentAdapter` (wraps the provider stand-in `adapter.outbound.payment.acme.AcmePayClient` with `int pay(String account, long cents)` status codes; `AcmePaySandbox` approves up to a credit limit and records every call); `adapter.outbound.events.RecordingEventPublisher`; `config.ShopCompositionRoot` (a record exposing `placeOrder`, `cli`, `orders`, `events`, `acme`; `inMemory()`, `fileBacked(Path ordersFile)` — ids continue after the stored orders) and `config.ShopDemo` | `ShopDemo` | PatternShop order placement (capstone bridge) | The capstone's architecture in miniature: the same use case runs against in-memory and file adapters by changing only the composition root; the payment provider is adapted (m04) behind an outbound port; business outcomes are a sealed result, not exceptions | *use-case tests, run once per composition root (memory, file):* valid command → `Placed` with total = Σ quantity × price, order saved, exactly one `OrderPlaced` published; unknown SKU → `Rejected("unknown product: X")`, nothing charged, saved or published; payment declined → `Rejected("payment declined")`, nothing saved or published; empty order → `Rejected("empty order")`, blank customer → `Rejected("missing customer")`, quantity ≤ 0 → `Rejected("invalid quantity: X")`; ids are consumed only by placed orders; the service alone (hand-written doubles) charges, then saves, then publishes, and a failing save publishes nothing and propagates. *Adapter tests:* `LegacyPaymentAdapter` maps codes `0 → approved`, `51 → declined`, anything else → `IllegalStateException` with the code; `FileOrderRepository` survives a new instance on the same path; `CommandLineAdapter` parses `place alice BOOK-1:2 PEN-7:1` and prints `PLACED order-1 total 47.00` / `REJECTED <reason>`, and prints usage for malformed input. The ArchUnit rules below run against this package |

Task **M11-2b** (#63) — Domain events, anti-patterns & refactoring, test doubles, architecture rules:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `events.aggregate` — `Order` aggregate recording `sealed interface OrderEvent permits OrderPlaced, OrderPaid, OrderShipped, OrderCancelled` (records) and returning them from `pullEvents()`; `UnitOfWork` (`register(Order)`, `commit()`, `rollback()`); `DomainEventDispatcher` (typed `subscribe`, queue-based `dispatch`); handlers `SendConfirmation`, `ReserveStock`; `OrderStore` (in-memory, can be told to fail) | `DomainEventsDemo` | PatternShop order lifecycle | The aggregate decides *what* happened, the unit of work decides *when* the world hears about it: commit first, dispatch after; m07's event bus reused as the in-process dispatcher, with the timing rule added | no handler runs before `commit()`; after commit events are dispatched in the order they were raised; a failing store → commit throws, no event is dispatched and the store is unchanged; `rollback()` discards pending events; an illegal transition (pay a cancelled order) throws and records no event; a failing handler after commit does not undo the commit and goes to the injected error handler while other handlers still run; an event raised by a handler is dispatched after the current event's handlers finish; `pullEvents()` empties the aggregate's list (no event dispatched twice) |
| `events.outbox` — `OutboxOrderStore` (writes the order state and its events as `OutboxEntry(long sequence, String type, String payload, boolean published)` in one atomic in-memory swap), `OutboxRelay` (`relayPending(MessageBroker)`), `MessageBroker` (functional port), `IdempotentConsumer` (dedupes by sequence) | `OutboxDemo` | order events to a message broker | Transactional outbox: no event is lost if the broker is down after commit; delivery is at-least-once, so consumers must be idempotent | saving an order stores its state and its events together (both or neither — a failing write leaves neither); the relay publishes pending entries in sequence order and marks them published; a broker failure leaves that entry and all later ones pending (order preserved) and the next relay resumes from it; an entry re-delivered after a crash between publish and mark is processed once by `IdempotentConsumer` |
| `antipatterns.godclass` — `before.OrderManager` (validation, `if/else` pricing, map persistence, e-mail text and logging in one class); `after.{OrderValidator, PricingPolicy, OrderRepository, ConfirmationMailer, CheckoutFacade}` | `GodClassDemo` | checkout | God class → Chain (validation), Strategy (pricing), Repository, Facade — the same public behaviour with responsibilities split | one characterization test (parameterised table of orders) passes against both `before` and `after` with identical results and e-mail texts; every `after` class has ≤ 5 public methods and ≤ 4 constructor parameters (reflection check), `before.OrderManager` has more than 10 public methods (documented) |
| `antipatterns.anaemic` — `before.Account` (getters/setters only) + `before.AccountService` (all rules); `after.Account` (behaviour and invariants inside), record `Money` | `AnaemicDomainDemo` | bank account | Anaemic domain model vs. rich domain model: in the anaemic version any caller can break an invariant | the same valid scenarios give the same balances in both versions; `before`: `setBalance(-50)` is accepted (test documents the hole); `after`: withdrawing more than the balance, negative amounts and deposits to a closed account throw `IllegalArgumentException` / `IllegalStateException` and leave the balance unchanged; `after.Account` has no public setters (reflection check) |
| `antipatterns.globalstate` — `before.ServiceLocator` (static registry) and `before.StockLevels` (Singleton holding mutable stock) used by `before.ReorderService`; `after.ReorderService(StockLevels, Supplier…)` with `after.StockLevels` as an interface + `InMemoryStockLevels` | `GlobalStateDemo` | stock reorder | Singleton abuse and Service Locator hide dependencies and leak state between tests; constructor injection makes them explicit (see open question 2) | `before`: state set by one scenario is visible in the next unless `ServiceLocator.reset()` is called (test documents the leak and resets in `@AfterEach`); `before.ReorderService` has a no-arg constructor although it needs two collaborators (reflection); `after`: two services with their own stock never interfere; constructor parameter types reveal every dependency; reorder decisions are identical to `before` for the same stock table |
| `antipatterns.patternitis` — `before.{GreeterFactoryProvider, GreeterFactory, GreetingStrategy, AbstractGreeter, FormalGreeter}`; `after.Greetings` (one static method + one `Function<String,String>` for the only real variation) | `PatternitisDemo` | greeting message | Speculative generality: patterns without a second variation are cost, not design — the "when NOT to use" of the whole course | both versions produce identical output for a table of names (characterization); the `after` version needs 1 type instead of 5 (asserted by listing the package's types, documenting the difference) |
| `refactoring.shipping` — `before.ShippingCalculator` (`switch` on a `String` type code with nested `if`s); `after.ShippingMethod` (`sealed interface permits Standard, Express, Pickup`, records) + `after.ShippingCalculator` (exhaustive `switch` with record patterns) | `ShippingRefactoringDemo` | shipping cost | Replace Conditional with Polymorphism / Replace Type Code with a sealed type, step by step (Extract Method → introduce sealed type → move logic → delete the string code); the compiler now flags a missing case | the characterization table (method × weight × distance → cost, including boundaries) gives identical results for `before` and `after`; `before` silently returns 0 for an unknown code (documented), `after` has no unknown case; no `default` branch in the `after` switch (source-level check in the lesson, behaviour asserted per variant) |
| `refactoring.notifications` — `before.RegistrationService` calling `Mailer`, `CrmClient`, `Analytics` directly; `after.RegistrationService` raising `UserRegistered` through a `DomainEventDispatcher` with three subscribed handlers | `NotificationsRefactoringDemo` | user sign-up | Replace hard-wired side effects with Observer / domain events: the service no longer changes when a new reaction is added | both versions record the same three side effects for the same registrations (spy); `after.RegistrationService` has one collaborator instead of three (reflection); adding a fourth handler in the test requires no change to `after.RegistrationService` |
| `testdoubles.checkout` — `CheckoutService(PriceLookup, PaymentGateway, ReceiptRepository, Notifier, AuditLog)`; hand-written doubles in `testdoubles.checkout.doubles`: `DummyAuditLog` (throws if touched), `StubPriceLookup` (canned prices), `FakeReceiptRepository` (working in-memory), `SpyNotifier` (records calls), `MockPaymentGateway` (`expectCharge(customer, cents)`, fails fast on an unexpected call, `verify()`) | `TestDoublesDemo` | checkout | The five kinds of test double written by hand, each used for the collaborator where it fits; the lesson's table of "when to use which" (state vs. interaction verification, classicist vs. mockist) | a checkout with stub prices charges the expected amount (mock `verify()` passes); an unexpected charge fails immediately with a message naming expected vs. actual; `verify()` reports a missing expected call; the spy records notifications with arguments in order; the fake repository finds the saved receipt; the dummy throws `AssertionError` if the service ever touches the audit log on the happy path |
| `testdoubles.time` — `SessionManager(Clock, Duration ttl, Supplier<String> tokens)`; `MutableClock extends Clock` (fake: `advance(Duration)`); `SequenceTokens` | `SessionExpiryDemo` | login sessions | Time and randomness as injected collaborators: a fake clock makes expiry testable to the millisecond without `sleep` | session valid just before `ttl`, expired at exactly `ttl` (rule: expired when `now ≥ created + ttl`); `touch` extends expiry; `Clock.fixed` and `MutableClock` give identical results for a non-advancing scenario; tokens are `token-1`, `token-2`, …; `MutableClock.withZone` shares the same instant (advancing one advances both) |
| `archcheck` — `DependencyScanner` (reads a class's bytes with `Class.getResourceAsStream` and parses them with `java.lang.classfile.ClassFile`; collects types from constant-pool `ClassEntry`s and field / method descriptors), record `Violation(String from, String to)`, `LayerRule(String packagePrefix, Set<String> forbiddenPrefixes)` | `ArchCheckDemo` | architecture erosion | How architecture tools work underneath, with no dependency: a class-file reader finds forbidden dependencies. Scans an explicit list of classes (no classpath scanning) from `erosion` and `hexagonal.shop` | finds `erosion.domain.Invoice → erosion.adapter.InvoiceDao`; reports no violation for `hexagonal.shop.domain`; finds a dependency that appears only as a field type; **documented limitation:** a type that appears only as a generic type argument (e.g. `Consumer<OrderEvent>`) is *not* found — test asserts the miss, the lesson explains why ArchUnit (which also reads generic signatures) is the real tool; class files report major version 71 |
| `erosion` — `domain.Invoice` calls `adapter.InvoiceDao`, which takes an `Invoice` (a package cycle) | `ErosionDemo` | invoicing | A small, deliberately broken design that compiles and runs: architecture erosion is invisible to the compiler — only rules catch it | the demo runs and prints `stored …` (the code "works"); the ArchUnit test class `ErosionRulesTest` asserts that the domain-isolation rule and the no-cycles rule **fail** on this package, and that the failure messages name `Invoice.save()` and the `adapter → domain → adapter` cycle |

**Architecture tests (test scope only, `src/test/java/…/m11/architecture/`, ArchUnit — see open question 1):**

| Test class | Rules | Tested behaviour |
|---|---|---|
| `HexagonalShopArchitectureTest` (plain JUnit tests calling `rule.check(classes)` on classes imported once with `ImportOption.DoNotIncludeTests`) | `onionArchitecture()` with `domainModels("..shop.domain..")`, `applicationServices("..shop.application..")`, one `adapter(…)` per adapter package, `withOptionalLayers(true)`, and `ignoreDependency(resideInAPackage("..config.."), alwaysTrue())` for the composition root; domain classes only depend on `java.lang..`, `java.util..`, `java.math..`, `java.time..` and the domain package; adapters do not depend on each other (`slices().matching("..shop.adapter.(*).(*)..").should().notDependOnEachOther()`); outbound ports are interfaces; classes in `..adapter..` are only accessed from `..adapter..` and `..config..`; no cycles between `hexagonal.shop` slices | all rules pass on `hexagonal.shop`; the same rules are also run on `hexagonal.transfer` |
| `ErosionRulesTest` | the domain-isolation and no-cycles rules above applied to `erosion` | each rule throws `AssertionError`; messages asserted as in the `erosion` row |
| `CourseConventionsArchTest` (`@AnalyzeClasses(packages = "io.github.aliturgutbozkurt.patterns.m11")` + `@ArchTest` fields, the JUnit-engine style, shown once) | `src/main` depends only on `java..` and the course root package (proves "zero external dependencies"); no cycles between top-level example packages (`erosion` excluded); no class calls `System.exit`; non-final static fields exist only in `antipatterns.globalstate.before` (enforces open question 2) | all rules pass |

**Implementation notes (M11-2b, synced with the code):**

- `events.aggregate`: order ids are strings; `OrderStore` offers `saveAll` (all-or-nothing), `failNextSave()`, `load`,
  `statuses()`. `DomainEventDispatcher<B>` is generic over the event base type (so `refactoring.notifications` reuses
  it) and has `dispatchAll`; after a commit, events go out per aggregate in the order they were raised, aggregates in
  registration order.
- `events.outbox`: `OutboxOrderStore.save(Order)` stores the `events.aggregate.Order` state and its events; a failing
  write (`failNextWrite()`) happens before the aggregate's events are pulled, so a retry loses nothing.
  `OutboxRelay.relayPending` rethrows the broker's exception after stopping.
- `antipatterns.godclass.after` adds the value records `CheckoutRequest` and `PlacedOrder`; the size check (≤ 5 public
  methods, ≤ 4 constructor parameters) applies to the five collaborators named above. `before.OrderManager` has 12.
- `antipatterns.globalstate.before` adds the record `ReorderPolicy`; `after.ReorderService(StockLevels, IntSupplier)`.
  `ServiceLocator.reset()` also resets the `StockLevels` Singleton (the typical test-only hatch).
- `refactoring.shipping.after.ShippingMethod.Pickup` has no components; `refactoring.notifications` keeps the three
  side-effect ports (`Mailer`, `CrmClient`, `Analytics`) in the parent package and wires them in
  `after.SignUpReactions`.
- `testdoubles.checkout`: `PaymentGateway.charge` returns `Optional<String>` (transaction id, empty = declined);
  `MockPaymentGateway.expectDeclinedCharge`; a declined charge is written to the `AuditLog` (so the dummy fails there).
- `CourseConventionsArchTest` analyses all of m11's production code (`examples`, `exercises`, `solutions`), not only
  `examples`. The transfer hexagon's port rule selects classes named `*Port`; its adapter slices are
  `..transfer.adapter.(*)..`. ArchUnit prints the erosion cycle across several lines, so the test compares it with
  whitespace normalised.

Capstone bridge: `repository.catalog` / `hexagonal.shop` prepare slice C3 (domain, repository), `events.aggregate` /
`events.outbox` and `LegacyPaymentAdapter` prepare slice C5 (events, payment adapter), and the architecture tests
above are the template for slice C6's ArchUnit rules (`tasks/todo.md`, Phase 5). m07's `observer.eventbus` is the
in-process dispatcher; m11 adds the rule "dispatch after commit".

### Verified on JDK 27

All facts below were checked on JDK 27 (Oracle, macOS arm64) with scratch projects outside the repo (2026-09-30);
the examples and tests rely on them:

1. **ArchUnit 1.5.1 + JUnit 6.1.3 + Maven Surefire 3.6.0** (the versions pinned in the parent `pom.xml`) import
   `--release 27` class files (major 71), including sealed interfaces, records (`JavaClass.isRecord()` is `true`),
   record patterns and `_` in a `switch`, and evaluate rules correctly. Both styles work: plain `@Test` methods calling
   `rule.check(classes)`, and the ArchUnit JUnit Platform engine (`@AnalyzeClasses` / `@ArchTest` fields) running
   side by side with Jupiter tests under JUnit 6. A deliberately violating rule fails the build with a readable
   `Architecture Violation` message listing each offending call with source line.
2. ArchUnit's `onionArchitecture()` reports **"Layer 'domain service' is empty"** unless `withOptionalLayers(true)`
   is set, and treats the composition root (a package in no layer that constructs adapters) as a violation unless its
   outgoing dependencies are ignored with `ignoreDependency(resideInAPackage("..config.."), alwaysTrue())`. With both,
   the rule passes on a correct hexagon. `layeredArchitecture().consideringOnlyDependenciesInLayers()` with an explicit
   `Config` layer (`mayNotBeAccessedByAnyLayer()`) is an equivalent alternative (also verified).
3. `slices().matching("<root>.(*)..").should().beFreeOfCycles()` detects a two-package cycle and prints the cycle
   (`Slice adapter -> Slice domain -> Slice adapter`) with the dependencies of each slice.
4. `classes().…onlyDependOnClassesThat().resideInAnyPackage("java.lang..", "java.util..", …)` passes for a domain with
   records and sealed interfaces: the record bootstrap (`java.lang.runtime.ObjectMethods`) and `java.lang.invoke` are
   covered by `java.lang..`.
5. ArchUnit logs `SLF4J(W): No SLF4J providers were found` once per run; harmless, no SLF4J binding is added.
6. The multi-file **source launcher** runs a demo whose classes live in five sibling packages
   (`domain`, `application`, `adapter.inbound.cli`, `adapter.outbound.memory`, `config`) with no build, and
   `Class.getResourceAsStream("X.class")` returns the in-memory compiled bytes under the source launcher as well as from
   `target/classes`, so `archcheck` works both ways.
7. The **Class-File API** (`java.lang.classfile`, final since JDK 24) parses those bytes with no flags and compiles
   under `-Xlint:all -Werror`. Constant-pool `ClassEntry`s alone miss types that occur only in descriptors (a field of
   type `Consumer`); adding `fieldTypeSymbol()` / `methodTypeSymbol()` finds them; a type that occurs only as a generic
   type argument (`Consumer<OrderEvent>`) is still missed without reading the `Signature` attribute — hence the
   documented limitation above.

## Assignments

### ex01 — PatternShop checkout as Ports & Adapters (Repository + hexagonal service)

- **Goal:** implement an application service that depends only on ports, plus one outbound adapter, so that the same
  checkout works with any adapter — the core of the capstone.
- **Given (do not modify):** records `Sku(String value)`, `Money(long cents)` (non-negative; `plus`, `times`),
  `OrderId(String value)`, `CartItem(Sku sku, int quantity)`, `Product(Sku sku, String name, Money price, int stock)`,
  `OrderLine(Sku sku, int quantity, Money unitPrice)`, `Order(OrderId id, String customer, List<OrderLine> lines,
  Money total)` (compact constructor: `List.copyOf`, total must equal the sum of the lines), `OrderPlaced(OrderId id,
  String customer, Money total)`; inbound port `CheckoutUseCase` (`CheckoutResult checkout(String customer,
  List<CartItem> cart)`); `sealed interface CheckoutResult permits Confirmed, Rejected` with records
  `Confirmed(Order order)` and `Rejected(String reason)`; outbound ports `ProductCatalog` (`Optional<Product>
  find(Sku)`), `PaymentPort` (`boolean charge(String customer, Money amount)`), `OrderRepository` (`void save(Order)`,
  `Optional<Order> findById(OrderId)`, `List<Order> findByCustomer(String)`, `int count()`), `EventPublisher`
  (`void publish(OrderPlaced)`), `OrderIdGenerator` (`OrderId next()`).
- **Rules:** items are checked in cart order and the first problem decides: empty cart → `"empty cart"`; quantity ≤ 0
  → `"invalid quantity: <sku>"`; unknown SKU → `"unknown product: <sku>"`; stock < quantity → `"insufficient stock:
  <sku>"` (checked after merging: duplicate SKUs are merged into one line at the position of their first
  appearance). Then the total is charged **exactly once**; a declined payment → `"payment declined"`. Only a
  confirmed order consumes an id, is saved, and is then published as exactly one `OrderPlaced` — save strictly before
  publish. If `save` throws, nothing is published and the exception propagates. The repository replaces an order saved
  again with the same id (keeping its position), returns `findByCustomer` in first-save order, and returns immutable
  lists. `null` arguments → `NullPointerException`. The service must not depend on any adapter class.
- **Student writes:** `CheckoutService implements CheckoutUseCase` with constructor `CheckoutService(ProductCatalog,
  PaymentPort, OrderRepository, EventPublisher, OrderIdGenerator)`, and the outbound adapter
  `adapter.InMemoryOrderRepository implements OrderRepository`.
- **Test doubles in the contract:** a stub catalogue, a mock payment port (expected amount, call count), a spy
  publisher and spy repository sharing one call log, a sequence id generator — hand-written, as in
  `testdoubles.checkout`.
- **Acceptance criteria (contract tests):** `confirmsValidCartAndReturnsTheOrder`, `totalIsSumOfLinePrices`,
  `chargesTotalExactlyOnce`, `savesBeforePublishing`, `publishesExactlyOneOrderPlaced`, `rejectsEmptyCart`,
  `rejectsNonPositiveQuantity`, `rejectsUnknownProduct`, `rejectsInsufficientStock`,
  `firstInvalidItemDecidesTheReason`, `mergesDuplicateSkus`, `validationFailureNeverCharges`,
  `declinedPaymentSavesAndPublishesNothing`, `idsAreConsumedOnlyByConfirmedOrders`,
  `saveFailurePublishesNothingAndPropagates`, `repositoryFindsSavedOrderById`,
  `repositorySaveWithSameIdReplacesInPlace`, `repositoryFindsByCustomerInSaveOrder`, `repositoryListsAreImmutable`,
  `serviceDoesNotDependOnAdapters` (ArchUnit rule on the package of the bound `CheckoutService` class),
  `rejectsNullArguments`.

### ex02 — Order lifecycle with domain events dispatched after commit

- **Goal:** make an aggregate record events, commit its state through a port, and dispatch the events only after a
  successful commit — with well-defined handler semantics.
- **Given (do not modify):** record `OrderId(String value)`; `enum OrderStatus { PLACED, PAID, SHIPPED, CANCELLED }`;
  `sealed interface OrderEvent permits OrderPlaced, OrderPaid, OrderShipped, OrderCancelled` with records
  `OrderPlaced(OrderId id, long totalCents)`, `OrderPaid(OrderId id)`, `OrderShipped(OrderId id)`,
  `OrderCancelled(OrderId id, String reason)`; record `OrderSnapshot(OrderId id, OrderStatus status, long
  totalCents)`; outbound port `OrderStore` (`Optional<OrderSnapshot> load(OrderId)`, `void commit(OrderSnapshot)` —
  may throw); interface `Subscription` (`void close()`); interface `OrderLifecycle` (`OrderId place(long
  totalCents)`, `void pay(OrderId)`, `void ship(OrderId)`, `void cancel(OrderId, String reason)`, `OrderStatus
  status(OrderId)`, `<E extends OrderEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler)`).
- **Rules:** transitions `PLACED → PAID → SHIPPED`, and `PLACED | PAID → CANCELLED`; anything else (pay twice, ship
  unpaid, cancel shipped, …) → `IllegalStateException`, nothing committed, nothing dispatched. Unknown id →
  `NoSuchElementException`; `totalCents ≤ 0` → `IllegalArgumentException`. Every command is load → aggregate method
  (records the event) → `commit` → dispatch. If `commit` throws, the exception propagates, no event is dispatched and
  `status` is unchanged. Handlers receive events of their type (a handler for `OrderEvent.class` receives all) in
  subscription order; a throwing handler goes to the error handler, the other handlers still run and the commit stays.
  A command issued from inside a handler is executed, but its events are dispatched after the current event's
  handlers have finished. Each event is dispatched exactly once. `Subscription.close()` is idempotent.
- **Student writes:** an `Order` aggregate (records events, enforces transitions), an `EventDispatcher`, and
  `OrderLifecycleService implements OrderLifecycle` with constructor `OrderLifecycleService(OrderStore store,
  Supplier<OrderId> ids, Consumer<RuntimeException> errorHandler)`.
- **Test doubles in the contract:** a fake `OrderStore` (in-memory, can be switched to fail the next commit) that
  writes `"commit"` into a shared log that handlers also write to.
- **Acceptance criteria (contract tests):** `placeCommitsThenDispatchesOrderPlaced`,
  `eventsAreDispatchedOnlyAfterCommit`, `failedCommitDispatchesNothingAndKeepsState`, `payThenShipFollowsLifecycle`,
  `cannotPayTwice`, `cannotShipUnpaidOrder`, `cannotCancelShippedOrder`,
  `illegalTransitionCommitsAndDispatchesNothing`, `cancelCarriesReason`, `unknownOrderIsRejected`,
  `rejectsNonPositiveTotal`, `typedSubscriberReceivesOnlyItsEventType`, `supertypeSubscriberReceivesAllEvents`,
  `handlersRunInSubscriptionOrder`, `failingHandlerDoesNotStopOthersOrUndoCommit`,
  `commandFromHandlerIsDispatchedAfterCurrentEvent`, `closedSubscriptionReceivesNothing`, `eachEventIsDispatchedOnce`,
  `rejectsNullArguments`.

## Quiz topics

Composition root vs. Service Locator vs. Singleton; constructor vs. setter injection and temporal coupling; what a DI
container does at start-up and why its errors appear at run time; object lifetimes and who closes resources;
Repository vs. DAO; why repositories work per aggregate and return copies or immutable values; Specification vs.
`findBy…` explosion; optimistic vs. pessimistic locking; inbound vs. outbound ports and who owns a port interface;
hexagonal vs. classic layered architecture (dependency direction); why business outcomes are a sealed result and
infrastructure failures are exceptions; why events are dispatched after commit and not from inside the aggregate;
what the outbox guarantees (at-least-once) and why consumers must be idempotent; domain events vs. m07's UI-style
Observer; god class, anaemic domain model, Singleton abuse, patternitis — symptoms and refactorings; what a
characterization test is and why it comes first; dummy vs. stub vs. fake vs. spy vs. mock; state vs. interaction
verification; why mocks of types you don't own are risky; what ArchUnit reads (bytecode, not source) and which rules
belong in a build; why the compiler cannot catch architecture erosion.

## Out of scope

DI frameworks (Spring, Guice, Jakarta CDI, Dagger) beyond a comparison paragraph; JPA/Hibernate, JDBC and real
databases (the file adapters are plain text files); message brokers (Kafka, JMS) — the outbox relay publishes to an
in-memory port; event sourcing, CQRS and sagas beyond a mention; DDD strategic design (bounded contexts, context
maps) beyond a mention; JPMS modules as an architecture-enforcement tool beyond a mention; mocking libraries
(Mockito) beyond a mention; ArchUnit `FreezingArchRule` and PlantUML-based rules; payment compensation when saving
fails after a successful charge; preview features.

## Decisions (owner, 2026-09-30)

All questions below were answered **yes**: the recommended defaults apply (including any build or dependency change they describe).

1. **ArchUnit test dependency (CLAUDE.md "ask first").** The parent `pom.xml` already pins
   `com.tngtech.archunit:archunit-junit5` in `dependencyManagement` (`archunit.version` = 1.5.1), but no module uses
   it yet. The exact change is **one test-scoped dependency in `modules/m11-architecture-enterprise/pom.xml` only**:
   `<dependency><groupId>com.tngtech.archunit</groupId><artifactId>archunit-junit5</artifactId><scope>test</scope></dependency>`
   (no `<version>` — managed by the parent). It is used by the architecture tests of M11-2b and by ex01's
   `serviceDoesNotDependOnAdapters` contract test; `src/main` stays dependency-free (asserted by
   `CourseConventionsArchTest`), and no SLF4J binding is added (the NOP notice is harmless). *Recommended default:*
   yes. If no: the ArchUnit rules move into the lesson text only, `archcheck` becomes the executable rule example, and
   ex01 drops `serviceDoesNotDependOnAdapters`.
2. **Mutable static state in anti-pattern code.** CLAUDE.md §5 allows mutable static state only in the Singleton
   lesson. `antipatterns.globalstate.before` needs a static `ServiceLocator` registry and a stateful Singleton to show
   the problem honestly. *Recommended default:* allow it in that one package only, each type marked
   `// ANTI-PATTERN — see lesson`, tests reset it in `@AfterEach`, and `CourseConventionsArchTest` fails the build if a
   non-final static field appears anywhere else in m11.
3. **Class-File API (JEP 484) in `archcheck`.** It is final since JDK 24 but not yet listed in
   `docs/java27-features.md`, which asks for an explicit decision. *Recommended default:* yes — add a row
   (JEP 484, final since 24, ✅ verified on JDK 27, used in m11 `archcheck`) in the M11-2b PR. If no: drop `archcheck`
   and keep `erosion` + ArchUnit only.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m11-architecture-enterprise verify` green on JDK 27, including the architecture tests
- [ ] Lesson EN + TR + PDFs, with a hexagon diagram (ports, adapters, composition root), a sequence diagram of "commit, then dispatch", a before/after class diagram per anti-pattern and the test-double table; `check-docs.sh` clean
- [ ] New Turkish terms (composition root, aggregate, domain event, outbox, service locator, anaemic domain model, god class, characterization test, Specification) added to `docs/glossary.md` in M11-3, not invented silently
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
