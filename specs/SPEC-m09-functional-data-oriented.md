# Spec: m09-functional-data-oriented — Functional & Data-Oriented Patterns

> Status: **APPROVED** (owner, 2026-09-30) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 11 · Task: #51

## Objective

m02–m08 taught the GoF catalogue, and m06–m08 already showed single patterns shrinking to a lambda or a `switch`.
m09 steps back and teaches the *style* behind those shortcuts. Data-oriented programming (DOP) models the domain as
immutable records and sealed alternatives, keeps operations as plain functions over that data, validates once at the
boundary, and makes illegal states impossible to build. Errors become values (`Optional` for "maybe absent", a
sealed `Result<T, E>` for "succeeded or failed, and why"). Behaviour is built by composing small functions, and
expensive work is deferred and memoised. After this module a student can model a domain so the compiler checks
the cases, choose between exceptions, `Optional` and `Result`, compose and partially apply functions with
`java.util.function`, and look at a GoF pattern and say which Java 27 feature now does its job and when the full
pattern is still worth writing. The examples reuse the capstone's PatternShop order domain, so students can apply
this style to their capstone right away.

## Learning outcomes

After this module a student can:

1. **Model** a domain with data-oriented programming: records for data, sealed interfaces for alternatives,
   invariants in compact constructors, operations as exhaustive `switch` functions (record patterns, `_`, no
   `default`). They can **refactor** a mutable "status string + nullable fields" class into a model where illegal
   states cannot be expressed.
2. **Implement** deeply immutable values (defensive `List.copyOf`, withers, normalised `BigDecimal` money, `java.time`
   ranges), and **explain** view vs. copy, why a mutable hash key is a bug, and why a record is only as immutable as
   its components.
3. **Decide** between exceptions, `Optional` and a sealed `Result<T, E>`. They can **use** `Optional` correctly (as a
   return type only, and **explain** why), and **implement** `Result` with `map`/`flatMap`/`mapError`/`fold` to build
   a fail-fast ("railway") pipeline, comparing it with `CompletableFuture`'s `thenApply`/`thenCompose`/`exceptionally`.
4. **Compose** behaviour from functions (`andThen`/`compose`, `Predicate` combinators, `reduce` over a list of
   functions), and **apply** currying and partial application.
5. **Implement** lazy evaluation and memoisation (a memoising `Supplier`/`Function`, lazy log messages, stream
   laziness and short-circuiting, `Gatherers.scan`/`fold`), and **explain** why a recursive `HashMap.computeIfAbsent`
   is a bug.
6. **Compare** GoF patterns with the language features that replaced them (Strategy, Command, Template Method,
   Visitor, Iterator, Factory, Decorator, Singleton). For each pair they can **decide** whether the classic form is
   still justified: it is when it has state, needs several methods, needs a name, or needs to be extended from outside
   the module.

## Prerequisites

