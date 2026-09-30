# Spec: m10-concurrency-patterns — Concurrency Patterns

> Status: **APPROVED** (owner, 2026-09-30) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 12 · Task: #56

## Objective

Every pattern so far ran on one thread. m10 is about designs that stay correct when many things happen at once. It
starts from the model that virtual threads made cheap again, **one thread per task**, and then covers the patterns
that coordinate those tasks: handing work between threads through a bounded queue (Producer–Consumer), waiting until
a condition holds or refusing to act when it does not (Guarded Suspension and Balking), sharing data safely by never
changing it (Immutable Object), passing request context down a call chain without parameters or `ThreadLocal` leaks
(Scoped Values), and splitting one task into concurrent subtasks. That last part is taught twice: as a
`CompletableFuture` pipeline (final API) and as Structured Concurrency (`StructuredTaskScope`, **preview in JDK 27**,
isolated in one package). After this module a student can pick the right tool for a concurrent job, give concurrent
code a deadline and a failure policy, and write concurrency tests that are **deterministic**, meaning they never
depend on `sleep` or on luck.

## Learning outcomes

After this module a student can:

1. **Explain** why blocking I/O code on virtual threads scales with the thread-per-task model, **implement** it with
   `Executors.newVirtualThreadPerTaskExecutor()` / `Thread.ofVirtual()`, and **decide** when *not* to pool threads
   and instead limit the scarce resource (the m03 throttle, revisited, not repeated).
2. **Implement** Producer–Consumer over a bounded `BlockingQueue` with back-pressure and graceful shutdown (poison
   pill per consumer), and **implement** Guarded Suspension and Balking with `ReentrantLock`/`Condition` and with
   `synchronized`/`wait`/`notifyAll`. Always wait in a `while` loop, never with a bare `if`.
3. **Design** immutable value objects (records, `List.copyOf`, withers) and **publish** changing state safely through
   an `AtomicReference` holding immutable snapshots. **Explain** why immutable objects need no locks.
4. **Use** `ScopedValue` to carry request context, and **compare** it with `ThreadLocal` / `InheritableThreadLocal`
   (leaks on pooled threads, mutability, inheritance).
5. **Compose** asynchronous work with `CompletableFuture` (`thenCombine`, `thenCompose`, `exceptionally`, `allOf`,
   `orTimeout`, `completeOnTimeout`) on an injected executor, and **compare** it with Structured Concurrency
   (`StructuredTaskScope.open`, `fork`, `join`, joiners, cancellation on failure, timeouts). Point out what
   `CompletableFuture.cancel(true)` does *not* do.
6. **Test** concurrent code deterministically with latches, barriers, bounded waits, injected executors and thread
   factories, and invariant-based assertions instead of timing.

## Prerequisites

