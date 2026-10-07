# Spec: capstone — PatternShop Capstone Project

> Status: **APPROVED** (owner, 2026-10-01; capstone scope approved per CLAUDE.md §9) · Parent: [SPEC.md](../SPEC.md) §2 row C,
> §9 criterion 6, §10 assumption 7 · Weeks: 9–14 · Tasks: C1–C7 (#66–#72)
>
> Student-facing documents written in C1: [capstone brief](../capstone/spec.en.md) ([TR](../capstone/spec.tr.md)) and
> [rubric](../capstone/rubric.en.md) ([TR](../capstone/rubric.tr.md)). **The business rules in brief §2.2 are
> normative** (students read them; the acceptance tests encode them). This file adds what the brief does not need to
> say: Maven layout, the GIVEN API, the acceptance-test catalogue, fixtures, output formats, ArchUnit rules, the
> reference design per slice and the C7 guide. If brief and this spec disagree, fix the brief and this spec in the
> same PR before touching code.

## Objective

m00–m11 taught patterns one module at a time, each inside a handful of classes. The capstone asks the student to
combine them in one small but complete system, **PatternShop**: an in-memory order-processing application with a
command-line interface (no UI, no database — SPEC.md §10.7, §12). The student writes their own `SPEC.md` first (SDD),
implements the mandatory features behind a GIVEN API so that a published acceptance suite can check them, keeps a
set of ArchUnit rules green (hexagonal architecture), adds two extension features of their own, and justifies at
least ten patterns in a bilingual report and a short presentation.

The course ships, per SPEC.md §9.6: the brief and rubric (EN + TR, C1), a starter with acceptance tests (C2), a
reference solution that passes them and uses ≥ 10 patterns (C3–C6), and a walkthrough guide (C7).

## Learning outcomes

After the capstone a student can:

1. **Write** a specification for a feature set before coding (objective, scope, acceptance criteria, boundaries,
   pattern plan) and **keep** it in sync with the code through a change log.
2. **Select** patterns for concrete forces in one domain, **justify** each choice against an alternative and
   **reject** patterns that add no value (m11 "patternitis").
3. **Implement** a hexagonal application (ports, adapters, composition root) whose dependency rules are enforced by
   executable architecture tests.
4. **Combine** classic GoF structures with modern Java 27 (records, sealed types, exhaustive `switch` with record
   patterns, lambdas, virtual threads).
5. **Test** a system at three levels: given acceptance tests, own unit tests with hand-written test doubles, own
   acceptance tests for the extension features.
6. **Present** and **defend** a design orally, in English or Turkish.

## Prerequisites (what the capstone reuses)

The capstone *applies* the modules; it does not repeat their examples. Bridges already built in the modules:

| Concern | Module example the student builds on |
|---|---|
| Value records, `Money`, static factories | m02 `staticfactory.Money`; m09 `immutability.values`, `dop.boundary` |
| Builder, composition root | m03 `builder.*`, `di` (`CompositionRoot.production()/forTests`) |
| Payment Adapter | m04 `adapter.payment` (status codes → sealed result); m11 `hexagonal.shop` `LegacyPaymentAdapter` |
| Decorator | m04 `decorator.coffee.modern`, `decorator.resilience` |
| Facade with compensation | m05 `facade.checkout` |
| Strategy, Command with undo | m06 `strategy.shipping.modern`, `command.spreadsheet.modern` (edit returns its inverse) |
| Observer / event bus, validation chain | m07 `observer.eventbus` (typed `subscribe`, queued re-entrant publish), `chain.validation` (collect-all) |
| State, sealed-type operations | m08 `state.order.sealed`, `visitor.cart.modern`, `interpreter.rules` (promotion eligibility) |
| Data-oriented order, pricing as functions | m09 `dop.order.modern`, `composition.pricing`, `features.shop` |
| Thread-per-task, bounded fan-out, fulfilment | m10 `threadpertask.*`, `producerconsumer.fulfilment`, `structured.*` (preview, optional only) |
| DI, Repository + Specification, hexagon, events after commit, ArchUnit | m11 `repository.catalog`, `hexagonal.shop`, `events.aggregate`, `architecture/*Test` |

## Scope

**Mandatory features** (brief §2.1; each has an acceptance suite below):

| Id | Feature | Acceptance suite(s) |
|---|---|---|
| F1 | Catalogue: add, find, search (Specification), restock; physical and digital products | `CatalogueAcceptance` |
| F2 | Cart: open, add (merge), change quantity, remove, coupon | `CartAcceptance` |
| F3 | Undo / redo of cart edits (depth 20, per cart) | `UndoAcceptance` |
| F4 | Pricing & promotions: buy-X-get-Y, category %, amount-off-over-threshold, coupon, shipping — fixed order | `PricingAcceptance` |
| F5 | Checkout with a validation chain (collect-all, fixed rule order) | `CheckoutAcceptance` |
| F6 | Payment through a fake external API (awkward third-party shape → Adapter); refunds | `CheckoutAcceptance`, `LifecycleAcceptance` |
| F7 | Order lifecycle `PLACED → PAID` (at checkout) `→ SHIPPED → DELIVERED`, `PAID → CANCELLED` (refund) | `LifecycleAcceptance` |
| F8 | Domain events dispatched after commit; notifications via observers; low-stock alert | `EventsAcceptance` |
| F9 | Concurrent fulfilment on virtual threads with a parallelism limit | `FulfilmentAcceptance` |
| F10 | Reports as sealed request/report types, rendered as text and CSV | `ReportsAcceptance` |
| F11 | CLI (inbound adapter) over all use cases | `CliAcceptance` |
| — | ≥ 10 patterns declared, minimum mix, a sealed record hierarchy in the student's code | `PatternInventoryAcceptance` |
| — | Hexagonal layering | `ArchitectureRules` (default build) |

**Extension features** (brief §2.3): two per student (three per pair, see open question 3) from a menu, specified in
the student's `SPEC.md` with their own acceptance criteria and tests. Not covered by the shipped suite; graded by
the rubric (criterion C3). Structured Concurrency (preview) appears **only** as menu item E10.

## Maven layout and packages

Two new Maven modules of the parent POM (SPEC.md §3, §5), added to the parent `<modules>` in C2/C3 — **after owner
approval of open question 1**:

| Module dir | artifactId | Contents |
|---|---|---|
| `capstone/starter` | `capstone-starter` | `src/main`: GIVEN API (`…capstone.api..`) + student skeleton (`…capstone.shop..`, `// TODO(capstone)` markers). `src/test`: abstract acceptance contracts, fixtures and architecture rules (`…capstone.acceptance..`), their starter bindings (`…capstone.shop..`). Publishes its test classes as a **test-jar**. |
| `capstone/reference` | `capstone-reference` | `src/main`: reference solution (`…capstone.reference..`). `src/test`: bindings of the same contracts to the reference + its own unit tests. Depends on `capstone-starter` (compile, for `api` only) and `capstone-starter:test-jar` (test). |

Package roots (all under `io.github.aliturgutbozkurt.patterns.capstone`):

```
api/                 GIVEN — do not modify (starter src/main, also used by the reference)
  PatternShop, PatternShopFactory, ShopEnvironment, ShopSettings
  model/ catalogue/ cart/ pricing/ checkout/ order/ event/ fulfilment/ report/ cli/
  external/          third-party systems the core must not touch (payment API, warehouse API, notification gateway)
  sim/               deterministic simulators + DemoData for the CLI demo (config only)
  pattern/           @PatternRole, DesignPattern, PatternCategory
shop/                STUDENT code (starter skeleton): domain/ application/ adapter/in/… adapter/out/… config/
reference/           REFERENCE code (reference module only): domain/ application/ adapter/in/… adapter/out/… config/
acceptance/          (src/test, starter) abstract contracts, ShopTestKit and fakes — shared via test-jar
```

The reference therefore never lives in a starter package (CLAUDE.md §9), and the starter never contains a solution.
`src/main` of both modules has zero external dependencies; ArchUnit is test-scoped only.

**Commands** (Verify lines in `tasks/todo.md` for C3–C6 need `-am`, because the reference depends on the starter's
jars inside the reactor; see open question 1):

```bash
./mvnw -q -pl capstone/starter verify                    # GIVEN API + skeleton compile; ArchitectureRules green on the skeleton
./mvnw -q -pl capstone/starter test -Pexercises          # acceptance suites (red on the starter, green on a finished project)
scripts/check-starters.sh capstone/starter               # C2 verify: compiles, every *ExerciseTest suite fails
./mvnw -q -pl capstone/reference -am verify              # C3–C6 verify: reference passes every contract + ArchUnit
java -cp capstone/starter/target/classes io.github.aliturgutbozkurt.patterns.capstone.shop.config.Main --demo
```

## GIVEN API (starter `src/main`, do not modify)

All types are `public`, documented with Javadoc, and dependency-free. Records validate in compact constructors
(`null` → `NullPointerException`, other invalid values → `IllegalArgumentException`) and copy lists with `List.copyOf`.
Sealed results are the way business outcomes are reported; exceptions are reserved for programming errors and
unknown ids where stated.

Exception conventions (pinned in C2, stated in the use cases' Javadoc and in brief §2.2): invalid input →
`IllegalArgumentException` (`duplicate SKU: BOK-001`, `unknown product: XXX-999` when adding to a cart,
`unknown coupon: NOPE`, `expired coupon: SUMMER10`); the unknown target of an operation → `NoSuchElementException`
(`unknown cart: cart-9`, `unknown product: XXX-999` on restock); any edit, undo or redo of a closed cart →
`IllegalStateException("cart closed: cart-1")`. Business-request records (`ProductSpec`, `CheckoutRequest`) reject
only `null`; the business rules they carry are the student's code (otherwise the acceptance tests would test the
GIVEN API instead of the student's code).

| Package | Types |
|---|---|
| `api` | `PatternShop` (interface: `catalogue()`, `carts()`, `pricing()`, `checkout()`, `orders()`, `events()`, `fulfilment()`, `reports()`, `cli()`); `@FunctionalInterface PatternShopFactory` (`PatternShop create(ShopEnvironment env)`); record `ShopEnvironment(Clock clock, ExternalPaymentApi payments, WarehouseApi warehouse, NotificationGateway notifications, Consumer<Throwable> errors, ShopSettings settings)`; record `ShopSettings(String merchantId, int maxParallelOrders, int lowStockThreshold)` with `defaults()` = `("PATTERNSHOP", 4, 5)` |
| `api.model` | records `Sku(String value)` (`[A-Z]{3}-\d{3}`), `Money(long kurus)` (non-negative; `of(String)`, `ZERO`, `plus`, `minus` (throws if negative), `times(int)`, `percent(int)` rounded half-up, `min`, `isZero`, `compareTo`, `toPlainString()` → `"987.91"`), `CustomerId(String value)` (non-blank), `CartId(String value)`, `OrderId(String value)` (`order-<n>`, `Comparable` by `n`; `of(long)`, `number()`), `Address(String recipient, String street, String city, String postalCode)` (`isComplete()`); enums `Category { BOOKS, ELECTRONICS, HOME, TOYS }`, `ProductType { PHYSICAL, DIGITAL }`, `OrderStatus { PLACED, PAID, SHIPPED, DELIVERED, CANCELLED }` |
| `api.catalogue` | `CatalogueUseCase` (`ProductView add(ProductSpec)`, `Optional<ProductView> find(Sku)`, `List<ProductView> search(ProductQuery)`, `ProductView restock(Sku, int quantity)`); records `ProductSpec(Sku, String name, Category, ProductType, Money price, int initialStock)`, `ProductView(Sku, String name, Category, ProductType, Money price, int stock)`, `ProductQuery(Set<Category> categories, long maxPriceKurus, String nameContains)` with `all()` and withers `inCategory` (adds a category; empty set = any), `priceAtMost` (inclusive), `nameContaining` (ignoring case) |
| `api.cart` | `CartUseCase` (`CartId open(CustomerId)`, `CartView add(CartId, Sku, int)`, `CartView changeQuantity(CartId, Sku, int)`, `CartView remove(CartId, Sku)`, `CartView applyCoupon(CartId, String code)`, `CartView view(CartId)`, `boolean undo(CartId)`, `boolean redo(CartId)`); records `CartView(CartId id, CustomerId customer, List<CartLine> lines, String coupon, boolean open)` (`coupon` is `""` when none), `CartLine(Sku sku, int quantity)` |
| `api.pricing` | `PricingUseCase` (`void addPromotion(PromotionSpec)`, `PriceQuote quote(CartId)`); `sealed interface PromotionSpec permits BuyXGetYFree(Sku sku, int buy, int free), CategoryPercentOff(Category category, int percent), AmountOffOver(Money threshold, Money off), Coupon(String code, int percent, LocalDate validUntil)` (records nested; a coupon is valid up to and including `validUntil` in the clock's zone); records `PriceQuote(List<QuoteLine> lines, Money subtotal, List<Adjustment> discounts, Money shipping, Money total)` (invariant `total = subtotal − Σ discounts + shipping`; a discount is capped at what is left; 0.00 discounts are not listed), `QuoteLine(Sku sku, String name, int quantity, Money unitPrice, Money lineTotal)`, `Adjustment(String label, Money amount)` |
| `api.checkout` | `CheckoutUseCase` (`CheckoutResult checkout(CheckoutRequest)`); record `CheckoutRequest(CartId cart, Address shippingAddress, String cardToken)`; `sealed interface CheckoutResult permits Placed(OrderId order, Money total, String paymentReference), Rejected(List<String> reasons)` |
| `api.order` | `OrderUseCase` (`Optional<OrderView> find(OrderId)`, `List<OrderView> ordersOf(CustomerId)`, `TransitionResult cancel(OrderId, String reason)`, `TransitionResult markDelivered(OrderId)`); records `OrderView(OrderId id, CustomerId customer, List<OrderLine> lines, Money total, OrderStatus status, String paymentReference, String trackingCode, Instant placedAt, List<StatusChange> history)` (`trackingCode` is `""` until shipped), `OrderLine(Sku sku, String name, ProductType type, int quantity, Money unitPrice)`, `StatusChange(OrderStatus status, Instant at, String note)` (note: payment reference for `PAID`, tracking code for `SHIPPED`, reason for `CANCELLED`, else `""`); `cancel` refuses in the order `unknown order: <id>` → `cannot cancel <STATUS> order` → `missing reason` (blank reason) → `refund failed`; `sealed interface TransitionResult permits Done(OrderView order), Refused(String reason)` |
| `api.event` | `ShopEvents` (`<E extends ShopEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler)`); `Subscription` (`close()`, idempotent); `sealed interface ShopEvent permits OrderPlaced(OrderId, CustomerId, Money total), OrderPaid(OrderId, String paymentReference), OrderShipped(OrderId, String trackingCode), OrderDelivered(OrderId), OrderCancelled(OrderId, String reason, boolean refunded), StockLow(Sku, int remaining)` |
| `api.fulfilment` | `FulfilmentUseCase` (`FulfilmentReport fulfilPaidOrders()`); records `FulfilmentReport(List<OrderId> shipped, List<FulfilmentFailure> failed)`, `FulfilmentFailure(OrderId order, String reason)` |
| `api.report` | `ReportUseCase` (`Report run(ReportRequest)`, `String render(Report, ReportFormat)`); `sealed interface ReportRequest permits DailySales(LocalDate from, LocalDate to), TopProducts(int limit), CustomerStatement(CustomerId customer), InventoryStatus()`; `sealed interface Report permits DailySalesReport(List<DayTotal> days, int orders, Money revenue), TopProductsReport(List<ProductSales> rows), CustomerStatementReport(CustomerId customer, List<StatementLine> lines, Money totalSpent), InventoryReport(List<StockLine> lines)`; row records `DayTotal(LocalDate day, int orders, Money revenue)`, `ProductSales(int rank, Sku sku, String name, int units, Money revenue)`, `StatementLine(OrderId order, LocalDate date, OrderStatus status, Money total)`, `StockLine(Sku sku, String name, int stock, boolean low)`; `enum ReportFormat { TEXT, CSV }` |
| `api.cli` | `CommandLine` (`String execute(String line)`) |
| `api.external` | `ExternalPaymentApi` (`GatewayResponse authorize(String merchantId, String cardToken, String amount, String currency, String idempotencyKey)`, `GatewayResponse refund(String merchantId, String reference, String amount, String currency)`); record `GatewayResponse(int status, String reference, String message)` (200 approved, 402 declined, 5xx unavailable); `WarehouseApi` (`void pick(String orderRef, String sku, int quantity)`, `String pack(String orderRef)` → parcel id, `String ship(String parcelId, String postalCode)` → tracking code; all blocking, may throw `WarehouseException`); `WarehouseException extends RuntimeException`; `NotificationGateway` (`void send(Notification)`); record `Notification(String recipient, String subject, String body)` |
| `api.sim` | `SimulatedPaymentApi` (token `tok_visa_ok` → 200 `txn-<n>`, `tok_declined` (and any other token) → 402, `tok_unavailable` → 503; an approved idempotency key gets the same reference again; refunds of approved, not yet refunded references → 200, else 404), `SimulatedWarehouse` (parcel `PCL-<n>`, tracking `TRK-<4 digits>` with n = the order number, so output is deterministic under any interleaving; fixed 20 ms latency), `ConsoleNotifications` (`Consumer<String>` sink, one line `NOTIFY <recipient> | <subject> | <body>`), `DemoData` (`seed(PatternShop)`: the sample catalogue and promotions below, through the public use cases) |
| `api.pattern` | `@Retention(RUNTIME) @Target(TYPE) @Repeatable(PatternRoles.class) @interface PatternRole { DesignPattern value(); String role(); }`, `PatternRoles`; `enum PatternCategory { CREATIONAL, STRUCTURAL, BEHAVIOURAL, CONCURRENCY, ARCHITECTURAL }`; `enum DesignPattern` (with `category()`): every pattern of m02–m11 — SINGLETON, STATIC_FACTORY_METHOD, FACTORY_METHOD, ABSTRACT_FACTORY, BUILDER, PROTOTYPE, OBJECT_POOL; ADAPTER, BRIDGE, COMPOSITE, DECORATOR, FACADE, FLYWEIGHT, PROXY; CHAIN_OF_RESPONSIBILITY, COMMAND, INTERPRETER, ITERATOR, MEDIATOR, MEMENTO, OBSERVER, STATE, STRATEGY, TEMPLATE_METHOD, VISITOR; THREAD_PER_TASK, PRODUCER_CONSUMER, GUARDED_SUSPENSION, BALKING, IMMUTABLE_OBJECT, STRUCTURED_CONCURRENCY, SCOPED_VALUE; DEPENDENCY_INJECTION, REPOSITORY, SPECIFICATION, PORTS_AND_ADAPTERS, DOMAIN_EVENTS |

**Student skeleton (starter `src/main`, `shop`):** `config.ShopCompositionRoot implements PatternShopFactory` whose
`create` returns a `PatternShop` whose use cases throw `UnsupportedOperationException("TODO(capstone): …")`;
`config.Main` (reads commands from standard input, `--demo` seeds `DemoData`, uses the `api.sim` simulators); empty
`domain`, `application`, `adapter.in.cli`, `adapter.out` packages with a `package-info.java` each explaining what
belongs there. The skeleton already satisfies `ArchitectureRules`.

**Sample data** (`DemoData`, also used by the fixture): products `BOK-001` "Design Patterns Handbook" BOOKS PHYSICAL
250.00 ×20, `BOK-002` "Java 27 in Action" BOOKS PHYSICAL 400.00 ×8, `TOY-001` "Pattern Puzzle" TOYS PHYSICAL 120.00 ×6,
`HOM-001` "Hexagon Mug" HOME PHYSICAL 89.90 ×50, `ELE-001` "USB-C Hub" ELECTRONICS PHYSICAL 649.00 ×10, `DIG-001`
"E-book Bundle" BOOKS DIGITAL 99.90; promotions `BuyXGetYFree(TOY-001, 2, 1)`, `CategoryPercentOff(BOOKS, 10)`,
`AmountOffOver(1000.00, 100.00)`, `Coupon("AUTUMN5", 5, 2026-12-31)`. Fixed rule constants (brief §2.2): at most 10
units per SKU per order, undo depth 20, shipping fee 49.90 below a merchandise total of 500.00.

## Output formats (pinned by the acceptance tests)

- **Money** is printed with `Money.toPlainString()` (`987.91`); CLI lines append nothing else unless stated.
- **Reports, TEXT:** a title line, then one line per row with fields joined by `" | "`, then a total line where the
  report has one. Titles: `Daily sales <from> .. <to>`, `Top <n> products`, `Statement for <customer>`, `Inventory`.
  Rows: `<day> | <orders> | <revenue>`; `<rank> | <sku> | <name> | <units> | <revenue>`;
  `<order> | <date> | <status> | <total>`; `<sku> | <name> | <stock> | <yes/no>`. Totals: `Total | <orders> |
  <revenue>` (daily sales), `Total spent | <money>` (statement). Lines end with `\n`, including the last. In
  `Top <n> products`, n is the number of rows (the report record does not carry the requested limit); the title of
  daily sales uses the first and last day of `days`.
- **Reports, CSV:** a header of lower-case column names (`day,orders,revenue`; `rank,sku,name,units,revenue`;
  `order,date,status,total`; `sku,name,stock,low`), one row per entry, no title and no total line; fields that contain
  `,` or `"` are quoted RFC-4180 style; `low` is `yes`/`no` as in TEXT; every line ends with `\n`.
- **CLI** (`CommandLine.execute`, one command per call, output without trailing newline, multi-line output joined
  with `\n`): `help` → `Commands:` followed by one usage line per command, in this order:
  `product list [CATEGORY]`, `cart open <customer>`, `cart add <cart> <sku> <qty>`, `cart qty <cart> <sku> <qty>`,
  `cart remove <cart> <sku>`, `cart coupon <cart> <code>`, `cart undo <cart>`, `cart redo <cart>`, `cart show <cart>`,
  `quote <cart>`, `checkout <cart> <card-token> <recipient>;<street>;<city>;<postal-code>`, `order show <order>`,
  `order cancel <order> <reason…>`, `order deliver <order>`, `fulfil`,
  `report sales <from> <to> [--csv]`, `report top <n> [--csv]`, `report customer <customer> [--csv]`,
  `report inventory [--csv]`, `help`.
  Responses: `CART cart-1`; cart view `cart-1 alice open | BOK-001 x2, TOY-001 x3 | coupon AUTUMN5` (`(empty)` for no
  lines, coupon part omitted when none, `closed` instead of `open`); `NOTHING TO UNDO` / `NOTHING TO REDO`; quote as
  lines `subtotal <m>`, `- <label> <m>` per discount, `shipping <m>`, `total <m>`; `PLACED order-1 987.91 txn-1` /
  `REJECTED <reason>; <reason>`; order view `order-1 alice PAID 987.91 | BOK-001 x2, TOY-001 x3`;
  `CANCELLED order-1` / `DELIVERED order-1` / `REFUSED <reason>`; `order show` of an unknown order →
  `ERROR unknown order: order-9`; product list one line per product
  `BOK-001 | Design Patterns Handbook | BOOKS | PHYSICAL | 250.00 | 20` (sorted by SKU) or `NO PRODUCTS`;
  fulfilment `SHIPPED order-1 TRK-0001` and
  `FAILED order-2 <reason>` lines in order-number order, or `NOTHING TO FULFIL`; reports as rendered (without the
  final newline). `help` lists the usage lines indented by two spaces. Errors: `ERROR unknown command: <word>`, where
  the command word of the groups `product`, `cart`, `order` and `report` is the group plus its sub-command
  (`ERROR unknown command: cart fly`); wrong arity or unparsable argument (not an int, a malformed SKU or order id, an
  unknown category, a bad date, an address without exactly four `;`-separated fields, an unknown flag) →
  `USAGE <usage line of that command>`; exceptions from use cases → `ERROR <message>`. A blank line gives `""`.

C2 stores the expected CLI transcript and the expected report texts as test resources
(`src/test/resources/acceptance/*.txt`: `cli-help.txt`, `cli-session.txt`, `report-{daily-sales,top-products,
customer-statement,inventory}.txt` and their `.csv.txt` twins) and the brief links to them as the authoritative
examples.

## Test fixtures (starter `src/test`, package `acceptance`)

- `ShopTestKit` — builds a `ShopEnvironment` from the fakes below with `ShopSettings.defaults()` (overridable),
  creates the shop with the binding's `PatternShopFactory`, seeds `DemoData`, and offers helpers
  (`placeOrder(customer, lines…)`, `cartWith(…)`, `address()`).
- `MutableClock` — starts at `2026-11-16T09:00+03:00[Europe/Istanbul]`, `advance(Duration)`.
- `SandboxPaymentApi` — `ExternalPaymentApi` with the `api.sim` token semantics plus a call log (operation,
  arguments) and `failRefunds()`.
- `ScriptedWarehouse` — `WarehouseApi` that records each call with `Thread.currentThread().isVirtual()`, tracks
  current/peak orders in flight (from an order's first call until `ship` returns or a call fails), can hold calls on a
  latch or a `CyclicBarrier` (`holdPicksAt`, `holdOrderUntil(order, latch)`, `shippedSignal(order)`; always with a 5 s
  timeout so a broken implementation fails instead of hanging), and can fail a chosen order (`failOrder`). Parcels
  `PCL-<n>`, tracking codes `TRK-0001`, … by order number.
- `ExpectedOutput` — reads the expected outputs from the test resources (also inside the test-jar).
- `RecordingNotifications`, `RecordingErrors` — spies.
- Abstract contracts: `AcceptanceContract` (base: `protected abstract PatternShopFactory factory()`,
  `protected abstract String applicationRootPackage()`), the per-feature `*Acceptance` classes, `ArchitectureRules`,
  `PatternInventoryAcceptance`. Every test has `@Timeout(10)` (on the base class, inherited). The kit is created
  lazily on first use (`kit()`, or `kitWith(settings)` for other settings), so `ArchitectureRules` and the inventory
  never build a shop.

Bindings: starter `shop.<Feature>ExerciseTest extends <Feature>Acceptance` with `@Tag("exercise")`, factory
`new ShopCompositionRoot()`, root `…capstone.shop`; starter `shop.ShopArchitectureTest extends ArchitectureRules`
(untagged — runs in the default build and is green on the skeleton); reference
`reference.<Feature>ReferenceTest` and `reference.ReferenceArchitectureTest`, root `…capstone.reference`.

## Acceptance-test catalogue

83 exercise-tagged acceptance tests in 11 suites, plus 7 architecture rules. Every test is written against the GIVEN
API only, so the same test runs against the starter (red), any student project and the reference (green).

**`CatalogueAcceptance` (7)** — `addsProductAndFindsItBySku`; `rejectsDuplicateSku`; `rejectsInvalidProductSpec`
(malformed SKU, blank name, zero price, negative stock, digital product with stock); `searchByCategoryIsSortedBySku`;
`searchCombinesCategoryPriceAndNameCriteria`; `restockIncreasesStockOfPhysicalProduct`;
`restockRejectsDigitalProductAndNonPositiveQuantity`.

**`CartAcceptance` (6)** — `newCartIsEmptyAndOpen` (ids `cart-1`, `cart-2`, …);
`addingSameSkuMergesQuantitiesKeepingFirstPosition`; `changeQuantityToZeroRemovesTheLine`;
`rejectsUnknownProductAndNonPositiveQuantityLeavingCartUnchanged`; `unknownCartIsReported`
(`NoSuchElementException("unknown cart: cart-9")`); `couponIsValidatedWhenApplied` (unknown, expired, a second valid
coupon replaces the first).

**`UndoAcceptance` (7)** — `undoRevertsTheLastEdit`; `undoRestoresRemovedLineAtItsPosition`;
`redoReappliesTheUndoneEdit`; `newEditClearsRedo`; `undoAndRedoReturnFalseWhenNothingToDo`;
`failedEditIsNotRecorded`; `historyIsPerCartAndKeepsTwentyEdits` (21 edits → 20 undos succeed, the 21st returns
`false`; another cart is unaffected).

**`PricingAcceptance` (12)** — `emptyCartQuotesZero`; `subtotalIsSumOfLineTotals`;
`buyXGetYFreeDiscountsWholeGroupsOnly`; `categoryPercentOffRoundsHalfUpPerLine`;
`amountOffOverThresholdUsesDiscountedSubtotal`; `onlyHighestQualifyingThresholdApplies`;
`couponAppliesLastOnRemainingAmount`; `expiredCouponGivesNoDiscount` (clock advanced past `validUntil` after
applying); `workedExampleFromTheBrief` (total 987.91, labels and amounts exactly as in brief §2.2);
`shippingFeeBelowThresholdFreeAtOrAbove` (499.99 → fee, 500.00 → free); `digitalOnlyCartHasNoShipping`;
`discountsNeverMakeTheTotalNegative`.

**`CheckoutAcceptance` (14)** — `validCheckoutPlacesAPaidOrder`; `chargesTheQuotedTotalExactlyOnceInProviderFormat`
(one `authorize("PATTERNSHOP", token, "987.91", "TRY", "cart-1")`); `checkoutReservesStockAndClosesTheCart`
(edits, undo and redo afterwards → `IllegalStateException("cart closed: cart-1")`); `rejectsEmptyCart`;
`rejectsMissingAddressOnlyForPhysicalItems`; `rejectsQuantityAboveLimitPerSku`;
`rejectsInsufficientStockNamingTheSku`; `rejectsExpiredCouponAndMissingCardToken`;
`collectsAllValidationErrorsInRuleOrder`; `invalidCheckoutNeverCallsThePaymentApi`;
`declinedPaymentPlacesNoOrderAndKeepsStockAndCart`; `providerErrorIsReportedAsPaymentUnavailable`;
`orderIdsAreConsumedOnlyByPlacedOrders`; `freeOrderIsPlacedWithoutCallingThePaymentApi` (reference `FREE`).

**`LifecycleAcceptance` (8)** — `placedOrderHistoryIsPlacedThenPaidWithClockTimes`;
`cancelPaidOrderRefundsRestocksAndRecordsReason`; `refundFailureRefusesCancellation` (`Refused("refund failed")`,
order stays `PAID`, stock unchanged); `cannotCancelShippedOrDeliveredOrder` (`Refused("cannot cancel SHIPPED
order")`); `deliverOnlyFromShipped`; `cancelledOrderRefusesEveryTransition`; `unknownOrderIsRefused`
(`Refused("unknown order: order-9")`, `find` empty); `ordersOfCustomerAreInPlacementOrder`.

**`EventsAcceptance` (8)** — `checkoutPublishesOrderPlacedThenOrderPaid`; `typedSubscriberReceivesOnlyItsEventType`;
`supertypeSubscriberReceivesAllEvents`; `subscribersSeeCommittedState` (a handler of `OrderPaid` finds the order
`PAID` and the stock already reduced); `rejectedCheckoutPublishesAndNotifiesNothing`;
`customerIsNotifiedOnConfirmationShipmentAndCancellation` (subjects `Order order-1 confirmed` / `shipped` /
`cancelled`; body contains total / tracking code / reason); `failingSubscriberIsReportedAndOthersStillRun`
(exception reaches `ShopEnvironment.errors()`, checkout result unchanged); `stockLowIsPublishedOncePerThresholdCrossing`
(published and sent to `ops` when stock drops from ≥ 5 to < 5; not again while low; again after a restock above and
a new drop).

**`FulfilmentAcceptance` (8)** — `shipsEveryPaidOrderWithTrackingCode`; `fulfilsOrdersConcurrently` (three orders meet
at a barrier inside `pick` — impossible sequentially); `neverExceedsMaxParallelOrders` (8 orders, limit 2, peak = 2);
`warehouseIsCalledFromVirtualThreads`; `failingOrderStaysPaidAndOthersShip`;
`resultsAndEventsAreInOrderNumberOrder` (completion order scrambled by latches; `order-2` before `order-10`);
`digitalOnlyOrderShipsWithoutWarehouse` (tracking `DIGITAL`); `secondRunShipsNothingAndCancelledOrdersAreSkipped`.

**`ReportsAcceptance` (6)** — `dailySalesCoversEveryDayInRangeAndExcludesCancelled`;
`topProductsRankByUnitsThenSku`; `customerStatementListsOrdersAndTotalSpent`; `inventoryReportFlagsLowStock`;
`textRenderingMatchesTheSpecifiedLayout`; `csvRenderingHasHeaderAndOneRowPerEntry`.

**`CliAcceptance` (4)** — `helpListsEveryCommand`; `scriptedSessionProducesTheExpectedTranscript` (resource
`cli-session.txt`: open, add, undo, coupon, quote, checkout, order show, fulfil, deliver, report);
`unknownCommandIsReported`; `malformedArgumentsPrintUsage`.

**`PatternInventoryAcceptance` (3)** — `declaresAtLeastTenDistinctPatterns`; `meetsTheMinimumMixPerCategory`;
`coreDeclaresASealedHierarchyOfRecords`. Rules in the next section.

| Suite | Tests | Feature ids |
|---|---|---|
| Catalogue | 7 | F1 |
| Cart | 6 | F2 |
| Undo | 7 | F3 |
| Pricing | 12 | F4 |
| Checkout | 14 | F5, F6 |
| Lifecycle | 8 | F6, F7 |
| Events | 8 | F8 |
| Fulfilment | 8 | F9 |
| Reports | 6 | F10 |
| CLI | 4 | F11 |
| Pattern inventory | 3 | pattern requirement |
| **Total (exercise-tagged)** | **83** | |
| Architecture rules (default build) | 7 | architecture requirement |

## Architecture rules (ArchUnit, `ArchitectureRules`)

`<root>` is the binding's `applicationRootPackage()`; classes are imported once with
`ImportOption.DoNotIncludeTests`. Rules over packages a project may not have yet (domain, application, adapters,
cycles, static fields) use `allowEmptyShould(true)`, because ArchUnit 1.x fails a rule that selects no classes and
the skeleton has only `config`; a non-empty selection is checked in full. The rules follow m11's `HexagonalShopArchitectureTest` (same ArchUnit idioms,
already verified on JDK 27 / class file 71):

1. `domainDependsOnlyOnJdkAndApiValues` — `<root>.domain..` depends only on `java.lang..`, `java.util..`,
   `java.math..`, `java.time..`, `<root>.domain..`, `…capstone.api.model..`, `…capstone.api.event..`,
   `…capstone.api.pattern..`.
2. `applicationDoesNotDependOnAdaptersOrConfig` — `<root>.application..` does not depend on `<root>.adapter..`,
   `<root>.config..`, `…capstone.api.external..`, `…capstone.api.sim..`.
3. `externalSystemsAreReachedOnlyThroughOutboundAdapters` — only `<root>.adapter.out..` and `<root>.config..` depend
   on `…capstone.api.external..`; only `<root>.config..` depends on `…capstone.api.sim..`.
4. `adaptersDoNotDependOnEachOther` — `slices().matching("<root>.adapter.(*).(*)..").should().notDependOnEachOther()`.
5. `adaptersAreWiredOnlyInConfig` — classes in `<root>.adapter..` are accessed only from `<root>.adapter..` and
   `<root>.config..`; classes in `<root>.config..` are accessed by no other package.
6. `noCyclesBetweenPackages` — `slices().matching("<root>.(*)..").should().beFreeOfCycles()`.
7. `productionCodeIsSelfContained` — classes in `<root>..` depend only on `java..` and
   `io.github.aliturgutbozkurt.patterns.capstone..`; no call to `System.exit`; no non-final static field.

## Pattern inventory rules (`PatternInventoryAcceptance`)

A pattern counts when at least one class in `<root>..` is annotated `@PatternRole(DesignPattern.X, role = "…")`
(the GIVEN annotation, read by reflection through ArchUnit's import):

- **≥ 10 distinct** patterns from the categories CREATIONAL, STRUCTURAL, BEHAVIOURAL, CONCURRENCY. ARCHITECTURAL
  patterns (DI, Repository, Specification, Ports & Adapters, Domain events) are required by the architecture anyway and
  do **not** count towards the ten.
- **Minimum mix:** ≥ 2 creational, ≥ 2 structural, ≥ 3 behavioural, ≥ 1 concurrency pattern other than
  `IMMUTABLE_OBJECT` (records alone do not make a concurrency design).
- **Modern Java floor:** at least one `sealed` interface in `<root>..` whose permitted subclasses are all records
  (≥ 2 of them). Whether it is used through an exhaustive `switch` with record patterns is assessed by the rubric
  (bytecode cannot show it reliably).

The test proves only the floor. Whether each declared pattern is real, fits a force and is justified is graded by
humans (rubric C2, C4); declaring a pattern that is not there costs points there.

## Reference design per slice

Root `…capstone.reference`. Every participant type carries `@PatternRole` and a one-line Javadoc of intent with
`@see` the C7 guide section. Target: **13 counted patterns** (the guide explains each and the ones deliberately not
used, e.g. Singleton, Visitor, Memento).

**C3 — domain, builders, factories, repository** (acceptance: Catalogue, Cart)

| Concern | Pattern(s) | Reference types | Builds on |
|---|---|---|---|
| Values, ids | Immutable Object, Static Factory Method | `api.model` records; `domain.ids.SequentialIds` | m02 `staticfactory`, m09 `immutability` |
| Product creation by type | Factory Method | `domain.catalogue.ProductFactory` (`enum` of `ProductType` → constructor reference; DIGITAL ignores stock) | m02 `factorymethod.export.modern` |
| Order assembly | Builder | `domain.order.Order.builder()` (lines, customer, address, clock) | m03 `builder.*` |
| Persistence | Repository, Specification | `application.port.out.{ProductRepository, CartRepository, OrderRepository}`; `adapter.out.memory.*` (thread-safe, return copies); `domain.catalogue.ProductSpecs` | m11 `repository.catalog`, `repository.orders` |
| Wiring | DI (composition root) | `config.ReferenceCompositionRoot implements PatternShopFactory`; `config.Main` | m03 `di`, m11 `di.lifetimes` |
| Promotions as data (needed in C3: `DemoData.seed` registers promotions, the cart validates coupons) | Repository | `application.port.out.PromotionRepository` + `adapter.out.memory.InMemoryPromotionRepository`; `domain.pricing.PromotionRule` records (behaviour added in C4); `application.PricingService.addPromotion` | m11 `repository.*` |
| Transactions | — | `application.events.UnitOfWork`: one lock around every state change (C5 adds dispatch after commit) | m11 `events.aggregate` |

*Implemented (C3):* `Order.builder()` takes the clock's instant (`placedAt(Instant)`) rather than the clock, so the
domain never reads time itself. `ReferenceArchitectureTest` is bound already in C3 (not only in C6) so every slice is
checked against the seven rules; ports of later slices are placeholders in `config.PendingSlices` until their slice.

**C4 — pricing, lifecycle, validation** (acceptance: Pricing; lifecycle and validation by unit tests until C5)

| Concern | Pattern(s) | Reference types | Builds on |
|---|---|---|---|
| Promotion kinds | Strategy | `domain.pricing.PromotionRule` (sealed; one record per `PromotionSpec` kind) | m06 `strategy.shipping.modern` |
| Price pipeline in fixed order | Decorator | `domain.pricing.PriceStep` wrapping steps: `BasePrices` → `LinePromotions` → `OrderPromotion` → `CouponDiscount` → `Shipping`; `PricingPipeline.standard()` builds the chain | m04 `decorator.coffee.modern`, m09 `composition.pricing` |
| Order lifecycle | State (sealed) | `domain.order.OrderState` (sealed: `Placed`, `Paid`, `Shipped`, `Delivered`, `Cancelled` records, each with only the data valid in it) + exhaustive `switch` transitions returning `Transition` | m08 `state.order.sealed`, m09 `dop.order.modern` |
| Checkout validation | Chain of Responsibility | `domain.checkout.CheckoutRule` (functional) chained collect-all after a fail-fast `NonEmptyCart`; rule order as brief §2.2 | m07 `chain.validation` |

*Implemented (C4):* each `PromotionRule` record is a concrete strategy with `PriceSheet applyTo(PriceSheet)`; the
decorators share the abstract `PriceStepDecorator` (inner stage first, then `adjust`), and `PriceSheet` keeps per line
what is left after step 2 so category percentages always use that base. Transitions live in `domain.order.OrderLifecycle`
(one exhaustive `switch` per event, `Transition` = sealed `Allowed`/`Refused`); `OrderState.Cancelled` derives
`refunded()` from the payment reference (`FREE` → no refund). The chain is `CheckoutRules.standard()` =
`nonEmptyCart().andThen(addressForPhysicalItems().and(quantityLimit()).and(stockAvailable()).and(couponNotExpired()).and(cardTokenPresent()))`
over a `CheckoutCandidate` value.

**C5 — events, payment, checkout, undo** (acceptance: Checkout end-to-end, Events, Undo)

| Concern | Pattern(s) | Reference types | Builds on |
|---|---|---|---|
| Events after commit | Observer, Domain events | aggregates record `ShopEvent`s; `application.events.EventDispatcher` (typed subscribe, queued re-entrant publish, failing handler → `errors`); `application.events.UnitOfWork` (commit, then dispatch) | m07 `observer.eventbus`, m11 `events.aggregate` |
| Notifications | Observer | `application.notify.CustomerNotifier`, `StockAlerts` subscribed in the composition root; outbound port `Notifier` | m07, m11 `refactoring.notifications` |
| Payment provider | Adapter | outbound port `PaymentPort` (sealed `PaymentOutcome`); `adapter.out.payment.ExternalPaymentAdapter` (money ↔ `"987.91"`, status codes ↔ outcome) | m04 `adapter.payment`, m11 `LegacyPaymentAdapter` |
| Checkout orchestration | Facade (+ compensation) | `application.CheckoutService`: validate → quote → charge → commit → dispatch; if commit fails after a charge it refunds | m05 `facade.checkout` |
| Cart undo/redo | Command | `domain.cart.CartEdit` (sealed: `AddItem`, `ChangeQuantity`, `RemoveItem`, `ApplyCoupon`); `apply` returns the inverse edit; `domain.cart.EditHistory` (two `ArrayDeque`s, depth 20) | m06 `command.spreadsheet.modern` |

*Implemented (C5):* `CartEdit` has a fifth record, `RestoreItem(position, item)`, the inverse of a removal (and of a
change to 0), so undo puts the line back at its old position; `CartService` (client) performs every edit through the
cart's `EditHistory` (invoker). Events are raised into the transaction's `Changes` collector of the
`UnitOfWork` (not stored inside the aggregates): `run` commits under one lock, releases it and then dispatches;
`runDeferred` hands the events back (fulfilment, C6); nested runs join the outer transaction. `EventDispatcher`
keeps its re-entrancy queue per thread. `PaymentPort` (`charge`, `refund` → sealed `PaymentOutcome`) is the Adapter's
target. Checkout holds the unit-of-work lock across the charge (single writer: validation, charge and commit cannot be
interleaved with another change — a deliberate trade-off of throughput for simplicity in an in-memory shop), prices
before validating (pricing has no side effects) and refunds when the commit fails after a charge. `Inventory` holds the
stock bookkeeping that checkout (reserve, `StockLow` on a crossing) and cancellation (release) share. **Bindings:**
Checkout and Undo are bound in C5; Lifecycle and Events are bound in C6, because three tests of each ship orders
through fulfilment (verified: all their other tests pass in C5).

**C6 — fulfilment, reports, CLI, architecture** (acceptance: all; ArchUnit; inventory)

| Concern | Pattern(s) | Reference types | Builds on |
|---|---|---|---|
| Concurrent fulfilment | Thread-per-task (virtual threads) + bounded parallelism | `application.FulfilmentService`: `Executors.newVirtualThreadPerTaskExecutor()` in try-with-resources, a `Semaphore(maxParallelOrders)`, outbound port `Warehouse` adapted by `adapter.out.warehouse.WarehouseAdapter`; outcomes committed per order, events dispatched on the caller thread after the run in order-number order | m10 `threadpertask.*`, `producerconsumer.fulfilment` |
| Reports | sealed types + pattern matching; Template Method | `application.ReportService` (exhaustive `switch` over `ReportRequest` with record patterns); rendering in `application.render.ReportRenderer` (template: title → rows → total) with `TextRenderer`, `CsvRenderer` | m08 `visitor.cart.modern`, m06 `templatemethod.*` |
| CLI | Command (command table), inbound adapter | `adapter.in.cli.CliAdapter implements CommandLine` (`Map<String, CliCommand>`) | m06 `command.*`, m11 `CommandLineAdapter` |
| Architecture | Ports & Adapters | packages as above; `ReferenceArchitectureTest` | m11 `hexagonal.shop`, architecture tests |

*Implemented (C6):* the outbound port `Warehouse` ships one whole order (`ship(order, physicalItems, postalCode)` →
sealed `ShipmentOutcome`); `WarehouseAdapter` drives pick → pack → ship and turns a `WarehouseException` into
`Failed(message)`. `FulfilmentService` submits one task per paid order to a virtual-thread-per-task executor (closed
by try-with-resources, so the call returns only when all tasks are done), each task holds a `Semaphore` permit while
it talks to the warehouse, commits through `UnitOfWork.runDeferred` (re-reading the order, so an order cancelled
meanwhile is refused, not shipped) and returns its events; the caller joins the futures in order-number order and
dispatches the events. Reports: `ReportService.run` is an exhaustive switch with record patterns over the sealed
requests; `render.Table.of(Report)` does the same over the sealed reports; `ReportRenderer.render` is the template
method (heading → rows → total), `TextRenderer` / `CsvRenderer` the concrete classes. CLI: `CliAdapter` (invoker) looks
commands up in a table built by `CliCommands`, `CliArgs` parses arguments (`UsageException` → `USAGE …`), use-case
exceptions become `ERROR …`. C6 also binds Lifecycle and Events (see C5) and removes the C3–C5 placeholders.
Counted patterns: 13 + Immutable Object, as planned. The Structured-Concurrency variant stays a C7 guide snippet;
nothing in `capstone/*` is compiled with `--enable-preview`.

Counted patterns in the reference (13): Static Factory Method, Factory Method, Builder; Adapter, Decorator, Facade;
Strategy, Chain of Responsibility, State, Command, Observer, Template Method; Thread-per-task. Plus Immutable Object
(not counted for the concurrency minimum) and five architectural patterns.

**Optional extension, not in the reference build:** a Structured-Concurrency variant of `FulfilmentService`
(`StructuredTaskScope.open(Joiner.allUntil(_ -> false))`, preview in JDK 27, JEP 533 — JDK 27 has no
`Joiner.awaitAll()`; `allUntil` with a never-true predicate waits for every subtask and `join()` returns them in fork
order; the sketch was checked with `java --enable-preview --source 27` in C7). The C7 guide shows it as a `// snippet`
with the preview banner and the m10 run command; no preview code is compiled in `capstone/*`, so no POM needs
`--enable-preview` (docs/java27-features.md: "Capstone uses it only in an optional extension").

## Concurrency, determinism and preview policy

- Mandatory fulfilment uses only final APIs (virtual threads, `java.util.concurrent`). No test depends on timing
  except the fixture's 5 s safety timeouts; concurrency is proven with barriers and latches (m10 "deterministic
  concurrency tests").
- Clock and ids are injected; CLI and report output are deterministic, so the transcript can be compared verbatim.
- Students may implement E10 (Structured Concurrency) in their own copy only by following m10's isolation rules
  (one package, `--enable-preview` + `-Xlint:-preview` in their POM, an isolation test), with the default path still
  on final APIs and all shipped tests green **without** preview. It never affects passing.

## Starter (C2) and the starter check

- Starter compiles with `-Xlint:all -Werror`; `ShopArchitectureTest` is green; each of the 11 `*ExerciseTest` suites
  fails (all tests fail through `UnsupportedOperationException`, the inventory fails on zero patterns), which
  `scripts/check-starters.sh capstone/starter` proves.
- The test-jar and `src/test/resources/acceptance/` contain no solution logic beyond fakes and expected outputs.
- `capstone/starter/SPEC.md` and `capstone/starter/REPORT.md` ship as empty templates (brief §4, §9); the brief's
  template is the source.

## C7 walkthrough guide and rubric mapping

`capstone/guide.{en,tr}.md` (+ PDFs via `scripts/build-pdf.sh capstone`, which converts **every**
`capstone/*.{en,tr}.md` — so C7 also produces PDFs of the brief and the rubric; that is intended). Sections:

1. **Pattern map** — one row per counted pattern: force in PatternShop, participants (links to reference source
   files), module that taught it, alternative rejected and why. This is a filled-in example of the rubric's
   pattern-justification table.
2. **Slice walkthrough** — C3–C6 in build order, each with a class or sequence diagram (checkout: validate → quote →
   charge → commit → dispatch; fulfilment fan-out).
3. **Trade-offs** — Command vs. Memento for undo; Decorator chain vs. function composition for pricing; sealed state
   vs. enum state; semaphore vs. Producer–Consumer for fulfilment; why no Singleton or Visitor.
4. **SDD artefacts** — a sample student `SPEC.md` for the reference (the brief's template, filled in) and its change
   log.
5. **Rubric mapping** — for each rubric criterion C1–C10, where the reference shows the "excellent" level and what a
   weaker submission typically lacks.
6. **Optional extension** — the Structured-Concurrency sketch (preview, `// snippet`, not compiled).

*Implemented (C7):* the guide opens (before §1, without a heading, so the PDF's automatic section numbers match the
`@see "capstone guide §N …"` references in the reference Javadoc) with how to read the reference and how to run it.
§2 starts with the architecture overview (hexagon diagram, package table, composition root, and a verbatim CLI session
of `Main --demo` including reports). Two sections were added before the optional extension, which therefore is §8:
§6 **Testing approach** (acceptance contracts, architecture rules, concurrency tests with barriers instead of timing)
and §7 **Common pitfalls**. §3 also names a limit of the reference (the unit of work has no rollback). 10 Mermaid
diagrams. `scripts/lib/check_docs.py` resolves `// file:` markers of `capstone/*.md` against both capstone modules
(the guide quotes the reference and the starter's acceptance contracts), and `docs/pdf/header.typ` lets long tables
break across pages.

If the owner chooses the instructor-only branch in open question 2, the guide is merged together with the reference.

## Task breakdown and verification

| Task | Delivers | Verify |
|---|---|---|
| C1 (#66) | brief + rubric EN/TR, this spec, glossary rows | owner review; `scripts/check-docs.sh` |
| C2 (#67) | `capstone/starter`: GIVEN API, skeleton, fixtures, 11 contracts + architecture rules, bindings, resources, templates; parent `<modules>` + POMs per open question 1 | `scripts/check-starters.sh capstone/starter`; `./mvnw -q -pl capstone/starter verify` |
| C3 (#68) | reference slice 1 (table above); Catalogue + Cart suites green | `./mvnw -q -pl capstone/reference -am verify` (other reference bindings may be added slice by slice) |
| C4 (#69) | slice 2; Pricing suite green; lifecycle and validation covered by the reference's own unit tests | same |
| C5 (#70) | slice 3; Checkout, Events, Undo, Lifecycle green | same |
| C6 (#71) | slice 4; all 83 + 7 green; ≥ 10 counted patterns | same |
| C7 (#72) | guide EN/TR + PDFs | `scripts/check-docs.sh`; `scripts/build-pdf.sh capstone` |

During C3–C5 the reference binds only the suites of finished slices (a binding is added in the slice that makes the
whole suite green — Lifecycle and Checkout need the payment adapter, so they are bound in C5) so `verify` stays green
after every PR; C6 binds the rest (Fulfilment, Reports, CLI, inventory, architecture).

## Out of scope

UI (web, desktop, TUI beyond the line-based CLI), databases and file persistence in the mandatory part (E5 allows a
file adapter), real payment providers or network calls, authentication, multi-currency and tax calculation (prices
are VAT-inclusive TRY), DI frameworks, mocking libraries, preview features in mandatory code, auto-grading platform
integration (SPEC.md §12, v1.1).

## Decisions (owner, 2026-10-01)

All questions below were answered **yes**: the recommended defaults apply (build changes, published reference module, individual work, report in one language plus a one-page summary in the other).

1. **Build changes and the ArchUnit test dependency for the capstone (ask first).** C2/C3 need: (a) `capstone/starter`
   and `capstone/reference` added to the parent `<modules>`; (b) in **both** capstone POMs the same test-scoped
   `archunit-junit5` dependency m11 uses (version managed by the parent, already approved for m11); (c) in the starter
   POM a `maven-jar-plugin` `test-jar` execution (plugin already in the default lifecycle, only a goal is added), and
   in the reference POM dependencies on `capstone-starter` (compile) and `capstone-starter:test-jar` (test);
   (d) the Verify lines of C3–C6 in `tasks/todo.md` become `./mvnw -q -pl capstone/reference -am verify`.
   `src/main` stays dependency-free (rule 7 asserts it). *Recommended default:* approve all four. If no to (c):
   the contracts are duplicated into the reference (drift risk) — not recommended.
2. **Publishing the reference solution.** CLAUDE.md forbids solutions *inside starter packages*; a separate module
   `capstone/reference` (package `…capstone.reference`) satisfies that, and SPEC.md §9.6 requires a reference in the
   repo. Students work in a fork that contains it. *Recommended default:* publish it in `main` as a separate module,
   and rely on what cannot be copied: the student's own SPEC.md (graded at W10, before most slices exist in their
   repo), two extension features not in the reference, the pattern-justification table, the oral defence (C10 asks
   about the student's own code), weekly commit history (rubric C8), and a similarity check against the reference
   (brief §12: substantial copying → implementation part 0 and the institution's procedure). Alternative: keep
   `capstone/reference` on an instructor-only branch until W14 and merge it for the v1.0.0 release — stronger, but
   it delays SPEC.md §9.6 and CI coverage of the reference on `main`.
3. **Team size.** *Recommended default:* individual work; pairs allowed on request at W9 with three extension
   features instead of two and each member presenting their own part (both answer questions on all of it).
4. **"Bilingual report".** *Recommended default:* the report is written in English **or** Turkish (student's
   choice) and includes a one-page summary in the other language; terminology follows `docs/glossary.md`. A full
   report in both languages doubles the writing load without adding design content.

## Success criteria

- [ ] Brief and rubric exist in EN + TR with identical heading structure; `scripts/check-docs.sh` reports 0 problems
- [ ] Every rubric criterion has four level descriptors that can be checked from the submission (counts, commands,
      files), and the automatic-fail conditions are explicit
- [ ] Owner approves domain, scope and the open questions (C1 acceptance)
- [ ] C2: starter compiles, architecture rules green on the skeleton, all 11 acceptance suites red (`check-starters.sh`)
- [ ] C3–C6: the reference passes all 83 acceptance tests and 7 architecture rules and declares ≥ 10 counted patterns
- [ ] C7: guide EN + TR + PDFs; pattern map links to reference source files