m08 (sealed types + pattern matching instead of Visitor; State as sealed records), m07 (fail-fast vs. collect-all
validation chains; Memento records), m06 (Strategy as lambdas, Template Method as a higher-order function, Command as
data, Stream Gatherers), m03 (records with compact constructors, withers), m00 (records, sealed types, `switch`
patterns, lambdas, streams).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Data-oriented programming (veri odaklı programlama) | mutable entity class with a status field, nullable fields and setters; behaviour spread across `if (status.equals(...))`, or a Visitor | records = data, sealed interfaces = alternatives, compact constructors = invariants; operations as static functions with exhaustive `switch` over record patterns; "parse, don't validate" at the boundary | records (395), sealed (409), record patterns (440), `switch` patterns (441), `_` (456) |
| Immutability (değişmezlik) & value objects | getters without setters, `Collections.unmodifiableList` wrappers, `clone()` | records with `List.copyOf` in the compact constructor; withers; normalised `Money`; `java.time` as the JDK's immutable value types | records, `List.copyOf` (10), `java.time` (8) |
| `Optional` and `Result` (errors as values) | `null` returns, checked exceptions, error codes | `Optional` **as a return type only** (`map`/`flatMap`/`or`/`orElseGet`/`stream`); generic sealed `Result<T, E>` (Ok/Err) with `map`/`flatMap`/`mapError`/`fold`; sealed error types handled exhaustively; `CompletableFuture` as the JDK's asynchronous result type | generic sealed records, record patterns with inferred type arguments, `switch` patterns, `Collectors.teeing` |
| Function composition, currying, partial application | Decorator classes wrapping each other; long parameter lists | `Function.andThen`/`compose`, `Predicate.and`/`or`/`negate`/`not`, `BiFunction.andThen`, `reduce(Function.identity(), Function::andThen)`; `Function<A, Function<B, C>>` | lambdas, method refs, `java.util.function` |
| Lazy evaluation & memoisation | Virtual Proxy (m04), lazy holder (m02), hand-written caches | memoising `Supplier`/`Function`; `Supplier<String>` log messages (`System.Logger.log(Level, Supplier)`); lazy, short-circuiting streams; `Gatherers.scan`/`fold`. **Sidebar only:** Lazy Constants (JEP 531, preview, `java.lang.LazyConstant.of(Supplier)`) | lambdas, `ConcurrentHashMap.computeIfAbsent`, virtual threads (444, for the concurrency test), Stream Gatherers (485) |
| Patterns that became language features | Strategy, Command, Template Method, Visitor, Iterator, Factory, Decorator, Singleton as class hierarchies | lambdas / method refs, sealed records + `switch`, streams + gatherers, `Supplier`/constructor refs, `Function.andThen`, `enum` | all of the above. **Sidebar only:** primitive patterns (JEP 532, preview) |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m09.examples`. Every demo prints deterministic output and runs
with `java <File>.java`. Example packages import only the JDK, with one exception: `dop.order.modern`,
`result.checkout` and `result.jdk` use the shared `Result` from `result.core`. The multi-file source launcher finds it
in the same source tree (verified below), so those demos still run without a build. Money is kept as `long` cents or
a package-local record. No example uses a preview feature. Concurrency appears in only one test (`lazy.memo`, on
virtual threads), and that test asserts counts, never timing. Implementation order inside M09-2a: `result.core` first.

Task **M09-2a** (#52): data-oriented programming, immutability, `Optional` and `Result`.

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `dop.order.classic`: `MutableOrder` (fields `String status`, nullable `paymentRef`, `trackingCode`, `cancelReason`; setters), `LegacyOrderService` (`if (status.equals(...))` chains) | `OrderLifecycleDemo` | PatternShop order lifecycle | The "before" picture: the model accepts states that make no sense, and every operation re-checks the status string | the classic model accepts `"SHIPPED"` with a `null` tracking code; a typo status (`"SHIPED"`) is accepted silently and `LegacyOrderService.describe` falls through to `"unknown"`; an order can be both cancelled and have a tracking code |
| `dop.order.modern`: `sealed interface Order permits Draft, Placed, Paid, Shipped, Cancelled` (records sharing `OrderId id` and `List<OrderLine> lines`, each carrying only the fields valid in that state: `Paid(…, String paymentRef)`, `Shipped(…, String paymentRef, String trackingCode)`, `Cancelled(…, String reason, boolean refundDue)`); records `OrderId`, `OrderLine(String sku, int quantity, long unitPriceCents)`; `sealed interface TransitionError permits EmptyOrder, AlreadyShipped, AlreadyCancelled`; `OrderTransitions` (`place(Draft)`, `pay(Placed, String)`, `ship(Paid, String)`, `cancel(Order, String)`), `OrderViews` (`describe(Order)`, `totalCents(Order)`) | `OrderLifecycleDemo` | PatternShop order lifecycle | DOP: each state is its own record, so "shipped without tracking" cannot be built; transitions are functions typed by their input state (`ship` accepts only `Paid`); operations are exhaustive `switch`es with record patterns and no `default` | compact constructors reject a blank tracking code / payment reference and copy `lines` (`List.copyOf`); `place` of an empty draft → `Err(EmptyOrder)`; `ship`'s parameter type is `Paid` (reflection check, so shipping a `Placed` order does not compile); `cancel` of `Shipped` → `Err(AlreadyShipped)`, of `Cancelled` → `Err(AlreadyCancelled)`; cancelling `Paid` sets `refundDue = true` and cancelling `Draft`/`Placed` sets it `false`; `describe` gives exact text for each state; `totalCents` is the same in every state; `Order.class.getPermittedSubclasses()` lists exactly the five records |
| `dop.boundary`: value records `Sku` (`[A-Z]{3}-\d{4}`), `Quantity` (1..99), `Email` (trimmed, lower-cased with `Locale.ROOT`), `OrderLine(Sku, Quantity, long unitPriceCents)`; `sealed interface ImportedRow permits Accepted, Rejected` (`Accepted(int lineNo, OrderLine line)`, `Rejected(int lineNo, String reason)`); `CsvOrderImporter` (the boundary); `OrderLines.totalCents(List<OrderLine>)` (the core, with no validation code) | `ParseDontValidateDemo` | PatternShop bulk-order CSV import | "Parse, don't validate": invalid input is turned into typed data **once**, at the edge. Constructors enforce invariants, and the importer converts their exceptions into `Rejected` rows, so the core only ever receives valid values | exact `Accepted`/`Rejected` rows (with line numbers and reasons) for a fixed 6-line CSV; value constructors throw `IllegalArgumentException` naming the field and the bad value; `new Email("  Ali@Example.COM ")` equals `new Email("ali@example.com")`; the importer never throws for bad rows (only for `null` input); `totalCents` of the accepted lines is exact |
| `immutability.cart`: `LeakyCart` (record holding the caller's `List`), `Cart` (record: `List.copyOf` in the compact constructor; withers `withLine`, `withoutSku`, `withQuantity(String sku, int qty)`), `CartLine` (record) | `DefensiveCopyDemo` | PatternShop shopping cart | A record is only as immutable as its components: the leak, the fix, and "change = new value" with withers. View (`Collections.unmodifiableList`) vs. copy (`List.copyOf`) | changing the source list after construction changes `LeakyCart` but not `Cart`; `LeakyCart.lines()` returns the caller's own list object; `Cart.lines().add(...)` throws `UnsupportedOperationException`; a `null` line → `NullPointerException` from `List.copyOf`; each wither returns a new `Cart` and leaves the original unchanged; `withQuantity(sku, 0)` removes the line; an unmodifiable *view* shows later changes to its source while a copy does not |
| `immutability.values`: `Money(BigDecimal amount, Currency currency)` (scale normalised to `currency.getDefaultFractionDigits()` with `HALF_EVEN`; `plus`, `times(int)`, `percent(int)`), `DateRange(LocalDate start, LocalDate endInclusive)` (`days()`, `overlaps`, `withEnd`, `shiftedBy(Period)`), `MutableKeyPitfall` (a mutable class used as a `HashSet` key) | `ValueObjectsDemo` | prices and promotion periods | Value objects: equality by value, normalised representation, operations that return new values; `java.time` as the JDK's immutable value types; the mutable-key bug that records prevent | `Money.of("2.0", "EUR")` equals `Money.of("2.00", "EUR")` even though the raw `BigDecimal`s are not `equals`; `JPY` amounts have scale 0; `2.345 EUR` rounds to `2.34` and `2.355 EUR` to `2.36` (`HALF_EVEN`); adding different currencies throws; `DateRange` rejects `end < start`; `days()` is inclusive; `overlaps` covers touching ranges; `withEnd`/`shiftedBy` leave the original unchanged; after a key is mutated, `HashSet.contains(key)` is `false` while the set still holds it |
| `optional.directory`: `Customer` (record; `sealed interface Referral permits Direct, ReferredBy` instead of a nullable/`Optional` field; `Optional<CustomerId> referrer()` convenience method), `CustomerDirectory` (`Optional<Customer> findByEmail`, `Optional<Customer> findById`, `List<OrderSummary> ordersOf(CustomerId)`) | `OptionalDoneRightDemo` | PatternShop customers and referrals | `Optional` as a return type only: chaining `map`/`flatMap`/`filter`/`or`, `orElseGet` vs. `orElse`, `orElseThrow` with a message, `Optional::stream` inside `flatMap`; "no results" is an empty list, never an `Optional<List>`; absence *inside* data is modelled with a sealed type | found / not found chains give exact names; the referral chain `findByEmail → referrer → findById → name` falls back to `"nobody"`; `orElse(expensive())` calls the supplier even when a value is present while `orElseGet` does not (call counter); `ordersOf` an unknown customer is an empty list; `Optional` is not `Serializable`; no public method of the package takes an `Optional` parameter and no record component or field has type `Optional` (reflection check that enforces the course rule) |
| `result.core`: `sealed interface Result<T, E> permits Ok, Err` (records; `null` value/error rejected), methods `map`, `mapError`, `flatMap`, `fold`, `orElse`, `orElseGet(Function<? super E, ? extends T>)`, `toOptional`, `static attempt(Callable<T>, Function<? super Exception, ? extends E>)`; `Results.sequence(List<Result<T, E>>)` → `Result<List<T>, E>`, `Results.partition(...)` → record `Partitioned<T, E>(List<T> oks, List<E> errors)` (via `Collectors.teeing`) | `ResultBasicsDemo` | parsing quantities | A minimal, honest `Result`: two tracks, functions applied only on the right track, exhaustive handling with `fold` or a `switch` over `Ok(var v)` / `Err(var e)`; the "railway" picture | `map`/`flatMap` never call their function on an `Err` and `mapError` never calls its function on an `Ok` (call counters); `fold` picks the right branch; the monad laws (left identity, right identity, associativity) hold for a fixed table of values and functions; `new Ok<>(null)` / `new Err<>(null)` throw `NullPointerException`; `attempt` turns a thrown checked exception into `Err`; `sequence` returns the first `Err` or `Ok` of all values in order; `partition` keeps input order in both lists; `toOptional` of an `Err` is empty |
| `result.checkout`: the same checkout three ways: `ExceptionCheckout` (throws `CheckoutException` subclasses), `OptionalCheckout` (returns `Optional<Receipt>`), `ResultCheckout` (returns `Result<Receipt, CheckoutError>` with `sealed interface CheckoutError permits EmptyCart, OutOfStock, InvalidCoupon, PaymentDeclined`); fakes `InMemoryInventory` (reserve/release, call log) and `ScriptedPaymentGateway`; `CheckoutErrors.message(CheckoutError)` | `CheckoutStylesDemo` | PatternShop checkout | Choosing an error model: exceptions (hidden control flow, not in the signature), `Optional` (loses *why*), `Result` (the failure is in the type, each step is a `flatMap`, and the caller must handle every error kind) | for a shared scenario table (parameterised test), the exception version and the `Result` version agree on the receipt or the error kind; the `Optional` version is empty for every failure; after the first failure no later step runs (the payment gateway is not called when stock is short); a declined payment releases the reserved stock (inventory log asserted); `CheckoutErrors.message` gives exact text for each error with an exhaustive `switch` and no `default` |
| `result.jdk`: `JdkResultShapes` (small functions over `Optional`, `Stream` and `CompletableFuture`), `FutureResults.toResult(CompletableFuture<T>)` → `Result<T, Throwable>` (unwraps `CompletionException`) | `JdkResultShapesDemo` | price lookups (already-completed futures) | The same `map`/`flatMap` shape across the JDK: `Optional.map`/`flatMap`, `Stream.map`/`flatMap`, `CompletableFuture.thenApply`/`thenCompose`; `exceptionally`/`handle` as recover/fold; `CompletableFuture` as an asynchronous `Result` | `completedFuture(2).thenApply(×10).thenCompose(+1)` joins to `21`; `thenCompose` on a failed future never calls its function; `exceptionally` directly on `failedFuture(e)` receives `e`, but after a `thenApply` it receives a `CompletionException` wrapping `e`; `toResult` gives `Ok` for a success and `Err` holding the *unwrapped* cause for a failure; `flatMap(Optional::stream)` drops empties while keeping order |

Task **M09-2b** (#53): composition, laziness, and "patterns that became language features".

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `composition.pricing`: record `Price(long cents)`; `PriceRules` (`percentOff(int)`, `amountOff(long)`, `addTax(int percent)`, `floorAt(long)`) returning `Function<Price, Price>`; `PricePipeline.of(List<Function<Price, Price>>)` (`reduce(Function.identity(), Function::andThen)`); `Eligibility` (`Predicate<Customer>` combinators) | `PricePipelineDemo` | PatternShop pricing rules | Decorator without classes: behaviour stacked by composing functions; order matters; a list of rules folded into one function; eligibility rules combined with `and`/`or`/`negate`/`Predicate.not` | `a.andThen(b)` equals `b.compose(a)` on every sample; `amountOff` before `addTax` and after it give different exact prices; an empty pipeline is the identity; the pipeline applies rules in list order (trace); `floorAt` stops a price going below the floor; combined predicates give the expected eligibility for a fixed customer table |
| `composition.text`: `Slugifier` built from small `Function<String, String>` steps (`lowerCase(Locale)`, `transliterateTurkish`, `stripDiacritics` (NFD + remove `\p{M}`), `replaceNonAlphanumeric`, `collapseDashes`, `trimDashes`) | `SlugifierDemo` | product URL slugs for a Turkish shop | A text pipeline of tiny, separately testable functions; `compose` vs. `andThen`; why every case conversion names its `Locale` (the Turkish dotted/dotless I) | `"İstanbul'da Kış İndirimi"` → `"istanbulda-kis-indirimi"` with `Locale.forLanguageTag("tr")`; `"ISTANBUL".toLowerCase(tr)` is `"ıstanbul"` and the transliteration step turns `ı` into `i`; NFD stripping alone leaves `ı` unchanged (so the explicit mapping is needed); `"İ".toLowerCase(Locale.ROOT)` has length 2; each step has its own unit test; the slug never starts or ends with `-` |
| `composition.currying`: `Curry` (`curry(BiFunction)`, `uncurry(Function<A, Function<B, C>>)`, `partial(BiFunction, A)`, `flip(BiFunction)`); `ShippingRates` (`Function<Zone, Function<Integer, Long>>`: zone → weight in grams → cents); `LogFormat` (`level → component → message → line`) | `CurryingDemo` | shipping tariff; log lines | Currying and partial application: fix some arguments now and supply the rest later; a curried function is a factory of specialised functions (a "configured strategy" without a class) | `uncurry(curry(f))` gives the same results as `f` for a table of inputs; `partial(f, a).apply(b) == f.apply(a, b)`; `flip` swaps arguments; `ShippingRates.forZone(DOMESTIC)` is stored and reused as a `Function<Integer, Long>` with exact prices at the weight boundaries; `LogFormat.of(WARN).apply("cart")` formats lines exactly |
| `lazy.memo`: `Memoized.supplier(Supplier<T>)` (thread-safe, computes at most once *on success*, `null` result rejected), `Memoized.function(Function<K, V>)` (backed by `ConcurrentHashMap.computeIfAbsent`, non-recursive use only); `ExchangeRates` (an expensive loader with a call counter); `Fibonacci` (the broken recursive `HashMap.computeIfAbsent` version next to a correct iterative one) | `MemoizationDemo` | currency rates; Fibonacci | Lazy initialisation and memoisation as functions: the Virtual Proxy / lazy holder idea with no new class per use; the lesson sidebar shows the preview `LazyConstant.of(...)` (JEP 531) as the future JDK form, **not compiled in the module** | nothing is computed until the first `get()`; the supplier runs exactly once when 100 virtual threads call `get()` concurrently (counter = 1); an exception from the supplier is rethrown and **not cached** (the next `get()` retries); the memoised function calls the loader once per distinct key; the recursive `HashMap.computeIfAbsent` Fibonacci throws `ConcurrentModificationException` (the test documents the pitfall); the correct version gives `fib(90) = 2880067194370816120` |
| `lazy.streams`: `LazyTrace` (records the order of `peek`/`filter` calls), `LazyLog` (a tiny logger with `log(Level, Supplier<String>)` like `System.Logger`), `Ledger` (running balance with `Gatherers.scan`, closing balance with `Gatherers.fold`) | `LazyStreamsDemo` | order stream; account ledger | Streams are lazy and process element by element ("vertical" order); short-circuiting (`findFirst`, `limit` on `Stream.iterate`); deferring a costly log message; `scan` vs. `fold` | the trace for `of(1..5).peek.filter(even).peek.findFirst()` is exactly `[see 1, see 2, even 2]`; `Stream.iterate(1, x -> 2x).limit(5)` gives `[1, 2, 4, 8, 16]`; a disabled log level never invokes the message supplier; `scan` over `[+100, -30, +5]` gives `[100, 70, 75]` and `fold` gives `[75]`; on an empty stream `scan` gives `[]` but `fold` gives `[initial]` |
| `features.catalogue`: one file per row, each a final holder class with nested `Classic` and `Modern` types: `StrategyRow` (interface + classes → `Comparator`/lambda), `CommandRow` (command objects → `Runnable` / sealed records), `TemplateMethodRow` (abstract class → higher-order function), `VisitorRow` (visitor + `accept` → sealed records + `switch`), `IteratorRow` (hand-written `Iterator` → `Stream.iterate` + `Gatherers.windowFixed`), `FactoryRow` (factory class hierarchy → `Map<String, Supplier<Shape>>` with constructor refs), `DecoratorRow` (wrapper classes → `Function.andThen`), `SingletonRow` (lazy synchronized class → `enum`); `Catalogue` (the table data) | `CatalogueDemo` | small neutral domains (one per row) | The comparative "before / after" catalogue: for each pattern, the classic form, the Java feature that now does its job, and a "still use the class when…" note; printed as a table and quoted in the lesson | for every row, `Classic` and `Modern` produce the same output on the same fixed inputs (parameterised over the rows); the Visitor row's modern side adds a new operation without touching the data types; reflectively creating the enum singleton throws `IllegalArgumentException` ("Cannot reflectively create enum objects"); the factory map throws a clear exception for an unknown key; `Catalogue` has exactly 8 rows, and each row names its m0x lesson for the full treatment |
| `features.shop`: the same PatternShop "invoice run" written twice. `features.shop.classic`: `DiscountStrategy` classes, a `LineItemVisitor` (tax and weight), `abstract InvoiceFormatter` (Template Method), `PostAction` command objects. `features.shop.modern`: records + `sealed interface LineItem permits PhysicalItem, DigitalItem, GiftCard`, discount rules as `Function`s, `switch` functions, a formatter taking its steps as functions, and post-actions as `Runnable`s | `InvoiceRunDemo` (one in each subpackage) | PatternShop invoicing | The combined effect: a realistic slice written in "GoF-heavy" style and in "functional + DOP" style, with identical behaviour. It also marks where the modern version still keeps a named type (the `LineItem` hierarchy, `Invoice`) | both versions produce byte-identical invoice text for a fixed set of 4 orders; both run the post-actions in the same order; the modern package declares no abstract class and no method named `accept` (reflection check); the modern package declares fewer top-level types than the classic one (count asserted; the demo prints both counts) |

**JDK behaviour this spec relies on (verified 2026-09-30 on JDK 27+35 with `javac -Xlint:all -Werror` and the source
launcher, in scratch files outside the repo):**

- A generic sealed interface `Result<T, E> permits Ok, Err` with nested records `Ok<T, E>(T value)` and
  `Err<T, E>(E error)` compiles cleanly under `-Xlint:all -Werror`. Default methods on the interface can
  `switch (this)` with `case Ok<T, E>(var v)` / `case Err<T, E>(var e)` and no `default`, and the switch is exhaustive.
  Record patterns **infer their type arguments**: `case Ok(var v)`, `case Err(_)`, `r instanceof Ok(var v)` and
  guarded `case Ok(Integer v) when v > 1` all compile and work. `flatMap(Function<? super T, ? extends Result<?
  extends U, E>>)` needs a small private "narrow" helper that rebuilds the result; casting `Err<T, E>` to
  `Result<U, E>` would be an unchecked warning (a build failure under `-Werror`), so `map`/`flatMap` rebuild the `Err`
  with `new Err<>(e)`. The same file runs with `java Result.java`.
- **Generic exhaustiveness:** with `sealed interface Shape<T> permits Circle, Label`, `record Circle<T>(…) implements
  Shape<T>` and `record Label(…) implements Shape<String>`, a `switch` over a `Shape<Integer>` is exhaustive with
  only `case Circle<Integer> c`, because `Label` can never be a `Shape<Integer>`. The lesson shows this as an
  advanced note; no example relies on it.
- **Pitfall (inference):** `System.out.println(result.fold(v -> "ok:" + v, e -> "err:" + e))` does **not** compile:
  `println(char[])` and `println(String)` are both applicable, so `R` cannot be inferred. Assign the result to a
  `String` first. Demos follow this rule, and the lesson lists it under pitfalls.
- `Gatherers.scan(() -> 0, Integer::sum)` on `1..4` gives `[1, 3, 6, 10]` and on an empty stream `[]`.
  `Gatherers.fold(() -> 0, Integer::sum)` gives `[10]`, gives `[0]` on an empty stream, and gives `[5050]` on a
  parallel `1..100` stream.
- `List.copyOf(List.of(...))` returns the **same instance**. `List.copyOf` of an `ArrayList` or of a
  `Collections.unmodifiableList` view returns a new list. `List.copyOf` throws `NullPointerException` on a `null`
  element. An unmodifiable view reflects later changes to its source. A record whose component is a caller's
  `ArrayList` returns that same object from its accessor, so the record is not immutable. A record with an `int[]`
  component uses reference equality for it (`new Arr(new int[]{1}).equals(new Arr(new int[]{1}))` is `false`).
- `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` is `false`, and `compareTo` is `0`.
  `stripTrailingZeros()` of `100` prints `1E+2`, so `Money` normalises with `setScale(fractionDigits, HALF_EVEN)`
  instead. `setScale(2)` without a rounding mode throws `ArithmeticException` for `2.345`. With `HALF_EVEN`, `2.345`
  gives `2.34` and `2.355` gives `2.36`. Default fraction digits are EUR 2, USD 2, TRY 2, JPY 0.
- `Optional` does not implement `Serializable`. `Optional.of(x).orElse(expensive())` evaluates `expensive()`, and
  `orElseGet` does not. `Stream.flatMap(Optional::stream)` drops empties in order.
- A recursive `HashMap.computeIfAbsent` (memoised Fibonacci) throws `ConcurrentModificationException`. Iterative
  `fib(90)` is `2880067194370816120`.
- `CompletableFuture`: `thenCompose` on a failed future never calls its function. `exceptionally` directly on
  `failedFuture(e)` receives `e`, and after a `thenApply` it receives a `CompletionException` (cause `e`).
  `join()` throws `CompletionException` wrapping the cause. `handle` sees the raw exception on `failedFuture`.
- `Function.andThen`/`compose` order: `inc.andThen(x2).apply(3) == 8`, `inc.compose(x2).apply(3) == 7`.
  `UnaryOperator.andThen` is inherited from `Function` and returns a `Function`, so a pipeline of
  `UnaryOperator`s is folded with `reduce(Function.identity(), Function::andThen)` over `Function`s.
- Turkish casing: `"I".toLowerCase(tr)` is `"ı"` and `"i".toUpperCase(tr)` is `"İ"`. `"İ".toLowerCase(Locale.ROOT)`
  has length 2 (`i` + U+0307). `"İstanbul'da Kış İndirimi".toLowerCase(tr)` is `"istanbul'da kış indirimi"`.
  `Normalizer` NFD + removing `\p{M}` maps `ş ö ğ ü ç` to ASCII but leaves `ı` unchanged, because the dotless i does
  not decompose.
- `System.Logger` has `default void log(Level, Supplier<String>)`.
- The multi-file source launcher (JEP 458) resolves a class from a *sibling* package in the same source tree:
  `java p/dop/order/Demo.java` compiles and uses `p.core.Box` without a build.
- Reflective `Constructor.newInstance` on an enum throws `IllegalArgumentException("Cannot reflectively create enum
  objects")`.
- Preview features (not used in code): `java.lang.LazyConstant<T> extends Supplier<T>` with `static
  LazyConstant.of(Supplier)` exists in JDK 27 (JEP 531). A primitive pattern (`case int i when i >= 400`) fails
  without flags with "primitive patterns are a preview feature and are disabled by default" and runs with
  `java --enable-preview --source 27`.

## Assignments

### ex01 — From Visitor to data-oriented payroll

- **Goal:** replace a Visitor-based design with data-oriented code: convert legacy objects into sealed records at the
  boundary, then write every operation, including one the Visitor design made awkward, as an exhaustive `switch`
  function over immutable data.
- **Given (do not modify):** package `legacy`: `abstract LegacyEmployee` (getters, `<R> R accept(EmployeeVisitor<R>)`),
  `LegacySalaried`, `LegacyHourly`, `LegacyContractor`, `LegacyIntern`, `EmployeeVisitor<R>`, and working
  `MonthlyPayVisitor` and `BenefitsVisitor` (the behavioural oracle). The target model: `sealed interface Employee
  permits Salaried, Hourly, Contractor, Intern` with records `Salaried(String id, String name, long annualSalaryCents)`,
  `Hourly(String id, String name, long hourlyRateCents, int hoursThisMonth)`, `Contractor(String id, String name,
  long invoiceCents, boolean vatRegistered)`, `Intern(String id, String name, long stipendCents, boolean
  universityFunded)` (compact constructors reject blank ids/names and negative amounts); `enum Kind { SALARIED,
  HOURLY, CONTRACTOR, INTERN }`; record `PayrollSummary(long totalCents, Map<Kind, Long> totalByKind, List<String>
  highestPaidIds)`; interface `Payroll` (`Employee fromLegacy(LegacyEmployee)`, `long monthlyPayCents(Employee)`,
  `String benefits(Employee)`, `Kind kindOf(Employee)`, `Employee withRaise(Employee, int percent)`,
  `PayrollSummary summarize(List<Employee>)`).
- **Rules:** monthly pay: salaried `annual / 12` (integer division); hourly `rate × hours`, where hours above 160 are
  paid at `rate × 3 / 2` (integer division); contractor `invoice`, plus 20 % (`invoice × 120 / 100`) when
  VAT-registered; intern `stipend`, or `0` when university-funded. Benefits: salaried `"health, pension"`; hourly
  `"health"` when hours ≥ 80, otherwise `"none"`; contractor `"none"`; intern `"mentoring"`. `withRaise` returns a
  **new** record with the salary, rate, invoice or stipend multiplied by `(100 + percent) / 100` (integer
  arithmetic) and leaves the original unchanged; `percent` must be 0..100, otherwise `IllegalArgumentException`.
  `summarize`: `totalByKind` contains **every** `Kind` (0 for absent kinds) and is unmodifiable; `highestPaidIds` lists
  the ids of all employees with the maximum monthly pay, in input order; an empty list gives total 0 and no ids.
  `fromLegacy` maps every legacy subclass to the matching record. No class written by the student may implement
  `EmployeeVisitor` or call `accept`; the brief explains why, and the reference solution follows the rule.
  `null` arguments throw `NullPointerException`.
- **Student writes:** `DataOrientedPayroll implements Payroll`, using exhaustive `switch` expressions with record
  patterns and no `default`. The contract creates payrolls only through `Payroll newPayroll()`.
- **Acceptance criteria (contract tests):** `convertsEveryLegacyKind`, `monthlyPayMatchesLegacyVisitorForAllSamples`
  (parameterised over a fixed table, compared with `MonthlyPayVisitor`), `benefitsMatchLegacyVisitorForAllSamples`,
  `hourlyOvertimeIsPaidAtTimeAndAHalf`, `vatAddedOnlyForRegisteredContractors`, `universityFundedInternCostsNothing`,
  `kindOfClassifiesEveryVariant`, `withRaiseReturnsNewRecordAndLeavesOriginalUnchanged`,
  `withRaiseRejectsPercentOutOfRange`, `summaryTotalsByKindIncludeAbsentKinds`, `summaryListsAllHighestPaidInInputOrder`,
  `summaryOfEmptyListIsZero`, `summaryMapIsUnmodifiable`, `rejectsNullArguments`.

### ex02 — Railway-style sign-up validation with a sealed `Result`

- **Goal:** build a validation pipeline that **collects** all independent field errors, then runs dependent checks
  **fail-fast**, using a sealed `Result` instead of exceptions, and never calls an external service on invalid input.
- **Given (do not modify):** `sealed interface Result<T, E> permits Ok, Err` with the same API as
  `examples.result.core.Result` (a copy in the exercise package, so the contract depends only on GIVEN types);
  record `RawSignup(String username, String email, String age, String country, String referralCode)` (strings as
  typed into a form; any may be `null`); `enum Country { TR, DE, NL, US }`; `sealed interface Referral permits
  NoReferral, ReferredBy` (`ReferredBy(String code)`); record `Signup(String username, String email, int age, Country
  country, Referral referral)`; `sealed interface SignupError permits Missing, TooShort, InvalidFormat, OutOfRange,
  UnknownCountry, UsernameTaken, InvalidReferral` (records, each naming its `field` where it applies); interface
  `UserRegistry` (`boolean isTaken(String username)`, `boolean isValidReferral(String code)`); interface
  `SignupPipeline` (`Result<Signup, List<SignupError>> validate(RawSignup raw)`).
- **Rules:** *Stage 1 (collect-all, field order username → email → age → country → referral):* every field is
  trimmed; `null`/blank username, email, age or country → `Missing(field)`; username is 3..20 chars of `[a-z0-9_]`
  after lower-casing with `Locale.ROOT` (shorter → `TooShort("username", 3)`; longer than 20 or other characters → `InvalidFormat("username")`); email must
  match `local@domain.tld` and is lower-cased; age is a base-10 integer (`InvalidFormat` otherwise) in 13..120
  inclusive (`OutOfRange("age", 13, 120)`); country is a `Country` name, ignoring case (`UnknownCountry(value)`); a
  blank/`null` referral code → `NoReferral`, otherwise `ReferredBy(code upper-cased)`. If any field fails, the result is
  `Err` with **all** stage-1 errors in field order, and the registry is **not** called. *Stage 2 (fail-fast, only
  after stage 1 succeeds):* `isTaken(username)` → `Err([UsernameTaken])`; then, only for `ReferredBy`,
  `isValidReferral(code)` false → `Err([InvalidReferral(code)])`. Success → `Ok(Signup)`. The error list is
  unmodifiable. The pipeline never throws for bad input; a `null` `RawSignup` → `NullPointerException`.
- **Student writes:** `DefaultSignupPipeline implements SignupPipeline` with constructor
  `DefaultSignupPipeline(UserRegistry registry)`, plus field parsers of their choice, each returning
  `Result<X, SignupError>`. The contract creates pipelines only through `SignupPipeline newPipeline(UserRegistry
  registry)` and uses a counting fake registry.
- **Acceptance criteria (contract tests):** `validSignupProducesNormalisedValue`, `missingFieldsAreReported`,
  `allFieldErrorsAreCollectedInFieldOrder`, `usernameTooShortAndInvalidCharacters`, `emailIsTrimmedAndLowerCased`,
  `ageMustBeANumberInRange`, `ageBoundariesAreInclusive`, `countryIsCaseInsensitive`, `unknownCountryIsReported`,
  `blankReferralMeansNoReferral`, `registryNotCalledWhenFieldsInvalid`, `takenUsernameFailsFast` (the referral is not
  checked), `invalidReferralIsReported`, `errorListIsUnmodifiable`, `neverThrowsForBadInput`, `rejectsNullInput`.

## Quiz topics

The four DOP rules (model data as immutable data, sealed alternatives, validate at the boundary, make illegal states
unrepresentable); data vs. objects (when behaviour belongs with the data and when it belongs in functions); the
expression problem: adding a case vs. adding an operation with a Visitor and with sealed types + `switch`; why a
sealed `switch` should have no `default`; "parse, don't validate"; shallow vs. deep immutability of records; view
vs. copy (`unmodifiableList` vs. `List.copyOf`); withers; value equality and `BigDecimal` scale; why a mutable hash
key breaks a `HashSet`; `java.time` as immutable values; why `Optional` is for return types only (not
`Serializable`, extra allocation and a third state for fields, clumsy parameters); `orElse` vs. `orElseGet`;
`Optional<List>` vs. an empty list; exceptions vs. `Optional` vs. `Result` (what each makes visible in the signature);
`map` vs. `flatMap`; fail-fast (railway) vs. collect-all validation; `CompletableFuture` as a result type
(`thenApply`/`thenCompose`/`exceptionally`) and `CompletionException` wrapping; `andThen` vs. `compose`; currying vs.
partial application; why `toLowerCase()` without a `Locale` is a bug (Turkish I); lazy vs. eager evaluation and
short-circuiting streams; memoisation and why a recursive `HashMap.computeIfAbsent` fails; `scan` vs. `fold`; which
GoF patterns became language features, and when the classic class-based form is still the better choice.

## Out of scope

Functional libraries (Vavr, Functional Java, Arrow-style types) beyond a mention; a full `Validated`/applicative type
(ex02 collects errors in a plain `List`); persistent collections with structural sharing (lesson mention only);
monad theory beyond the three laws as tests; State and Interpreter as patterns (m08); Visitor mechanics and double
dispatch (m08; here only the before/after comparison); concurrency patterns, `StructuredTaskScope` and asynchronous
`CompletableFuture` pipelines (m10; here only already-completed futures); serialisation formats (JSON libraries);
preview features in code: Lazy Constants (JEP 531) and primitive patterns (JEP 532) appear only as lesson sidebars.

## Decisions (owner, 2026-09-30)

All questions below were answered **yes**: the recommended defaults apply (including any build or dependency change they describe).

1. **ex01 overlaps m08's "Visitor vs. sealed types".** Issue #55 asks for "Refactor a Visitor-based design to
   data-oriented programming", and m08 already teaches Visitor → `switch`. **Recommended default:** keep ex01 as
   specified, in a new domain (payroll) that m08 does not use. Its weight is on the DOP part: converting at the
   boundary (`fromLegacy`), immutable updates (`withRaise`), and an aggregate operation (`summarize`) that is awkward
   with a Visitor. The given legacy visitors act as the test oracle.
2. **One `Result` type, two copies.** Contracts may depend only on GIVEN types, so ex02 needs its own `Result` in
   the exercise package, identical in API to `examples.result.core.Result`. **Recommended default:** accept the copy
   (the brief says so). `Result` rejects `null` in both `Ok` and `Err`, and no example adds a `Validated` type:
   collecting errors is part of what ex02 teaches.
3. **Size of the "language features" catalogue.** `features.catalogue` has 8 rows, and 4 of them (Strategy, Template
   Method, Command, Iterator/Visitor) repeat m06/m08 in miniature. **Recommended default:** keep all 8. Each row is
   ≤ 40 lines per side, points to its m0x lesson for the full treatment, and adds only the "still use the class
   when…" judgement. The lesson's comparison table is generated from these compiled rows, not typed by hand.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m09-functional-data-oriented verify` green
- [ ] Lesson EN + TR + PDFs, with a class diagram for the sealed `Order` and `Result` hierarchies, a "railway" diagram for the checkout pipeline, the before/after catalogue table, and ⚠️ preview sidebars for Lazy Constants and primitive patterns; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