m09 (immutability, records, sealed result types), m07 (Observer/`Flow` and the idea of asynchronous delivery), m06
(Command objects as `Runnable`/`Callable`), m03 (Object Pool, `Semaphore` throttle with virtual threads), m00
(records, sealed types, `switch` pattern matching, lambdas). Basic `Thread`, `Runnable`, `synchronized` from a first
Java course.

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Thread-per-task (virtual threads) | one platform thread per request, or a fixed `ThreadPoolExecutor` that caps concurrency | a new virtual thread per task: `Executors.newVirtualThreadPerTaskExecutor()` in try-with-resources (`close()` waits), `Thread.ofVirtual().name("crawler-", 0).factory()`; blocking code stays simple; since JDK 24 (JEP 491) `synchronized` no longer pins the carrier | virtual threads (444), `ExecutorService` as `AutoCloseable` (19) |
| Producer–Consumer (Üretici–Tüketici) | shared `LinkedList` + `wait`/`notify` | bounded `ArrayBlockingQueue` (`put`/`take`, `offer` with timeout); queue items as a `sealed` message type so the poison pill is a type rather than a magic value; one consumer per virtual thread | sealed (409), records (395), `switch` patterns (441), `BlockingQueue` |
| Guarded Suspension & Balking | `synchronized` + `while (!condition) wait()` + `notifyAll()`; balking = "return immediately if the state is wrong" | `ReentrantLock` with named `Condition`s (`notFull`, `notEmpty`), timed `awaitNanos`, interruptible waits; balking with `tryLock` / `AtomicBoolean.compareAndSet` | `java.util.concurrent.locks`, virtual threads |
| Immutable Object | `final` class, `private final` fields, defensive copies, no setters | records with compact-constructor validation and `List.copyOf`; withers returning new instances; copy-on-write publication through `AtomicReference.updateAndGet` | records, `List.copyOf` (10), `AtomicReference` |
| Scoped Values | `ThreadLocal` / `InheritableThreadLocal` for per-request context | `ScopedValue.where(KEY, value).run/call(...)`: immutable, bounded lifetime, rebinding restores the outer value, no `remove()` to forget; inherited by `StructuredTaskScope` subtasks only | scoped values (506, final) |
| `CompletableFuture` pipelines | `Future.get()` blocking chains, callbacks | `supplyAsync(…, executor).thenCombine(…).thenCompose(…).exceptionally(…)`; fan-out with `allOf`; deadlines with `orTimeout` / `completeOnTimeout`; injected `Executor` (`Runnable::run` in tests) | lambdas, `CompletableFuture` (8/9), `Future.state()`/`resultNow()` (19) |
| Structured Concurrency ⚠️ **preview** | `invokeAll`, `ExecutorCompletionService`, manual cancellation | `try (var scope = StructuredTaskScope.open(joiner)) { fork…; join(); }`: subtasks cannot outlive the scope, failure or timeout cancels (interrupts) siblings, the owner-thread rule, joiners `allSuccessfulOrThrow`, `anySuccessfulOrThrow`, `awaitAllSuccessfulOrThrow` (default), `allUntil`, custom `Joiner` | JEP 533 (7th preview): **m10 only, package `structured` only** |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m10.examples`. Every demo prints deterministic output and runs
with `java <File>.java`. Demos in `structured` run with `java --enable-preview --source 27 <File>.java`. Concurrent
results are always printed in a defined order (input order, id order or sorted). Demos never print thread names,
timings or completion order. How each concurrent behaviour is tested is fixed in
[Deterministic concurrency tests](#deterministic-concurrency-tests) below. Object Pool and the `Semaphore` throttle
are **not** repeated. They belong to m03, and the lesson links back to them.

Task **M10-2a** (#57): virtual threads, Producer–Consumer, Guarded Suspension/Balking, Immutable Object.

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `threadpertask.crawler`: `Crawler` (injected `ExecutorService`, `maxDepth`), `WebClient` (functional: `Page fetch(String url) throws IOException`), `FakeWeb` (in-memory site map, blocking fetch with an injected latency `Duration`, per-URL `fetchCounts()`, `catalogue(int)` builds a binary-tree shop with back links to the home page and one dead link), records `Page(String url, List<String> links)` and `CrawlReport(SortedSet<String> visited, SortedMap<String,String> failures)` | `CrawlerDemo` | simulated web crawler | Thread-per-task: every fetch is a blocking call on its own virtual thread. The code reads like sequential code, and a concurrent `Set` de-duplicates URLs. The crawl goes level by level (`invokeAll` per depth), so a page's depth is its shortest link distance and the report is deterministic even for graphs with several paths to a page. Demo crawls a 200-page fake site with 50 ms latency per page and prints the sorted report | each reachable URL fetched exactly once, cycles included (fetch counter per URL = 1); pages deeper than `maxDepth` are not fetched; depth = shortest link distance; a failing URL is reported in `failures` and does not stop the crawl; `visited` is sorted and unmodifiable; fetches really run concurrently: a `WebClient` whose N child pages each wait on a `CountDownLatch(N)` barrier finishes (bounded 5 s) only if all N fetches were in flight together |
| `threadpertask.scaling`: `InFlightTracker` (current/peak counter), `BlockingJob` (waits on a shared gate, records itself in the tracker), `Workloads` (`runOnFixedPool(int threads, int tasks)`, `runThreadPerTask(int tasks)`, both returning `Workloads.Result(completed, peakInFlight)` and rethrowing a failed job via `Future.state()`/`exceptionNow()`), `ThreadKinds` (`describe(Thread.Builder)` → `Facts(name, virtual, daemon)`, `namedVirtual(prefix)`) | `ThreadPerTaskDemo` | 10 000 blocking "I/O" tasks | Why pooling threads caps throughput and why virtual threads remove the cap. Fixed pool of 100 platform threads: peak in-flight = 100. Virtual thread per task: peak in-flight = 10 000. The output is deterministic because tasks meet at a barrier instead of sleeping. The demo also prints `isVirtual()`/`isDaemon()` facts for both builders | fixed pool: peak in-flight equals the pool size and never exceeds it; thread-per-task: all 10 000 tasks are in flight at once (barrier of 10 000 releases; each job's wait is bounded at 5 s so a broken run fails inside the class's `@Timeout(10)`); a job whose gate never opens fails instead of hanging; leaving the executor's try-with-resources means every task completed (counter = tasks); virtual threads are daemon and `isVirtual()`; a named factory yields `crawler-0`, `crawler-1`, … in creation order |
| `producerconsumer.logs`: `LogIngestion(int consumers, BlockingQueue<LogMessage> queue, ThreadFactory)` and `withBoundedQueue(consumers, capacity)` (1 producer on the calling thread, N consumers, bounded `ArrayBlockingQueue`), `sealed interface LogMessage` with nested records `LogLine`, `EndOfStream`, `enum Level` (`ERROR`, `WARN`, `INFO`, `DEBUG`, `UNPARSEABLE`), record `LevelCounts(SortedMap<Level,Long>)` (always holds every level, so equal inputs give equal counts) | `LogIngestionDemo` | log-file ingestion | Canonical Producer–Consumer. The bounded queue gives back-pressure, each consumer gets its own poison pill (`EndOfStream`, a *type* instead of a magic string), consumers `switch` over the sealed message, and partial counts are merged after all consumers finish | every line counted exactly once (sum of counts = lines) for 1, 2 and 8 consumers; queue size observed by an instrumented queue never exceeds capacity (capacity 2 with 1 000 lines); every consumer terminates (one `EndOfStream` consumed per consumer); a malformed line is counted under `UNPARSEABLE`, not thrown; result identical to a sequential count of the same input |
| `producerconsumer.fulfilment`: `FulfilmentPipeline(capacity, pick, pack, ship, ThreadFactory)` (stages `pick → pack → ship`, each stage a consumer of one bounded queue and producer for the next; `submit`, `trySubmit(Order, Duration)`, `shutdownAndAwait(Duration)`, `shipments()`, `shutdownLog()`, `maxInFlight(capacity)`), `Stage` (functional), package-private `sealed interface Parcel` (order in transit or `EndOfOrders`), record `Order(long id, List<String> items)`, record `Shipment(long orderId, List<String> stageLog)` | `FulfilmentDemo` | warehouse order fulfilment | Multi-stage Producer–Consumer: shutdown propagates stage by stage (the pill is forwarded after the stage drains); a gated slow last stage makes the upstream `offer(…, timeout)` fail, which is back-pressure you can observe; results are sorted by order id | every order passes all stages in order (`stageLog = [pick, pack, ship]`); shutdown forwards exactly one pill per stage and all stage threads end; with the last stage gated, `trySubmit` returns `false` once all queues are full (deterministic: capacity 1 per queue, gate closed, 6 = 3 queues + 3 stages); `shutdownAndAwait(50 ms)` returns `false` while a stage is stalled and `true` once it moves; submit after shutdown → `IllegalStateException`; after the gate opens every submitted order ships; shipments sorted by id |
| `guarded.buffer`: `BoundedBuffer<T>` (interface: `put`, `take`, `poll(Duration)`, `size`, `capacity`), `LockConditionBuffer<T>` (`ReentrantLock` + `notFull`/`notEmpty`), `MonitorBuffer<T>` (classic `synchronized`/`wait`/`notifyAll`) | `BoundedBufferDemo` | hand-off between a sensor reader and a writer | Guarded Suspension, two ways. The guard is always a `while` loop, `Condition`s give separate wait sets for "not full" and "not empty", and waits are interruptible and can be timed. The two implementations share one abstract test | FIFO order; `take` on empty blocks: the taker thread reaches `WAITING` (bounded poll), then a `put` releases it with that element; `put` on full blocks likewise until a `take`; `poll(50 ms)` on empty returns `Optional.empty()`; interrupting a blocked `take` throws `InterruptedException` and loses no element; 8 producers × 8 consumers × 1 000 items through capacity 4: the multiset of taken items equals the multiset put |
| `guarded.gate`: `ReadinessGate` (states `STARTING → READY` or `FAILED`; `awaitReady(Duration)`; `ReentrantLock` + `Condition.signalAll`), `WarmingService` (`warmUp()` loads the cache then opens or fails the gate; `lookup(key, maxWait)` returns `Optional<String>`) | `ReadinessGateDemo` | service start-up (requests wait until caches are warm) | Guarded Suspension on a *state* rather than a buffer: callers suspend until the state becomes `READY`, and a failed start wakes all waiters with the failure instead of leaving them hanging | waiter blocked (reaches `WAITING`) until `markReady()`, then returns; 100 virtual-thread waiters all released by one `markReady()`; `markFailed(cause)` makes every waiter throw `IllegalStateException` with that cause; `awaitReady(50 ms)` while starting returns `false`; after `READY` `awaitReady` returns immediately; illegal transitions out of a final state (`READY → FAILED`, `READY → READY`, `FAILED → READY`) rejected; lookups wait for the warm-up, a failed warm-up fails them |
| `guarded.balking`: `AutoSavingDocument` (`edit`, `isDirty`, `save()` returns `SaveResult` = `SAVED`/`SKIPPED_CLEAN`/`SKIPPED_IN_PROGRESS`; dirty = latest version ≠ saved version), injected `DocumentStore` (`write(long version, String text)`) | `BalkingDemo` | editor auto-save | Balking: when the precondition fails (nothing changed, or a save is already running) the call returns at once instead of waiting. Implemented with `AtomicBoolean.compareAndSet` and a dirty flag | clean document → `SKIPPED_CLEAN`, store not called; second `save()` while the first is held inside a gated store → `SKIPPED_IN_PROGRESS` (deterministic via latch), store called once; edit during a save leaves the document dirty so the next save writes again; 100 concurrent `save()` calls on one dirty document write exactly once; a failing store leaves the document dirty and releases the in-progress flag |
| `immutable.order`: records `Money(long cents, Currency currency)` (`Money.of(cents, code)`), `OrderLine(String sku, int quantity, Money unitPrice)`, `Order(String id, List<OrderLine> lines)` (compact constructor: validation + `List.copyOf`, at least one line; withers `withLine`, `withoutSku`; `total()`), `MutableOrder` (the classic broken bean, not shared across threads in any test) | `ImmutableOrderDemo` | shop order shared across threads | Immutable Object. There is nothing to lock: 1 000 virtual threads read one `Order` while "updates" create new instances. The demo contrasts it with the classic JavaBean and its defensive copying burden | mutating the source list after construction does not change the order; `lines()` is unmodifiable; a wither returns a new instance and leaves the original equal to before; invalid values rejected in the compact constructor (negative quantity, blank sku, mixed currencies, empty order, negative amount); the mutable bean leaks its internal list (single-threaded test); 1 000 concurrent readers all observe `total() == sum of lines` |
| `immutable.config`: record `ConfigSnapshot(int version, int minConnections, int maxConnections, Map<String,String> flags)`, withers `withVersion`, `withPoolSize(min, max)`, `withFlag`; `LiveConfig` (`AtomicReference<ConfigSnapshot>`, `update(UnaryOperator<ConfigSnapshot>)`, `current()`) | `LiveConfigDemo` | hot-reloadable configuration | Safe publication by swapping immutable snapshots (copy-on-write). A reader gets either the old or the new snapshot and never a half-updated one. `updateAndGet` retries with a pure function, so no update is lost | 16 writers × 1 000 `update(v -> v.withVersion(v.version() + 1))` on virtual threads → final version = 16 000 (no lost update); concurrent readers never see `min > max`, nor a snapshot breaking the writers' `max == 2 × min` rule, while writers change both fields together; snapshot `flags` unmodifiable; update function that throws leaves the current snapshot unchanged |

Task **M10-2b** (#58): Scoped Values, `CompletableFuture`, Structured Concurrency (preview).

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `scopedvalue.request`: `RequestContext` (`static final ScopedValue<Principal> PRINCIPAL`, `ScopedValue<String> REQUEST_ID`), `RequestHandler` (binds both with `ScopedValue.where(…).where(…).call(…)`: generic `handle(requestId, principal, CallableOp<T, X>) throws X` and `placeOrder(requestId, principal, item)`), `OrderService` → `AuditLog` (read the context deep in the call chain, no parameters; `AuditLog.Entry(requestId, principal, action)`, entries sorted by request id with a stable sort), record `Principal(String name, Set<String> roles)` with constants `SYSTEM` and `ANONYMOUS` | `ScopedRequestDemo` | web request handling | Scoped values as implicit, immutable, bounded-lifetime context. A nested `where` temporarily rebinds (e.g. "run as system"), and the outer value comes back afterwards. The demo handles 3 requests on virtual threads and prints the audit log sorted by request id | inside the handler `PRINCIPAL.get()` is the bound principal; after `call` returns it is unbound (`isBound() == false`); nested rebinding restores the outer value; `get()` when unbound throws `NoSuchElementException` and `orElse` gives the default; 1 000 concurrent requests on virtual threads each see only their own request id (every audit entry's id matches its request, asserted as a set); checked exception from `call` propagates unchanged |
| `scopedvalue.threadlocal`: `ThreadLocalContext` (classic `ThreadLocal` with `set`/`remove`, held in an instance field so there is no mutable static state), `ContextLeaks` (`secondTaskSeesWithoutRemove`, `secondTaskSeesWithFinallyRemove`), `InheritanceFacts` | `ThreadLocalVsScopedValueDemo` | the same request context done three ways | Why `ScopedValue` replaces `ThreadLocal` for context. With a pooled thread a forgotten `remove()` leaks one request's user into the next; `InheritableThreadLocal` copies values into child threads; a `ScopedValue` cannot leak and is *not* seen by plain executor threads (only by structured subtasks, see `structured.context`) | single-thread pool: value set in task 1 without `remove()` is visible in task 2 (the leak, made deterministic by the 1-thread pool); with `try/finally remove()` task 2 sees `null`; `InheritableThreadLocal` value visible in a started child virtual thread, plain `ThreadLocal` not; `ScopedValue` bound in the parent is **unbound** in a `newVirtualThreadPerTaskExecutor` task and in a raw `Thread.ofVirtual()` thread |
| `future.checkout`: `CheckoutPipeline` (injected `Executor`; `CompletableFuture<Receipt> checkout(Cart)`), services `PriceService`, `StockService`, `PaymentService` (functional interfaces returning `CompletableFuture`), `InMemoryShop` (implements all three with `supplyAsync` on an injected `Executor`, counts payment calls), records `Cart`, `Receipt` (nested `enum Status { APPROVED, DECLINED }`) | `CheckoutPipelineDemo` | shop checkout | A `CompletableFuture` pipeline. `thenCombine` joins independent price and stock look-ups, `thenCompose` chains the dependent payment (no nested futures), `thenApply` builds the receipt and `exceptionally` maps failures to a declined receipt. With the executor `Runnable::run` the whole pipeline is synchronous and deterministic | happy path gives the expected receipt; the future is already done when `checkout` returns under `Runnable::run`; stock failure gives a declined receipt whose reason is the *unwrapped* cause (the pipeline strips `CompletionException`); payment is never called when stock fails (call counter); a payment failure also becomes a declined receipt; `join()` on a failed stage throws `CompletionException`, `get()` throws `ExecutionException` (both asserted); the same pipeline on a virtual-thread executor gives the same receipt |
| `future.quotes`: `QuoteFanOut(Executor)` (`CompletableFuture<List<Quote>> all(List<Supplier<Quote>>, Duration perQuoteDeadline, Quote fallback)`, `allOrTimeout(providers, deadline)`, `allStrict(providers)`), record `Quote(provider, premiumCents)`, `CancellationFacts` (`cancelCompletableFuture`, `cancelExecutorFuture` → `Outcome(cancelled, interrupted)`) | `QuoteFanOutDemo` | insurance quotes from several providers | Fan-out/fan-in with `allOf`. The results are read in *input* order (not completion order), `completeOnTimeout` substitutes a fallback and `orTimeout` fails with `TimeoutException`. It also shows the limits: `allOf` keeps waiting for the slow siblings after one fails, and `cancel(true)` does **not** interrupt the running task. That is the motivation for structured concurrency | results in input order when the later supplier finishes first (latch-ordered); a never-completing supplier is replaced by the fallback after a 50 ms deadline; `orTimeout` → `CompletionException` with a `TimeoutException` cause; `allOf` is not done while a sibling is pending even though another already failed, and completes exceptionally once the sibling completes; after `cancel(true)` `isCancelled()` is true but the task, released by a latch, finishes and records `interrupted == false`; for contrast, `cancel(true)` on an `ExecutorService` future does interrupt the task |
| `structured.travel` ⚠️ preview: `TripPlanner` (`StructuredTaskScope.open()` with the default joiner; `fork` flight, hotel and weather look-ups; `Subtask.get()` after `join()`), `TripPlannerWithDeadline` (`open(Joiner.allSuccessfulOrThrow(), cf -> cf.withTimeout(d))`), functional `Lookup<T>` (`T find(String destination) throws Exception`), record `Trip(Flight, Hotel, Forecast)` with nested records | `TripPlannerDemo` | travel booking | Structured fan-out/fan-in. Subtasks of *different* types are joined in one scope. If one fails, the scope is cancelled: its siblings are interrupted and `join()` throws `ExecutionException` with the cause. A timeout cancels the scope with `CancelledByTimeoutException`. When the `try` block ends, no subtask is still running | all succeed → `Trip` built from the three results; hotel fails → `join()` throws `ExecutionException` whose cause is the hotel exception, and the blocked flight subtask observed its interrupt *before* `close()` returned (the hotel look-up fails only after the flight look-up has signalled that it started, so the interrupt cannot miss a thread that never ran); the cancelled subtask's state is `UNAVAILABLE` (asserted on a scope opened in the test); deadline 50 ms with a subtask blocked forever → cause `CancelledByTimeoutException` (only the outcome is asserted); subtasks run on virtual threads |
| `structured.mirrors` ⚠️ preview: `MirrorDownloader` (`Joiner.anySuccessfulOrThrow()`; record `Mirror(name, Source)`, record `Download(mirror, content)`), `ScatterGather` (`gatherAll`: `Joiner.allSuccessfulOrThrow()` returns results in *fork* order; `gatherBestEffort(shards, timeout)`), `BestEffortJoiner<T>` (custom `Joiner<T, List<T>, RuntimeException>`: records subtasks in `onFork`, `result()`/`timeout()` return the successful ones in fork order, failures ignored) | `MirrorDownloadDemo` | downloading a file from mirrors / gathering search shards | Choosing a joiner is choosing a policy: "first success wins, cancel the rest", "all or nothing" and "best effort". A custom joiner is written with `onFork`/`onComplete`, `result()` and `timeout()` | first successful mirror wins and a blocked mirror is interrupted; all mirrors fail → `ExecutionException`; `allSuccessfulOrThrow` returns results in fork order although a later fork finished first (latch-ordered); best-effort joiner returns only the successes and, on a 50 ms timeout, the successes so far (the test forks the hanging shard only after the other subtasks report `SUCCESS`, so "so far" is deterministic); the winning mirror answers only after the blocked one has started; `anySuccessfulOrThrow` with no forks → `ExecutionException` caused by `NoSuchElementException` |
| `structured.context` ⚠️ preview: `ScopedFanOut` (`traceAll(traceId, services)`: binds a `ScopedValue`, then forks subtasks that read it), `ScopeRules` (owner-thread and lifecycle rules; each method returns the exception the API threw) | `StructuredContextDemo` | per-request tracing across subtasks | Scoped values are *inherited* by structured subtasks, while plain executor threads do not get them (contrast with `scopedvalue.threadlocal`). The demo also shows the API's lifecycle rules | every forked subtask sees the parent's binding; `fork` from a non-owner thread → `WrongThreadException`; `fork` after `join` → `IllegalStateException`; `Subtask.get()` before `join` → `IllegalStateException`; closing a scope with forks but without `join()` → `IllegalStateException` |

A plain JUnit test `PreviewIsolationTest` (M10-2b) reads the class-file header (minor version, bytes 4–5) of every
class under `target/classes`. It asserts that only classes in `…m10.examples.structured` are marked as preview
(`0xFFFF`), so preview use cannot spread silently into other packages (see Open question 1).

### Structured Concurrency API (verified on JDK 27)

Facts below were checked on JDK 27+35 (2026-09-30) by compiling scratch programs outside the repo with
`javac --release 27 --enable-preview -Xlint:all -Xlint:-preview -Werror` and running them with the source launcher.
The examples and tests rely on them. JEP 533 (7th preview) changed the API compared with older tutorials:

1. `StructuredTaskScope<T, R, R_X extends Throwable>` is an **interface** with static factories `open()`,
   `open(Joiner)`, `open(UnaryOperator<Configuration>)`, `open(Joiner, UnaryOperator<Configuration>)`. There are no
   `ShutdownOnFailure`/`ShutdownOnSuccess` subclasses and no `throwIfFailed()`. `join()` throws `R_X` and
   `InterruptedException`.
2. `open()` uses `Joiner.awaitAllSuccessfulOrThrow()`: `join()` returns `null` and throws `ExecutionException`
   (cause = first failure). Results come from `Subtask.get()`, whose `state()` is `SUCCESS`, `FAILED` or
   `UNAVAILABLE`.
3. Joiners in JDK 27: `allSuccessfulOrThrow()` (returns `List<T>` in **fork order**, verified with a latch making the
   first fork finish last), `anySuccessfulOrThrow()`, `awaitAllSuccessfulOrThrow()`, `allUntil(Predicate<Subtask>)`
   (returns `List<Subtask<T>>`), plus overloads taking `Function<Throwable, R_X>` to throw another exception type
   (e.g. `CompletionException::new`). A custom `Joiner` implements `result()` and `timeout()`. `onFork`/`onComplete`
   are defaults, and returning `true` cancels the scope.
4. When a subtask fails under `allSuccessfulOrThrow`, the scope is cancelled (`isCancelled() == true`), a blocked
   sibling is **interrupted**, and `close()` waits for it. The cancelled sibling's state stays `UNAVAILABLE`.
   (A sibling whose thread has not started yet when the scope is cancelled may never run at all, so tests that
   assert the interrupt let the failing subtask wait until the blocked one has signalled that it started.)
5. `Configuration.withTimeout(Duration)`: on expiry the scope is cancelled and the built-in joiners make `join()`
   throw `ExecutionException` whose cause is `StructuredTaskScope.CancelledByTimeoutException` (a
   `RuntimeException`). Also available: `withThreadFactory`, `withName`. Subtasks run on virtual threads by default.
6. `anySuccessfulOrThrow` with zero forks: `ExecutionException` caused by `NoSuchElementException("No subtasks
   completed")`.
7. Owner rules: `fork` from another thread → `WrongThreadException`; `fork` after `join` → `IllegalStateException`;
   `Subtask.get()` before `join` or on a failed subtask → `IllegalStateException` (`exception()` gives the cause);
   `close()` after forking without `join()` → `IllegalStateException`.
8. A `ScopedValue` bound around the scope is visible in every forked subtask (`get()` works). It is **not** visible
   in tasks of `Executors.newVirtualThreadPerTaskExecutor()` or in raw `Thread.ofVirtual()` threads.

Preview mechanics (verified):

- `javac --release 27` without `--enable-preview` rejects the code: *"StructuredTaskScope is a preview API and is
  disabled by default."*
- With `--enable-preview -Xlint:all -Werror` the build **fails** (one `[preview]` warning per preview API use). Adding `-Xlint:-preview`
  turns them into a note and the build passes, while other lint warnings still fail the build (checked with a
  deliberate raw type).
- javac marks **only** the classes that use preview APIs as preview (class-file minor version `65535`). Other classes
  compiled in the same `--enable-preview` invocation keep minor version `0` and run with plain `java -cp …` without
  the flag. A marked class without the flag fails with `UnsupportedClassVersionError: Preview features are not
  enabled …`.
- Source launcher: `java …/plain/SomeDemo.java` works without flags even when a sibling package in the same source
  tree uses preview APIs, because the launcher compiles only the classes the demo references.
  `java …/structured/X.java` without the flag fails at compile time with the message above.
  `java --enable-preview --source 27 X.java` runs it (on JDK 27 `--enable-preview` alone also works; the lesson uses
  the documented form). Non-preview demos also still run when the flags are given.
  **Consequence:** a non-preview demo must never import from `structured`, or it will need the flags too.
  `PreviewIsolationTest` covers the compiled side, and the review checklist covers imports.
- Maven (scratch copy of the parent POM, JDK 27, Maven wrapper): the configuration in Open question 1 compiles
  main and test code, runs tests from both kinds of packages, keeps `-Werror` effective, and works with
  `-Pcoverage`. JaCoCo 0.8.15 instruments the preview-marked class (100 % line coverage reported) and prepends its
  agent to the `argLine` property.

### Deterministic concurrency tests

Rules for every m10 test (examples, contracts, solutions). They make the "no flaky tests" risk in
`tasks/plan.md` concrete:

1. **No `Thread.sleep` in tests and no sleep-based synchronisation.** Ordering is forced with `CountDownLatch`,
   `CyclicBarrier` or gate latches that the test opens. Demos that simulate I/O latency may sleep, but they print
   only order-independent facts.
2. **Every blocking wait in a test is bounded.** Use `latch.await(5, SECONDS)` with its result asserted true, and
   `future.get(5, SECONDS)`. Each concurrent test class also carries `@Timeout(10)` (JUnit, same-thread mode) as a
   safety net. `assertTimeoutPreemptively` is not used, because it runs the body on another thread, which breaks
   `ScopedValue` bindings and the scope-owner rule.
3. **"Thread X is now blocked"** is detected by polling `thread.getState()` until `WAITING`/`TIMED_WAITING`, with
   `Thread.onSpinWait()` and a 5 s bound. Verified on JDK 27: a virtual thread blocked on a latch, a `Condition`,
   `Object.wait()` or a full `ArrayBlockingQueue.put` reports `WAITING`. Only then does the test release it.
4. **Real timeouts only where the timeout is the behaviour under test**, and at most 50–100 ms. Tests assert the
   outcome (timed out, fallback used, `CancelledByTimeoutException`), never an elapsed-time upper bound below
   seconds. The work that must time out blocks on a latch that is never opened, so the timeout always fires.
5. **Assert invariants instead of interleavings:** exact counts, multisets, "each URL once", "peak ≤ N",
   "sum = total", "final version = writes", results sorted or in input/fork order.
6. **Injected concurrency:** components take an `Executor`/`ExecutorService`/`ThreadFactory` (tests use
   `Runnable::run` for synchronous pipelines and counting or named factories), and tests always close what they
   create (try-with-resources on executors and scopes).
7. **Concurrency is proven, not assumed:** "runs in parallel" is tested with a barrier sized to the task count that
   can only release if all tasks are in flight together.

Also verified on JDK 27 (final APIs): `ExecutorService.invokeAll` returns futures in task order, and
`invokeAll(tasks, 50 ms)` cancels the unfinished task **and interrupts** its virtual thread (`state() ==
CANCELLED`); `ExecutorCompletionService.take()` yields in completion order; 10 000 virtual threads each sleeping
100 ms complete in well under a second on a laptop (the demo prints no timings); `Thread.ofVirtual().name("crawler-",
0)` names threads `crawler-0`, `crawler-1`, …, virtual threads are daemon threads and unnamed by default (`""`);
`ArrayBlockingQueue.offer` on a full queue returns `false`; `CompletableFuture` with `Runnable::run` is complete when
`supplyAsync` returns; `exceptionally` on a `supplyAsync` stage receives a `CompletionException` wrapping the thrown
exception; `completeOnTimeout` substitutes the value; `orTimeout` → cause `java.util.concurrent.TimeoutException`;
`cancel(true)` on a running `CompletableFuture` does not interrupt the task; `ScopedValue` rebinding restores the
outer value, `get()` unbound → `NoSuchElementException`, and `call` propagates checked exceptions;
`InheritableThreadLocal` values reach child virtual threads while plain `ThreadLocal` values do not; with a
1-thread pool, a `ThreadLocal` set without `remove()` is visible to the next task.

## Assignments

Both graded assignments use **final APIs only**. Their starters and solutions must not import
`java.util.concurrent.StructuredTaskScope`, and `PreviewIsolationTest` also covers `exercises` and `solutions`.

### ex01 — Parallel price comparison with deadline and failure policy

- **Goal:** query several blocking price providers concurrently, give the whole comparison a deadline, cancel
  providers that miss it, and apply a failure policy. This is structured concurrency built by hand from final APIs.
- **Given (do not modify):** record `Quote(String provider, long priceCents)`; interface `PriceProvider`
  (`String name()`, `Quote quote(String sku) throws Exception`, may block); `sealed interface ProviderResult permits
  Priced, Failed, TimedOut` with records `Priced(Quote quote)`, `Failed(String provider, String reason)`,
  `TimedOut(String provider)`; record `Comparison(String sku, List<ProviderResult> results)` (compact constructor
  `List.copyOf`; `Optional<Quote> cheapest()`); `enum FailurePolicy { BEST_EFFORT, FAIL_FAST }`;
  `ComparisonFailedException(String provider, Throwable cause)` (unchecked); interface `PriceComparator`
  (`Comparison compare(String sku) throws InterruptedException`).
- **Rules:** all providers are called concurrently, each on its own thread from the injected `ExecutorService`.
  `results` has exactly one entry per provider, **in provider order** (not completion order). A provider that has
  not answered when the deadline expires (measured from the start of `compare`) becomes `TimedOut` and **must be
  cancelled** (its thread interrupted). `compare` returns soon after the deadline even if a provider never finishes.
  `BEST_EFFORT`: a provider exception becomes `Failed(provider, message)`. `FAIL_FAST`: the first provider exception
  cancels every provider still running and makes `compare` throw `ComparisonFailedException` naming that provider,
  **without waiting for the deadline**. `cheapest()` is the lowest `Priced` quote (ties go to the earlier provider),
  or empty. The comparator never shuts down the injected executor. A null or blank sku throws
  `IllegalArgumentException`. Hints in the brief: `invokeAll(tasks, timeout, unit)` for best-effort, and
  `ExecutorCompletionService` (or `CompletableFuture` + cancel of the underlying `Future`) for fail-fast. The brief
  also shows, *not graded*, how `StructuredTaskScope` expresses the same policy in a few lines once JEP 533 is final.
- **Student writes:** `ParallelPriceComparator implements PriceComparator` with constructor
  `ParallelPriceComparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy, ExecutorService
  executor)`.
- **Acceptance criteria (contract tests):** `returnsOneResultPerProviderInProviderOrder`,
  `providersAreCalledConcurrently` (barrier sized to the provider count, deadline 5 s),
  `cheapestPicksLowestPricedQuote`, `cheapestTieGoesToEarlierProvider`, `cheapestIsEmptyWhenNoQuoteSucceeded`,
  `bestEffortReportsFailedProviderAndKeepsOthers`, `slowProviderIsReportedAsTimedOut` (provider blocks on a latch
  that is never opened, deadline 100 ms), `slowProviderIsInterruptedAfterDeadline` (provider counts down a latch in
  its `catch (InterruptedException)`; test awaits it ≤ 5 s), `failFastThrowsNamingTheFailingProvider`,
  `failFastInterruptsRemainingProviders`, `failFastDoesNotWaitForTheDeadline` (deadline 30 s, `@Timeout(5)` on the
  test), `emptyProviderListGivesEmptyComparison`, `resultsListIsImmutable`, `doesNotShutDownTheInjectedExecutor`,
  `rejectsNullOrBlankSku`.

### ex02 — Bounded job queue with graceful shutdown

- **Goal:** build a Producer–Consumer work queue whose blocking behaviour is a set of Guarded Suspension and Balking
  rules: back-pressure when full, a drain-then-stop shutdown, an immediate stop, and released waiters.
- **Given (do not modify):** record `Job(long id, String payload)`; functional interface `JobHandler`
  (`String handle(Job job) throws Exception`); `sealed interface JobOutcome permits Completed, Failed` with records
  `Completed(long jobId, String result)` and `Failed(long jobId, String error)`; interface `JobQueue extends
  AutoCloseable` with `void submit(Job) throws InterruptedException`, `boolean trySubmit(Job)`, `void shutdown()`,
  `List<Job> shutdownNow()`, `boolean awaitTermination(Duration) throws InterruptedException`,
  `List<JobOutcome> outcomes()`, `void close()`.
- **Rules:** at most `capacity` jobs wait in the queue and at most `workers` jobs run at once. The queue creates
  exactly `workers` threads, all from the injected `ThreadFactory`. `submit` **blocks** while the queue is full.
  `trySubmit` **balks**: it returns `false` at once when the queue is full or shut down. A handler exception becomes
  `Failed(id, message)` and the worker continues. `shutdown()` stops accepting jobs, lets the workers finish every
  job already queued, and returns **without blocking** even when the queue is full. A thread blocked in `submit`
  when `shutdown()` is called is released with `IllegalStateException`. `submit` after shutdown throws
  `IllegalStateException`. `shutdownNow()` stops accepting jobs, interrupts running handlers and returns the queued
  jobs that never started, in submission order. `awaitTermination` returns `true` once every worker has exited, or
  `false` when the timeout elapses first. `outcomes()` is an immutable snapshot **sorted by job id**. `shutdown()`
  and `shutdownNow()` are idempotent. `close()` = `shutdown()` + wait for termination. Capacity or workers < 1 throws
  `IllegalArgumentException`. Hints in the brief: a `ReentrantLock` with `notFull`/`notEmpty` conditions and a
  `shutdown` flag checked in every `while` guard. A plain `ArrayBlockingQueue` with poison pills cannot release a
  blocked `submit` on shutdown, and the brief explains why.
- **Student writes:** `WorkerPoolQueue implements JobQueue` with constructor `WorkerPoolQueue(int capacity, int
  workers, JobHandler handler, ThreadFactory threadFactory)`.
- **Acceptance criteria (contract tests):** `processesEverySubmittedJobExactlyOnce`, `outcomesAreSortedByJobId`,
  `failingJobBecomesFailedOutcomeAndWorkerContinues`, `createsExactlyTheConfiguredNumberOfWorkers` (counting
  factory), `runsAtMostWorkersJobsConcurrently` (gated handler; peak in-flight = workers, never more),
  `trySubmitBalksWhenQueueIsFull`, `submitBlocksWhileQueueIsFullAndResumesWhenSpaceFrees` (submitter reaches
  `WAITING`, then the gate opens), `shutdownDrainsQueuedJobsBeforeTermination`, `shutdownDoesNotBlockWhenQueueIsFull`,
  `blockedSubmitterIsReleasedByShutdown`, `submitAfterShutdownIsRejected`, `trySubmitAfterShutdownReturnsFalse`,
  `shutdownNowReturnsJobsThatNeverStartedInSubmissionOrder`, `shutdownNowInterruptsRunningJobs`,
  `awaitTerminationReturnsFalseWhileJobsStillRun` (gated handler, 50 ms), `shutdownIsIdempotent`,
  `closeShutsDownAndAwaitsTermination`, `rejectsInvalidConstructorArguments`.

## Quiz topics

Thread-per-task vs. thread pools, and why virtual threads should not be pooled (link to m03); what "pinning" was and
what JEP 491 changed; back-pressure with a bounded queue vs. an unbounded queue that hides overload; poison pill per
consumer, and why the pill should be a type; `while` vs. `if` around `wait`/`await` (spurious wake-ups, stolen
conditions); `notify` vs. `notifyAll` and why `Condition`s help; Guarded Suspension vs. Balking; what makes an object
immutable (final fields, no leaking `this`, defensive copies, `List.copyOf`) and why immutables need no locks;
copy-on-write publication with `AtomicReference`; `ScopedValue` vs. `ThreadLocal` (lifetime, mutability, leaks,
inheritance); `thenApply` vs. `thenCompose`; where a `CompletionException` comes from; `allOf` vs. "first failure
cancels"; why `CompletableFuture.cancel(true)` does not stop the work; structured concurrency's guarantees (no
orphan subtasks, cancellation propagates, owner-thread rule); choosing a joiner; what "preview" means and how to run
preview code; how to test concurrent code without `sleep`.

