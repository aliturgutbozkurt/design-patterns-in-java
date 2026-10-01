# Assignment 01 — Parallel Price Comparison

> Module: m10-concurrency-patterns · Difficulty: ★★★ · Estimated time: 3 h

## Goal

A shop compares the price of one product at several suppliers. Every supplier is a slow, blocking remote call that
may fail or never answer. Asking them one after another is too slow, and waiting for the slowest one is not
acceptable either. Build a comparator that asks **all providers at the same time**, gives the whole comparison a
**deadline**, **cancels** every provider it no longer needs, and applies a **failure policy**. This is thread-per-task
plus "structured concurrency by hand", built from final `java.util.concurrent` APIs only (no preview features).

## What you are given

- `exercises/ex01/Quote.java` — record `Quote(String provider, long priceCents)` — **do not modify**
- `exercises/ex01/PriceProvider.java` — `String name()`, `Quote quote(String sku) throws Exception` (blocking) —
  **do not modify**
- `exercises/ex01/ProviderResult.java` — sealed: `Priced(Quote)`, `Failed(String provider, String reason)`,
  `TimedOut(String provider)` — **do not modify**
- `exercises/ex01/Comparison.java` — record `Comparison(String sku, List<ProviderResult> results)` with
  `Optional<Quote> cheapest()` — **do not modify**
- `exercises/ex01/FailurePolicy.java` — `BEST_EFFORT`, `FAIL_FAST` — **do not modify**
- `exercises/ex01/ComparisonFailedException.java` — unchecked, `provider()` names the failing provider — **do not
  modify**
- `exercises/ex01/PriceComparator.java` — `Comparison compare(String sku) throws InterruptedException` — **do not
  modify**
- `exercises/ex01/ParallelPriceComparator.java` — your code goes here (`TODO(ex01)` markers); constructor
  `ParallelPriceComparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy, ExecutorService
  executor)`

## Tasks

1. Reject a `null` or blank sku with `IllegalArgumentException`.
2. Call **every provider concurrently**, each in its own task on the injected `ExecutorService`. Never shut that
   executor down: its owner decides its lifetime.
3. Return **exactly one result per provider, in provider order** (not in completion order).
4. The deadline is measured from the start of `compare`. A provider that has not answered when it expires becomes
   `TimedOut(provider)` and **must be cancelled**, which means its thread is interrupted. `compare` returns soon after
   the deadline even if a provider never finishes.
5. `BEST_EFFORT`: a provider exception becomes `Failed(provider, message)`, and the other results are kept.
6. `FAIL_FAST`: the first provider exception cancels every provider that is still running and makes `compare` throw
   `ComparisonFailedException` naming that provider (the exception is its cause), **without waiting for the
   deadline**.
7. `cheapest()` (given) returns the lowest `Priced` quote; on a tie the earlier provider wins.

## Acceptance criteria

- [ ] `returnsOneResultPerProviderInProviderOrder`
- [ ] `providersAreCalledConcurrently`
- [ ] `cheapestPicksLowestPricedQuote`
- [ ] `cheapestTieGoesToEarlierProvider`
- [ ] `cheapestIsEmptyWhenNoQuoteSucceeded`
- [ ] `bestEffortReportsFailedProviderAndKeepsOthers`
- [ ] `slowProviderIsReportedAsTimedOut`
- [ ] `slowProviderIsInterruptedAfterDeadline`
- [ ] `failFastThrowsNamingTheFailingProvider`
- [ ] `failFastInterruptsRemainingProviders`
- [ ] `failFastDoesNotWaitForTheDeadline`
- [ ] `emptyProviderListGivesEmptyComparison`
- [ ] `resultsListIsImmutable`
- [ ] `doesNotShutDownTheInjectedExecutor`
- [ ] `rejectsNullOrBlankSku`

## Run the tests

```bash
./mvnw -pl modules/m10-concurrency-patterns test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

The tests never sleep. A provider that "never answers" waits for a latch that is only opened after the test, so a
test can only pass if your code really cancels it or really stops waiting for it.

## Hints

<details><summary>Hint 1 — best effort with invokeAll</summary>

`ExecutorService.invokeAll(tasks, timeout, unit)` runs all tasks, waits at most `timeout`, **cancels with an
interrupt** every task that is not finished, and returns the futures **in task order**. Afterwards,
`future.state()` tells you `SUCCESS`, `FAILED` or `CANCELLED`, and `resultNow()` / `exceptionNow()` read the outcome
without blocking. Wrap each provider call in a task that turns the provider's exception into data, so that you can
still tell which provider failed.

</details>

<details><summary>Hint 2 — fail fast with a completion service</summary>

`invokeAll` always waits for every task (or the timeout), so it cannot fail fast. An `ExecutorCompletionService`
returns futures **in completion order**: `poll(timeLeft, NANOSECONDS)` gives you the next finished task or `null`
when the deadline has passed. Remember the index of each task so that you can put its result in the right place, and
cancel every future (`cancel(true)`) in a `finally` block. A `CompletableFuture` is not enough on its own:
`CompletableFuture.cancel(true)` does **not** interrupt the running task (see the lesson), so you would have to cancel
the underlying `Future` of the executor.

</details>

<details><summary>Hint 3 — interrupts are part of the contract</summary>

A task that catches `InterruptedException` must not swallow it. Either rethrow it or restore the flag with
`Thread.currentThread().interrupt()`. A provider that was cancelled is reported as `TimedOut`, not as `Failed`.

</details>

## Stretch goals (optional, not graded)

- Add a third policy, `FIRST_QUOTE`, that returns as soon as any provider answered and cancels the rest.
- Once Structured Concurrency (JEP 533, a **preview** API in JDK 27) is final, the whole assignment shrinks to a few
  lines. The sketch below is **not graded** and does not belong in your solution, because assignments use final APIs
  only:

```java
// snippet — preview API (JEP 533), compile and run with --enable-preview; not part of the assignment
try (var scope = StructuredTaskScope.open(Joiner.<Quote>allSuccessfulOrThrow(),
        config -> config.withTimeout(deadline))) {
    providers.forEach(provider -> scope.fork(() -> provider.quote(sku)));
    List<Quote> quotes = scope.join();   // fail fast: the first failure cancels (interrupts) the rest
}
```
