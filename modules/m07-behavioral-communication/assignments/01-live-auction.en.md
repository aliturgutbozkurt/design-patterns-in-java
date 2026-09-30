# Assignment 01 — Live Auction Notifications

> Module: m07-behavioral-communication · Difficulty: ★★☆ · Estimated time: 2–3 h

## Goal

An online auction house shows every bid live: bidders' screens, the auctioneer's dashboard and a fraud monitor all
react to the same events, but the auction must not know any of them. Build the **subject** of an Observer: listeners
subscribe for all events or for one event type, get an unsubscribe handle, and are notified under well-defined rules —
in subscription order, safe against (un)subscribing during a delivery and against listeners that throw.

## What you are given

- `exercises/ex01/Bid.java` — record `Bid(String bidder, long amountCents)` — **do not modify**
- `exercises/ex01/AuctionEvent.java` — sealed: `BidPlaced`, `BidRejected`, `Sold`, `Unsold` (records) — **do not modify**
- `exercises/ex01/Subscription.java` — `void close()`, usable in try-with-resources — **do not modify**
- `exercises/ex01/Auction.java` — `subscribe(listener)`, `subscribe(type, listener)`, `placeBid`, `close`,
  `highestBid` — **do not modify**
- `exercises/ex01/LiveAuction.java` — your code goes here (`TODO(ex01)` markers)

## Tasks

1. Constructor `LiveAuction(long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler)`;
   a `null` error handler throws `NullPointerException`.
2. `placeBid`: the first accepted bid must be ≥ the starting price, every later one ≥ highest + minimum increment.
   Otherwise publish `BidRejected` with reason `"below starting price"` / `"below minimum increment"`. After `close()`
   every bid is rejected with `"auction closed"`. An accepted bid publishes `BidPlaced` and becomes `highestBid()`.
3. `close()` publishes `Sold(highest)` or `Unsold()` exactly once; a second `close()` does nothing.
4. `subscribe(listener)` delivers every event; `subscribe(type, listener)` only events of that type. Listeners are
   notified in subscription order. `Subscription.close()` removes exactly that registration and is idempotent — even
   if the same listener was subscribed twice.
5. Subscribing or unsubscribing *during* a delivery takes effect from the next event.
6. A listener that throws does not stop the others; its exception goes to the error handler.
7. `null` bids, listeners or types throw `NullPointerException`.

## Acceptance criteria

- [ ] `acceptsFirstBidAtStartingPrice`
- [ ] `rejectsBidBelowStartingPrice`
- [ ] `rejectsBidBelowMinimumIncrement`
- [ ] `highestBidReflectsAcceptedBidsOnly`
- [ ] `listenersNotifiedInSubscriptionOrder`
- [ ] `typedSubscriptionReceivesOnlyItsEventType`
- [ ] `closedSubscriptionReceivesNothing`
- [ ] `closingASubscriptionTwiceIsHarmless`
- [ ] `unsubscribingDuringDeliveryTakesEffectFromTheNextEvent`
- [ ] `subscribingDuringDeliveryTakesEffectFromTheNextEvent`
- [ ] `failingListenerDoesNotStopOthers`
- [ ] `closeWithBidsPublishesSold`
- [ ] `closeWithoutBidsPublishesUnsold`
- [ ] `closeIsPublishedOnlyOnce`
- [ ] `bidsAfterCloseAreRejected`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m07-behavioral-communication test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — which list?</summary>

A `CopyOnWriteArrayList` iterates over a snapshot: listeners added or removed while you loop are only seen by the
next loop. That is exactly rule 5, with no extra code. (A plain `ArrayList` plus `List.copyOf(...)` before the loop
works too.)

</details>

<details><summary>Hint 2 — unsubscribing the right registration</summary>

`list.remove(listener)` removes the *first equal* element — the wrong one if the same lambda was subscribed twice.
Wrap every subscription in a small private object that is compared by identity and remove that object instead.

</details>

<details><summary>Hint 3 — typed subscriptions</summary>

A typed subscription is just a filtering listener: `event -> { if (type.isInstance(event)) listener.accept(type.cast(event)); }`.

</details>

## Stretch goals (optional, not graded)

- Make `LiveAuction` safe to use from several threads. Which state needs protection, and what about listeners that
  are slow?
- Replace the listener list with a `SubmissionPublisher<AuctionEvent>`. What do you gain (back-pressure, async
  delivery) and what do you lose (ordering guarantees, the error handler)?