## Out of scope

Reactive libraries and `Flow` (m07); the Java Memory Model beyond "happens-before via final fields, locks, volatile
and `java.util.concurrent` hand-offs"; lock-free data structures and `VarHandle`; `ForkJoinPool`/parallel streams for
CPU-bound work beyond a sidebar; actors and distributed messaging; `Semaphore` throttling and Object Pool (m03);
graded use of preview APIs; Lazy Constants (JEP 531, sidebar in m02/m09 only); performance benchmarking (JMH); the
capstone's optional structured-concurrency extension (specified in the capstone spec).

## Decisions (owner, 2026-09-30)

All questions below were answered **yes**: the recommended defaults apply (including any build or dependency change they describe).

1. **Preview build change for m10 (ask first, CLAUDE.md §9).** This is the concrete change in
   `modules/m10-concurrency-patterns/pom.xml` only (the parent POM, CI and other modules are untouched). It was
   verified on a scratch copy of the parent POM:
   ```xml
   <properties>
       <!-- JaCoCo's prepare-agent prepends its agent to this property, so -Pcoverage keeps working -->
       <argLine>--enable-preview</argLine>
   </properties>
   <build><plugins><plugin>
       <artifactId>maven-compiler-plugin</artifactId>
       <configuration>
           <enablePreview>true</enablePreview>
           <compilerArgs combine.children="append"><arg>-Xlint:-preview</arg></compilerArgs>
       </configuration>
   </plugin></plugins></build>
   ```
   Consequences: the whole module (examples, exercises, solutions, tests) compiles with `--enable-preview`, but only
   classes that actually use a preview API are marked. Non-preview demos keep running with plain
   `java <File>.java`, as long as they do not import from `structured`. `-Xlint:-preview` also hides an *accidental*
   preview use elsewhere, and `PreviewIsolationTest` catches that. Students running `-Pexercises` get
   `--enable-preview` automatically. When JEP 533 becomes final (or changes API again in JDK 28), only the
   `structured` package and these lines change. *Recommended default:* approve exactly this change, isolated in the
   m10 POM, together with `PreviewIsolationTest`.
