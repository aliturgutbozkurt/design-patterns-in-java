# Spec: m04-structural-wrappers — Structural I: Wrappers

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 5 · Task: #26

## Objective

m02 and m03 were about creating objects. m04 starts the structural patterns with the three that all look the same in
a class diagram — one object wrapping another behind an interface — but exist for different reasons: Adapter
*changes* an interface so incompatible code can work together, Decorator *adds* behaviour while keeping the
interface, and Proxy *controls access* to the real object (lazily, with permission checks, with a cache, or
generically through `java.lang.reflect.Proxy`). After this module a student can tell the three apart by intent,
implement each in classic and modern Java, stack decorators knowing that order matters, and read the wrappers the JDK
itself is built from (`java.io` streams, `Arrays.asList`, `Collections.unmodifiableList`, dynamic proxies).

## Learning outcomes

After this module a student can:

1. **Implement** an object adapter and a class adapter for a legacy API, **explain** why the object adapter is
   usually preferred, and **write** an adapter as a lambda when the target is a functional interface.
2. **Implement** Decorator in its classic form (abstract decorator base) and its modern form (records, `Function`
   composition), and **predict** how the order of stacked decorators changes the result.
3. **Read and build** `java.io` decorator chains (`BufferedReader(InputStreamReader(GZIPInputStream(…)))`) and
   **write** a custom `FilterInputStream`.
4. **Implement** virtual, protection and caching proxies, and a dynamic proxy with `java.lang.reflect.Proxy` that
   handles default methods and exceptions correctly.
5. **Distinguish** Adapter, Decorator and Proxy by intent, and **decide** when a wrapper is the wrong tool
   (identity/`equals` surprises, deep stacks, sealed interfaces that forbid wrapping).

## Prerequisites

