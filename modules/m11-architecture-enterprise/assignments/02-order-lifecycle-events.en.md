# Assignment 02 — Order Lifecycle with Domain Events Dispatched After Commit

> Module: m11-architecture-enterprise · Difficulty: ★★★ · Estimated time: 3–4 h

## Goal

Make an aggregate **record** what happened instead of calling listeners, commit its state through a port, and
dispatch the recorded **domain events** only after the commit succeeded. You write three parts: the `Order`
aggregate (transitions and events), an in-process `EventDispatcher` (typed subscriptions, error handler, a queue for
events raised while dispatching) and the `OrderLifecycleService` use case that ties them together:
load → aggregate method → commit → dispatch.

## What you are given

- `exercises/ex02/OrderId.java` — record — **do not modify**
- `exercises/ex02/OrderStatus.java` — `PLACED`, `PAID`, `SHIPPED`, `CANCELLED` — **do not modify**
- `exercises/ex02/OrderEvent.java` — sealed: `OrderPlaced(id, totalCents)`, `OrderPaid(id)`, `OrderShipped(id)`,
  `OrderCancelled(id, reason)` (records) — **do not modify**
- `exercises/ex02/OrderSnapshot.java` — record `(id, status, totalCents)`: what the store keeps — **do not modify**
- `exercises/ex02/OrderStore.java` — outbound port `load(OrderId)`, `commit(OrderSnapshot)` (may throw) — **do not
  modify**
- `exercises/ex02/Subscription.java` and `exercises/ex02/OrderLifecycle.java` — **do not modify**
- `exercises/ex02/Order.java`, `EventDispatcher.java`, `OrderLifecycleService.java` — your code (`TODO(ex02)`
  markers; `Order` and `EventDispatcher` are a suggested shape you may change)

## Tasks

1. Transitions: `PLACED → PAID → SHIPPED` and `PLACED | PAID → CANCELLED`. Anything else (pay twice, ship an unpaid
   order, cancel a shipped one, …) throws `IllegalStateException` — nothing is committed and nothing is dispatched.
2. `place(totalCents)`: `totalCents ≤ 0` → `IllegalArgumentException`; otherwise take an id from the supplier, commit
   `PLACED`, dispatch `OrderPlaced`, return the id. Unknown ids → `NoSuchElementException` (`pay`, `ship`, `cancel`,
   `status`).
3. Every command is **load → aggregate method (records the event) → commit → dispatch**. If `commit` throws, the
   exception propagates, no event is dispatched and `status` is unchanged.
4. Handlers receive events of their type; a handler for `OrderEvent.class` receives all of them. Handlers run in
   subscription order. A throwing handler goes to the error handler; the other handlers still run and the commit
   stays.
5. A command issued from inside a handler is executed (and committed) at once, but its events are dispatched after
   the current event's handlers have finished. Every event is dispatched exactly once.
6. `Subscription.close()` stops delivery and is idempotent.
7. `null` arguments (ids, reasons, types, handlers, constructor arguments) → `NullPointerException`.

## Acceptance criteria

- [ ] `placeCommitsThenDispatchesOrderPlaced`
- [ ] `eventsAreDispatchedOnlyAfterCommit`
- [ ] `failedCommitDispatchesNothingAndKeepsState`
- [ ] `payThenShipFollowsLifecycle`
- [ ] `cannotPayTwice`
- [ ] `cannotShipUnpaidOrder`
- [ ] `cannotCancelShippedOrder`
- [ ] `illegalTransitionCommitsAndDispatchesNothing`
- [ ] `cancelCarriesReason`
- [ ] `unknownOrderIsRejected`
- [ ] `rejectsNonPositiveTotal`
- [ ] `typedSubscriberReceivesOnlyItsEventType`
- [ ] `supertypeSubscriberReceivesAllEvents`
- [ ] `handlersRunInSubscriptionOrder`
- [ ] `failingHandlerDoesNotStopOthersOrUndoCommit`
- [ ] `commandFromHandlerIsDispatchedAfterCurrentEvent`
- [ ] `closedSubscriptionReceivesNothing`
- [ ] `eachEventIsDispatchedOnce`
- [ ] `rejectsNullArguments`

The contract's fake `OrderStore` writes `"commit <id> <status>"` into the same log the test handlers write to, so the
tests can see whether a handler ran before or after the commit.

## Run the tests

```bash
./mvnw -pl modules/m11-architecture-enterprise test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — where do events live between "happened" and "dispatched"?</summary>

In the aggregate: a private list that `pay()`, `ship()`, … append to, and a `pullEvents()` that returns a copy and
clears the list. The service calls it only after `commit` returned normally — a thrown exception skips it.

</details>

<details><summary>Hint 2 — the re-entrant command</summary>

The handler calls `orders.ship(...)`, which commits and then calls `dispatch` again while the first dispatch is still
looping. Put events in an `ArrayDeque`, and if a dispatch is already running just enqueue and return — the outer
loop delivers them next (the same trick as m07's event bus).

</details>

<details><summary>Hint 3 — closing exactly one subscription</summary>

Wrap each registration in a small private record and remove it by identity (`h == registration`). Removing an
already-removed object does nothing — idempotence for free.

</details>

## Stretch goals (optional, not graded)

- Persist events in an outbox inside `commit` (state and events together) and dispatch them from a relay.
- Replace the `OrderStatus` checks with an exhaustive `switch` over the status (m08 State).