2. **Assignment coverage.** ex01 grades thread-per-task, deadlines and cancellation ("structured concurrency by
   hand", final APIs), and ex02 grades Producer–Consumer + Guarded Suspension/Balking. Immutable Object, Scoped
   Values, `CompletableFuture` and Structured Concurrency are practised through the examples and the quiz only.
   *Recommended default:* accept. Scoped values and immutability are small enough that the examples' tests cover
   them, and graded preview code is not allowed.
3. **Real timeouts in tests.** Tests whose behaviour *is* a timeout (e.g. `CancelledByTimeoutException`,
   `completeOnTimeout`, `poll(50 ms)`, ex01's 100 ms deadline, ex02's `awaitTermination(50 ms)`) use a real 50–100 ms
   timeout against work blocked on a never-opened latch, so they can only pass for the right reason. Everything else
   uses latches, barriers and bounded waits, with `@Timeout(10)` as a safety net. *Recommended default:* accept; the
   same approach as m03's single 50 ms acquire-timeout test.

## Success criteria

- [ ] All examples run with `java <File>.java` (`structured` demos with `java --enable-preview --source 27 <File>.java`) and have tests; `./mvnw -q -pl modules/m10-concurrency-patterns verify` green on JDK 27, with no `sleep`-based test synchronisation
- [ ] `PreviewIsolationTest` proves preview-marked classes exist only in `…m10.examples.structured`
- [ ] Lesson EN + TR + PDFs with a ⚠️ preview banner on the Structured Concurrency section and its run command, sequence diagrams for Producer–Consumer and structured fan-out/cancellation; `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`); neither uses preview APIs