m01 (composition over inheritance, OCP, DIP, programming to interfaces), m03 (injected `Clock`, composition root),
m00 (records, sealed types, lambdas, `switch` on enums).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Adapter (Adaptör) | object adapter (holds the adaptee) and class adapter (`extends Adaptee implements Target`) | record as an object adapter; a lambda / method reference *is* an adapter when the target is a functional interface; mapping legacy status codes to a sealed result with an exhaustive `switch`. **JDK:** `InputStreamReader` (bytes → chars), `Arrays.asList` (array → `List` view), `Enumeration.asIterator()` / `Collections.enumeration` | records (395), sealed (409), pattern matching for `switch` (441), lambdas |
| Decorator (Dekoratör) | component interface + abstract decorator holding a component; concrete decorators add behaviour before/after delegating | records as decorators (`record Milk(Beverage inner) implements Beverage`); cross-cutting decorators (logging, retry); functional decoration with `Function.andThen` / `compose` and `UnaryOperator`. **JDK:** `java.io` filter streams, readers and writers; `Collections.unmodifiableList` / `synchronizedList` | records, lambdas, `Function`, `FilterInputStream`, try-with-resources |
| Proxy (Vekil) | proxy implements the subject's interface and controls access to the real subject | virtual proxy with a thread-safe memoizing `Supplier`; protection proxy with an exhaustive `switch` on a role enum; caching proxy with an injected `java.time.Clock`; dynamic proxy (`java.lang.reflect.Proxy` + `InvocationHandler`, `InvocationHandler.invokeDefault`). **Sidebar:** Lazy Constants (JEP 531, preview — no graded code) for the virtual proxy. **JDK / frameworks:** `Collections.unmodifiableList` as a protection proxy, Spring AOP / JDK dynamic proxies, Hibernate lazy-loading proxies, RMI stubs | virtual threads (444, concurrency test), switch expressions (361), records, reflection |
| Adapter vs. Decorator vs. Proxy | three wrappers, one shape | same class diagram, three intents: *change* the interface / *add* behaviour / *control* access | — |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m04.examples`. Every demo prints deterministic output: no real
sleeping or wall-clock time — slow services are fakes that count calls, time comes from an injected `Clock` or a
fake nanosecond ticker, and concurrency demos print only order-independent facts (how many times something was
loaded). All payment, exchange-rate and image services are fictional and in-memory.

Task **M04-2a** (#27) — Adapter & Decorator:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `adapter.thermometer` — legacy `FahrenheitSensor` (`double readFahrenheit()`), target functional interface `CelsiusThermometer`; classic `FahrenheitAdapter` class | `ThermometerAdapterDemo` | smart home | Minimal canonical object adapter, then the same adapter as a lambda (`() -> toCelsius(sensor.readFahrenheit())`) because the target has one method | 212 °F → 100 °C, 32 °F → 0 °C, −40 °F → −40 °C; class and lambda adapters give identical readings; adapter reads the sensor on every call (no caching) |
| `adapter.payment` — legacy `LegacyPayGateway` (`int makePayment(String cardNumber, String amount, String currencyCode)` returning numeric status codes); target `PaymentProcessor` (`PaymentResult pay(PaymentRequest)`); `sealed interface PaymentResult permits Approved, Declined` (nested records) with `enum DeclineReason`; the translation both adapters share lives in the package-private `LegacyPaymentMapping`; object adapter `record LegacyPaymentAdapter(LegacyPayGateway gateway)`; class adapter `LegacyPaymentClassAdapter extends LegacyPayGateway implements PaymentProcessor` | `PaymentAdapterDemo` | online shop checkout | Realistic adapter: converts amounts in minor units (`long` kuruş/cents) to the legacy decimal string, and legacy status codes to a sealed result via an exhaustive `switch`; object vs. class adapter side by side | `1250` minor units → `"12.50"`; status `0` → `Approved` with the transaction id, `51` → `Declined(INSUFFICIENT_FUNDS)`, `54` → `Declined(CARD_EXPIRED)`, unknown code → `Declined(UNKNOWN)` with the code in the message; object and class adapters return equal results for the same requests; the class adapter *is a* `LegacyPayGateway` (its legacy methods leak to clients), the object adapter is not; non-positive amounts rejected before the gateway is called |
| `adapter.jdk` — no own types beyond the demo | `JdkAdaptersDemo` | JDK tour | Adapters the JDK already ships: `InputStreamReader` adapts bytes to chars, `Arrays.asList` adapts an array to a fixed-size `List` view, `Enumeration.asIterator()` / `Collections.enumeration` bridge the legacy and modern iteration interfaces | UTF-8 bytes of `"çğıİöşü"` (14 bytes) decode to 7 chars; `Arrays.asList(array).set(…)` writes through to the array; `add` throws `UnsupportedOperationException`; an `Enumeration` adapted to an `Iterator` yields the same elements in order |
| `decorator.coffee.classic` — `Beverage` (`String description()`, `long priceInKurus()`), `Espresso`, `HouseBlend`, `abstract CondimentDecorator`, `Milk`, `Syrup`, `ExtraShot` | `CoffeeDemo` | coffee shop | Minimal canonical GoF Decorator: every condiment wraps a `Beverage` and adds to description and price | price is the base plus every condiment (same condiment twice counts twice); description lists condiments in wrapping order (`"Espresso, Milk, Syrup"`); a decorated beverage is still a `Beverage` and can be wrapped again |
| `decorator.coffee.modern` — `Beverage`, `enum Coffee implements Beverage { ESPRESSO, HOUSE_BLEND }` (the base coffees) plus `record Milk(Beverage inner)`, `record Syrup(Beverage inner)`, `record ExtraShot(Beverage inner)` | `CoffeeDemo` | coffee shop | The same decorators as records: no abstract base, the wrapped component is a final component; records give `equals`/`toString` for free | same prices and descriptions as the classic version for the same order; two equal orders are `equals`; null `inner` rejected in the compact constructor |
| `decorator.resilience` — `StockService` (`int available(String sku)`), `FlakyStockService` (fake: fails the first *n* calls), `LoggingStockService(StockService, Consumer<String> log)`, `RetryingStockService(StockService, int maxAttempts)` | `ResilienceDemo` | warehouse inventory | Cross-cutting behaviour as decorators, composed in the composition root; **order matters**: `logging(retrying(x))` logs one call, `retrying(logging(x))` logs every attempt | retry succeeds when failures < `maxAttempts`; after `maxAttempts` failures the *last* exception is rethrown with earlier ones attached as suppressed; exact log lines for both stacking orders; `maxAttempts` < 1 rejected; retries immediately (no sleeping — backoff is out of scope) |
| `decorator.jdk` — `CountingInputStream extends FilterInputStream` | `JdkDecoratorsDemo` | log-file processing | `java.io` is built from decorators: `BufferedReader(InputStreamReader(GZIPInputStream(CountingInputStream(ByteArrayInputStream))))`; a custom `FilterInputStream`; `Collections.unmodifiableList` as a read-only *view* vs. `List.copyOf` as a *copy* | GZIP round trip through a writer stack and back through a reader stack gives the original lines; `CountingInputStream` counts bytes consumed by `read()`, `read(byte[], int, int)` **and** `skip` (it must override `skip`, see verified facts); closing the outermost reader closes the innermost stream; an unmodifiable view shows later changes to the backing list and rejects `add`, `List.copyOf` does not change |
| `decorator.functional` — `TextFilters` (static `UnaryOperator<String>` factories: `trim()`, `collapseSpaces()`, `censor(Set<String>)`, `truncate(int)`), `CommentPipeline` (`identity()`, `of(List<UnaryOperator<String>>)`, `then(…)`, `apply(String)`) | `FunctionalDecoratorDemo` | comment moderation | Decoration without classes: behaviour stacked with `Function.andThen` / `compose`; the pipeline is built once and reused | exact output for a fixed comment; `f.andThen(g)` ≠ `g.andThen(f)` when order matters (truncate-then-censor vs. censor-then-truncate); identity pipeline returns the input; `truncate` rejects a negative length |

Task **M04-2b** (#28) — Proxy:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `proxy.virtual` — `Image` (`String fileName()`, `String render()`), `HighResImage` (expensive: "loads" through an injected `ImageLoader` fake that counts loads), `LazyImage` (virtual proxy), `Lazy<T> implements Supplier<T>` (thread-safe memoizer) | `LazyImageDemo` | photo gallery | Virtual proxy: create the expensive object only when it is really needed, at most once; cheap questions (`fileName()`) are answered without loading | creating proxies loads nothing; `fileName()` does not load; the first `render()` loads exactly once and later calls reuse it; a gallery of 3 proxies where one is viewed loads 1 image; 1 000 virtual threads rendering the same proxy load it exactly once |
| `proxy.protection` — `DocumentStore` (`read`, `write`, `delete`), `InMemoryDocumentStore`, `record User(String name, Role role)`, `enum Role { VIEWER, EDITOR, ADMIN }`, `ProtectedDocumentStore(DocumentStore, User)`, `PermissionDeniedException` (unchecked) | `ProtectionProxyDemo` | document management | Protection proxy: the permission rule is an exhaustive `switch` on `Role`; the real store never sees a forbidden call | viewer may read only; editor may read and write; admin may also delete; a denied call throws `PermissionDeniedException` naming the user and the operation and **never reaches** the real store (spy counts calls); the proxy is usable wherever a `DocumentStore` is expected |
| `proxy.caching` — `ExchangeRateService` (`BigDecimal rate(String from, String to)`), `SlowExchangeRateService` (fake: fixed table, counts calls), `CachingExchangeRateService(ExchangeRateService, Duration ttl, Clock clock)`, `ManualClock extends Clock` (mutable, advanced by hand) | `CachingProxyDemo` | currency conversion | Caching proxy with a time-to-live; time comes from an injected `Clock`, so the demo and the tests control it | first call goes to the service, a repeat within the TTL does not; an entry is stale **exactly** at `storedAt + ttl` and is fetched again; different currency pairs are cached separately; non-positive TTL rejected |
| `proxy.dynamic` — `Proxies` (`static <T> T timed(Class<T>, T, LongSupplier nanoTicker, Consumer<String> log)`, `static <T> T readOnly(Class<T>, T)`), `TimingHandler`, `ReadOnlyHandler`, `@Mutator` (runtime annotation); demo interfaces `OrderRepository`, `PriceList` | `DynamicProxyDemo` | order service instrumentation | `java.lang.reflect.Proxy`: one handler adds behaviour to *any* interface at run time — the mechanism behind Spring AOP and mocking libraries. Timing proxy logs how long each call took; read-only proxy blocks methods annotated `@Mutator` | one log line per call with the method name and the elapsed time from the fake ticker (`"OrderRepository.findById took 3 ms"`); the same helper works for two unrelated interfaces; a target exception (checked or unchecked) reaches the caller unchanged, **not** as `UndeclaredThrowableException`; `default` interface methods work (via `InvocationHandler.invokeDefault`) and their inner calls go through the proxy again; `toString`/`equals`/`hashCode` are not timed; `@Mutator` methods throw `UnsupportedOperationException` and never reach the target; passing a class instead of an interface is rejected with `IllegalArgumentException`; `Proxy.isProxyClass` is true for the result |

**JDK behaviour this module relies on (verified 2026-09-29 on JDK 27, `java version "27" 2026-09-15`, source
launcher):**

- `java.lang.reflect.Proxy` works with the multi-file source launcher: a proxy for a **package-private** interface is
  generated in that interface's package (`demo.$Proxy0`); for a public interface it lands in a `jdk.proxyN` module
  package. Use `iface.getClassLoader()` as the class loader.
- If the handler rethrows `InvocationTargetException` instead of its cause, the caller receives
  `UndeclaredThrowableException`; the same happens when the handler throws a checked exception that the interface
  method does not declare. Handlers must unwrap with `throw e.getCause()`.
- `InvocationHandler.invokeDefault(proxy, method, args)` runs a `default` method; calls it makes to other interface
  methods go back through the proxy (the handler sees `twice, greet, greet`). `toString`, `hashCode` and `equals`
  are also dispatched to the handler.
- `Proxy.newProxyInstance` with a class (e.g. `ArrayList`) throws `IllegalArgumentException: java.util.ArrayList is
  not an interface`.
- `FilterInputStream.read(byte[])` delegates to `this.read(byte[], int, int)` (so a counting subclass need not
  override it), but `FilterInputStream.skip` delegates straight to the wrapped stream, bypassing the `read`
  overrides — `CountingInputStream` must override `skip`. `readAllBytes`/`transferTo` go through `read(byte[], int,
  int)`.
- Closing a `BufferedReader(InputStreamReader(GZIPInputStream(in)))` closes `in`.
- `GZIPOutputStream` output is byte-for-byte deterministic on JDK 27 (header mtime `0`, OS byte `255`); GZIP of an
  empty array is 20 bytes, while an *empty* input to `GZIPInputStream` throws `EOFException` and garbage throws
  `ZipException`. `Base64.getDecoder()` rejects invalid input with `IllegalArgumentException`.
- `Arrays.asList` writes through to the array on `set` and throws `UnsupportedOperationException` on `add`.
  `Collections.unmodifiableList` is a live view (shows later changes to the backing list) and does not re-wrap a
  list that is already unmodifiable (`unmodifiableList(ro) == ro`); `List.copyOf` is a snapshot.
- Turkish casing: `"İSTANBUL".toLowerCase(Locale.ROOT)` is 9 chars and not equal to `"istanbul"`, while
  `"İstanbul".equalsIgnoreCase("istanbul")` is `true`. Contract tests for case-insensitive keys therefore use ASCII
  city names; the ex02 brief mentions the trap as a hint.

## Assignments

### ex01 — Data-source decorators (compression + Base64)

- **Goal:** write stackable decorators that transform data on the way in and undo the transformation on the way out,
  and see that stacking order changes what is stored.
- **Given (do not modify):** interface `DataSource` (`void write(byte[] data)`, `byte[] read()`); final class
  `InMemoryDataSource implements DataSource` (the "file store": keeps a defensive copy of the last write; `read()`
  before any write returns an empty array).
- **Rules:** `write` transforms the data and passes it to the wrapped source; `read` reads from the wrapped source and
  reverses the transformation. `CompressionDecorator` uses GZIP (`java.util.zip`); `Base64Decorator` stores the
  standard Base64 text as US-ASCII bytes. Empty data stays empty in both directions (an empty array read from the
  wrapped source is returned as empty, not decoded). Corrupt data on `read` throws `IllegalStateException` with the
  original exception as its cause. Decorators hold no data of their own, so any decorator can wrap any `DataSource`,
  including another decorator of the same kind. Null arguments throw `NullPointerException`.
- **Student writes:** `abstract class DataSourceDecorator implements DataSource` (holds and delegates to the wrapped
  source), `CompressionDecorator`, `Base64Decorator`.
- **Acceptance criteria (contract tests):** `roundTripsThroughCompression`, `roundTripsThroughBase64`,
  `roundTripsThroughBothInEitherOrder`, `compressionShrinksRepetitiveData`, `base64StoresOnlyBase64Characters`,
  `outermostDecoratorTransformsFirst` (Base64 outside → the store holds Base64 text; compression outside → the store
  holds bytes starting with the GZIP magic `0x1f 0x8b`), `sameDecoratorCanBeStackedTwice`, `emptyDataRoundTrips`,
  `readingANeverWrittenSourceReturnsEmpty`, `corruptDataIsReportedAsIllegalState`,
  `decoratorsWorkWithAnyDataSource` (a test-only data source), `rejectsNullArguments`.

### ex02 — Caching proxy with TTL for a slow weather service

- **Goal:** put a caching proxy in front of a slow service without the client noticing, with expiry, invalidation,
  statistics and a size bound.
- **Given (do not modify):** record `Forecast(String city, int temperatureCelsius, String summary)`; interface
  `WeatherService` (`Forecast forecast(String city)`); interface `CachingWeatherService extends WeatherService`
  (`void invalidate(String city)`, `CacheStats stats()`); record `CacheStats(long hits, long misses)`.
- **Rules:** the constructor is `TtlCachingWeatherService(WeatherService target, Duration ttl, int maxEntries,
  Clock clock)`; `ttl` must be positive and `maxEntries` ≥ 1. Cache keys are the city trimmed and lower-cased with
  `Locale.ROOT`. An entry is fresh while `clock.instant()` is before `storedAt + ttl`; at exactly `storedAt + ttl` it
  is stale and is fetched again. A *hit* is a call answered from the cache; a *miss* is a call delegated to the
  target, whether it succeeds or fails. Exceptions from the target propagate unchanged and are **not** cached.
  `invalidate` removes one city (unknown city: no-op). When a new entry would exceed `maxEntries`, the least recently
  used entry (by hit or store) is evicted. A blank city throws `IllegalArgumentException` without calling the target.
- **Student writes:** `TtlCachingWeatherService implements CachingWeatherService`.
- **Acceptance criteria (contract tests, using a test-only counting weather service and a test-only mutable
  clock):** `firstCallGoesToTheService`, `repeatWithinTtlIsServedFromCache`, `entryExpiresExactlyAtTtl`,
  `cityKeysIgnoreCaseAndSurroundingSpaces`, `differentCitiesAreCachedSeparately`, `failuresAreNotCached`,
  `invalidateForcesARefresh`, `statsCountHitsAndMisses`, `leastRecentlyUsedEntryIsEvictedWhenFull`,
  `rejectsInvalidConfiguration`, `rejectsBlankCityWithoutCallingTheService`, `usableWhereAWeatherServiceIsExpected`.

## Quiz topics

Adapter vs. Decorator vs. Proxy given only the intent (same diagram, different reason); object adapter vs. class
adapter (single inheritance, leaking adaptee methods); when a lambda is enough to adapt; why decorator order matters
(logging around retry vs. retry around logging; compress-then-encode vs. encode-then-compress); which `java.io`
classes are decorators and which are "real" sources; why a decorator/proxy breaks identity (`==`, `equals`,
`getClass()`) and how records change `equals`; why a sealed interface cannot be decorated by third parties; the four
proxy kinds and one real use of each; what `InvocationHandler` receives and why `InvocationTargetException` must be
unwrapped; why JDK dynamic proxies need interfaces (and what CGLIB/ByteBuddy do instead); a view
(`unmodifiableList`) vs. a copy (`List.copyOf`).

## Out of scope

Bytecode-generating proxies (CGLIB, ByteBuddy) and AOP frameworks beyond a mention; retry back-off, circuit breakers
and resilience libraries (Resilience4j); caching libraries (Caffeine) and thread-safe caches (ex02 is
single-threaded; concurrency comes in m10); remote proxies / RMI beyond a mention; Bridge, Composite, Facade and
Flyweight (m05); Ports & Adapters as an architecture (m11 — m04 teaches the class-level Adapter); Lazy Constants in
graded code (preview, JEP 531).

## Decisions (owner, 2026-09-29)

All questions below were answered **yes**: the recommended defaults apply.

1. **Caching proxy example vs. ex02 overlap:** issue #28 asks for a TTL caching proxy example and issue #30 for a
   TTL caching assignment. *Recommended default:* keep both, but keep the example minimal (exchange rates: TTL only)
   and make ex02 go further (case-insensitive keys, failures not cached, `invalidate`, hit/miss stats, LRU bound),
   so the example teaches the idea without handing out the solution.
2. **Virtual proxy concurrency test:** `LazyImage` is made thread-safe through a small `Lazy<T>` memoizer and tested
   with 1 000 virtual threads asserting "loaded exactly once" (order-independent, same approach as the m03 decision).
   *Recommended default:* yes — it shows why a naive `if (real == null)` virtual proxy is a race; the lesson adds a
   Lazy Constants (JEP 531, preview) sidebar with no graded code.
3. **Class adapter:** Java's single inheritance makes the class adapter rare in practice. *Recommended default:* keep
   it in `adapter.payment` only, next to the object adapter, with a test that documents the leaked legacy methods —
   no other class-adapter example.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m04-structural-wrappers verify` green
- [ ] Lesson EN + TR + PDFs, with one class diagram per pattern plus an "Adapter vs. Decorator vs. Proxy" comparison and a sequence diagram of a dynamic-proxy call; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
