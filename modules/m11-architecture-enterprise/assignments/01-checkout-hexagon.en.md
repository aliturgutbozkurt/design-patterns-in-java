# Assignment 01 — PatternShop Checkout as Ports & Adapters

> Module: m11-architecture-enterprise · Difficulty: ★★★ · Estimated time: 3–4 h

## Goal

Build the core of the capstone: an application service that checks out a cart using **only ports** (interfaces the
core owns), plus one outbound adapter — an in-memory **Repository** of orders. Because the service never names an
adapter class, the same checkout runs with any catalogue, payment provider, store or event bus; an ArchUnit rule in
the contract proves it. Business refusals are values of a sealed result type, infrastructure failures stay
exceptions, and the order is saved strictly before the event is published.

## What you are given

- `exercises/ex01/Sku.java`, `Money.java` (non-negative cents; `plus`, `times`), `OrderId.java`, `CartItem.java`,
  `Product.java` (with `price` and `stock`), `OrderLine.java`, `Order.java` (its total must equal the sum of the
  lines), `OrderPlaced.java` — records — **do not modify**
- `exercises/ex01/CheckoutUseCase.java` — inbound port `CheckoutResult checkout(String customer, List<CartItem> cart)`
  — **do not modify**
- `exercises/ex01/CheckoutResult.java` — sealed: `Confirmed(Order)`, `Rejected(String reason)` — **do not modify**
- `exercises/ex01/ProductCatalog.java`, `PaymentPort.java`, `OrderRepository.java`, `EventPublisher.java`,
  `OrderIdGenerator.java` — outbound ports — **do not modify**
- `exercises/ex01/CheckoutService.java` — your application service (`TODO(ex01)` markers)
- `exercises/ex01/adapter/InMemoryOrderRepository.java` — your outbound adapter (`TODO(ex01)` markers)

## Tasks

1. `CheckoutService(ProductCatalog, PaymentPort, OrderRepository, EventPublisher, OrderIdGenerator)` rejects `null`
   collaborators with `NullPointerException`; `checkout` rejects a `null` customer or cart the same way.
2. Validate in this order — the first problem decides: an empty cart → `"empty cart"`; then, item by item in cart
   order, quantity ≤ 0 → `"invalid quantity: <sku>"` and an unknown SKU → `"unknown product: <sku>"`.
3. Merge duplicate SKUs into one line at the position of their first appearance (unit price from the catalogue), then
   check stock: stock < merged quantity → `"insufficient stock: <sku>"`.
4. Charge the total (Σ quantity × unit price) **exactly once**. Declined → `"payment declined"`.
5. Only for a confirmed order: take the next id, save the order, then publish exactly one `OrderPlaced` — save
   strictly before publish. If `save` throws, nothing is published and the exception propagates.
6. Return `Confirmed(order)`. A rejected checkout charges nothing (except the declined attempt), saves nothing,
   publishes nothing and consumes no id.
7. `InMemoryOrderRepository`: `save` replaces an order with the same id but keeps its position; `findByCustomer`
   returns the customer's orders in first-save order; returned lists are immutable snapshots; `count`.
8. `CheckoutService` must not depend on any class in an `adapter` package.

## Acceptance criteria

- [ ] `confirmsValidCartAndReturnsTheOrder`
- [ ] `totalIsSumOfLinePrices`
- [ ] `chargesTotalExactlyOnce`
- [ ] `savesBeforePublishing`
- [ ] `publishesExactlyOneOrderPlaced`
- [ ] `rejectsEmptyCart`
- [ ] `rejectsNonPositiveQuantity`
- [ ] `rejectsUnknownProduct`
- [ ] `rejectsInsufficientStock`
- [ ] `firstInvalidItemDecidesTheReason`
- [ ] `mergesDuplicateSkus`
- [ ] `validationFailureNeverCharges`
- [ ] `declinedPaymentSavesAndPublishesNothing`
- [ ] `idsAreConsumedOnlyByConfirmedOrders`
- [ ] `saveFailurePublishesNothingAndPropagates`
- [ ] `repositoryFindsSavedOrderById`
- [ ] `repositorySaveWithSameIdReplacesInPlace`
- [ ] `repositoryFindsByCustomerInSaveOrder`
- [ ] `repositoryListsAreImmutable`
- [ ] `serviceDoesNotDependOnAdapters`
- [ ] `rejectsNullArguments`

The contract uses hand-written test doubles, exactly like the lesson's `testdoubles.checkout`: a stub catalogue, a
mock payment port (amount and number of calls), a spy repository and a spy publisher that write into one shared call
log, and a sequence id generator.

## Run the tests

```bash
./mvnw -pl modules/m11-architecture-enterprise test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — merging while keeping the order</summary>

A `LinkedHashMap<Sku, Integer>` with `merge(sku, quantity, Integer::sum)` keeps the first position of every SKU and
adds up the quantities. Check quantity and catalogue membership in the same loop, before merging matters.

</details>

<details><summary>Hint 2 — "replace in place"</summary>

`LinkedHashMap.put` on an existing key replaces the value and keeps the key's original position — exactly the
repository rule. `stream().filter(...).toList()` returns an unmodifiable list.

</details>

<details><summary>Hint 3 — the architecture test passes, the others fail?</summary>

That is expected for the starter: the starter already depends on ports only. Keep it that way — the moment
`CheckoutService` imports `adapter.InMemoryOrderRepository` (for example to create it itself), the rule fails. The
composition root, not the service, decides which adapter is used.

</details>

## Stretch goals (optional, not graded)

- Add a `FileOrderRepository` adapter and run the same repository tests against it (see `repository.catalog` in the
  examples for a contract shared by two implementations).
- Add an `EventPublisher` adapter that writes to an outbox instead of publishing directly (lesson: transactional
  outbox).
