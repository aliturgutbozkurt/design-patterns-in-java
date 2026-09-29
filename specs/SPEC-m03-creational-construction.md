# Spec: m03-creational-construction — Creational II: Construction

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 4 · Task: #21

## Objective

m02 decided *which* class to create. m03 is about *how* to put a complex object together: step by step (Builder),
by copying a ready-made one (Prototype), by reusing expensive ones (Object Pool), and by wiring a whole object graph
in one place (dependency injection by hand). After this module a student can build always-valid immutable objects
with many optional parts, copy object graphs correctly, and explain why virtual threads changed when pooling is
worth it.

## Learning outcomes

After this module a student can:

1. **Implement** a classic Builder with validation in `build()`, a record with a nested builder and defaults, and
   `with`-style copies.
2. **Design** a step (type-safe) builder whose interfaces make an incomplete object impossible to build.
3. **Explain** shallow vs. deep copy, **compare** copy constructors with `Object.clone()`, and **implement** a
   prototype registry.
4. **Implement** a bounded object pool with timeouts and **decide** when pooling helps — and why virtual threads mean
   "never pool threads, limit concurrency instead".
5. **Wire** an application in a composition root by hand and **swap** real collaborators for fakes in tests.

## Prerequisites

m02 (static factories, Abstract Factory), m01 (DIP, composition root), m00 (records, compact constructors).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Builder (İnşacı) | static nested `Builder`, fluent setters, `build()` validates | record + nested builder with defaults; `withX` copies; step builder with interfaces; flexible constructor body computes values before `super(...)` | records (395), flexible constructor bodies (513), sealed (409) |
| Prototype (Prototip) | `Cloneable` + `clone()` | copy constructors / `copy()` methods; immutable records need no copying — share them; `clone()` pitfalls (and JEP 500's direction on reflective final-field mutation) | records, interfaces |
| Object Pool (Nesne Havuzu) | pool of reusable objects, acquire/release | `Semaphore` + try-with-resources lease; **virtual threads: do not pool threads, bound the scarce resource** | virtual threads (444), `Semaphore`, `AutoCloseable` |
| DI as creation | factories and singletons everywhere | one composition root builds the object graph; test root with fakes; `Clock` injected | records, lambdas |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m03.examples`. Every demo prints deterministic output
(concurrency demos print only order-independent facts, such as the maximum number of leases in use).

Task **M03-2a** (#22) — Builder:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `builder.classic` — `Pizza` (extends `MenuItem`) with nested `Builder` | `PizzaDemo` | pizzeria | Classic Builder: required size in `builder(size)`, optional toppings/crust/extra cheese, validation in `build()`; the price is computed *before* `super(name, price)` (JEP 513) | builds with defaults; max 5 toppings; duplicate topping rejected; price per size and topping; built pizza is immutable |
| `builder.classic` — `HttpRequest` with `newBuilder(uri)` | `HttpRequestDemo` | HTTP client | Builder for a request object (mirrors `java.net.http.HttpRequest`): method, headers, body, timeout | GET/DELETE with a body rejected; header names validated; headers kept in insertion order and immutable; default method GET and timeout 30 s |
| `builder.record` — `record ServerConfig` with nested `Builder` and `withPort`, `withTls` | `ServerConfigDemo` | server configuration | Record + builder with defaults; validation in the compact constructor (one place); `with`-style copies return new records | defaults applied; invalid port rejected by builder *and* by `withPort`; `withX` leaves the original unchanged |
| `builder.step` — `Query` with step interfaces `SelectStep` → `FromStep` → `QueryStep` | `QueryDemo` | SQL-like query | Step builder: the type after each call only offers the legal next calls; `build()` exists only once `from` was called | rendered SQL text for select/from/where/orderBy/limit; `where` optional and combinable with `AND`; `SelectStep` has no `build` method (reflection check); limit must be positive |

Task **M03-2b** (#23) — Prototype, Object Pool, composition root:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `prototype.documents` — `DocumentTemplate` (mutable sections + metadata) with `clone()` **and** a copy constructor | `DocumentPrototypeDemo` | document templates | Shallow copy (`super.clone()`) shares the section list; the copy constructor copies deeply | test **documents** the shallow-copy bug (editing the clone changes the template); copy constructor gives an independent copy |
| `prototype.registry` — `Unit` (`copy()`), `Soldier`, `Archer`, `Stats` and `Position` (records); `UnitRegistry` | `UnitRegistryDemo` | strategy game | Prototype registry: spawn configured units by name; immutable `record` stats are shared, mutable position is copied | spawned units are independent; registry prototype unchanged after moving a spawned unit; unknown name rejected with known names |
| `pool` — `Connection`, `FakeConnection` (record), `ConnectionPool` (bounded, `Semaphore`), `PooledConnection` (`AutoCloseable` lease) | `ConnectionPoolDemo` | database access | Object Pool: create at most N expensive objects, reuse them, time out when exhausted; try-with-resources returns the lease | never more than N leases in use under 200 virtual threads; connections are reused (only N created); acquire times out when exhausted; double release impossible |
| `pool.throttle` — `ThrottledClient` (a `Semaphore`, no pool) | `ThrottleDemo` | calling a rate-limited service | With virtual threads, *threads* are cheap: do not pool them — bound the scarce resource instead | at most K concurrent calls with 1 000 virtual threads; all calls complete |
| `di` — `CheckoutService`, `PriceCalculator`, `PaymentGateway`, `OrderRepository` (+ `CatalogPriceCalculator`, `SimulatedPaymentGateway`, `InMemoryOrderRepository`), `OrderRecord`, `ShopApp`, `CompositionRoot` (`production()`, `forTests(...)`) | `CompositionRootDemo` | shop checkout | Construction by hand-written DI: one place creates and wires the object graph; no class calls `new` on a collaborator | production root builds a working graph; test root with fakes gives predictable results (fixed clock, recording gateway); each collaborator created once |

## Assignments

### ex01 — Travel booking builder

- **Goal:** build an always-valid immutable object with required and optional parts; report *all* problems at once.
- **Given (do not modify):** record `Booking(String traveller, String from, String to, LocalDate departure,
  Optional<LocalDate> returnDate, int passengers, CabinClass cabin, Set<String> extras)`; `enum CabinClass
  { ECONOMY, BUSINESS, FIRST }`; interface `BookingBuilder` (fluent `traveller`, `from`, `to`, `departure`,
  `returnDate`, `passengers`, `cabin`, `extra` — each returns `BookingBuilder` — and `Booking build()`).
- **Rules:** traveller, from, to and departure are required; defaults: one-way, 1 passenger, `ECONOMY`, no extras;
  passengers 1–9; `from` ≠ `to` (case-insensitive); return date after departure. `build()` throws
  `IllegalStateException` whose message lists **every** violated rule. The built booking's extras are immutable and
  independent of the builder.
- **Student writes:** `DefaultBookingBuilder implements BookingBuilder`.
- **Acceptance criteria (contract tests):** `buildsWithDefaults`, `buildsAReturnTrip`,
  `missingRequiredFieldsAreAllReported`, `rejectsSameOriginAndDestination`, `rejectsReturnBeforeDeparture`,
  `rejectsPassengersOutsideOneToNine`, `extrasAreImmutableAndIndependentOfTheBuilder`,
  `builderCanBuildSeveralIndependentBookings`, `rejectsNullArguments`.

### ex02 — Shape editor with prototypes

- **Goal:** implement deep copies for a small composite object graph and a template registry.
- **Given (do not modify):** record `Point(int x, int y)`; interface `Shape` (`Point position()`,
  `void moveBy(int dx, int dy)`, `Shape copy()`, `String describe()`).
- **Student writes:** mutable `Circle(Point, int radius)`, `Rect(Point, int width, int height)`,
  `Group(List<Shape>)` (position = its first child's; `moveBy` moves all children; `copy()` is **deep**), and
  `TemplateRegistry` (`register(String, Shape)`, `create(String)` returns a fresh copy, `names()` sorted).
- **Acceptance criteria (contract tests):** `copyDescribesTheSameShape`, `copyIsANewObject`,
  `movingACopyLeavesTheOriginal`, `groupCopyIsDeep`, `nestedGroupsAreCopiedDeeply`,
  `registryReturnsFreshCopies`, `registeredTemplateIsNotAffectedByChangesToCreatedShapes`,
  `registeringCopiesTheTemplate`, `unknownTemplateRejected`, `namesAreSorted`.

## Quiz topics

Telescoping constructors vs. builder; where validation belongs (builder vs. compact constructor); what a step builder
guarantees at compile time; why records rarely need builders for ≤ 3 components; shallow vs. deep copy; why
`Cloneable`/`clone()` is considered broken (no constructor, `final` fields, checked exception, cast); why immutable
objects can be shared instead of copied; what a pool buys and costs (stale state, leaks, contention); why virtual
threads should not be pooled; what a composition root is and how it differs from a Singleton.

## Out of scope

Annotation-processor builders (Lombok, `@RecordBuilder`); serialization-based deep copy; thread pools as an Object
Pool example (explicitly discouraged for virtual threads); DI frameworks and scopes (m11); connection-pool libraries
(HikariCP) beyond a mention.

## Decisions (owner, 2026-09-29)

1. Concurrency tests assert only order-independent facts (maximum leases in use, number of connections created) with
   hundreds of virtual threads; one acquire-timeout test uses a real 50 ms timeout.
2. "DI as creation" gets one example; m11 goes deeper.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m03-creational-construction verify` green
- [ ] Lesson EN + TR + PDFs; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
