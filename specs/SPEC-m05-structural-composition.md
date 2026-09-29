# Spec: m05-structural-composition — Structural II: Composition

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 6 · Task: #31

## Objective

m04 wrapped *one* object to change its interface or behaviour. m05 is about how *many* objects are put together:
treating a whole tree like a single node (Composite), letting two hierarchies vary independently instead of
multiplying into N × M subclasses (Bridge), giving a tangle of subsystems one simple entry point (Facade), and sharing
immutable state among thousands of small objects (Flyweight). After this module a student can model part–whole
hierarchies with sealed types and exhaustive `switch`, split an abstraction from its implementation, design a narrow
facade without hiding the subsystem, and reduce object counts by sharing intrinsic state — and explain why value-based
classes such as `Integer` and `LocalDate` must never be compared with `==`.

## Learning outcomes

After this module a student can:

1. **Implement** Composite in its classic GoF form (component interface, leaf, composite holding children) and in its
   modern form (a `sealed` interface with `record` leaves and nodes, operations written as recursive exhaustive
   `switch` expressions), and **compare** the two (adding a node type vs. adding an operation).
2. **Explain** the transparency vs. safety trade-off (where `add`/`remove` live) and **implement** tree operations
   (totals, counts, depth-first traversal, search with a path, indented rendering).
3. **Implement** Bridge by composing an abstraction with an implementor interface, and **refactor** an N × M class
   explosion into N + M classes; **use** a lambda as the implementor when it has one method.
4. **Design** a Facade that turns a multi-step subsystem workflow (including failure compensation) into one call, and
   **explain** why a facade simplifies but does not forbid direct subsystem access.
5. **Implement** a thread-safe Flyweight factory, **separate** intrinsic from extrinsic state, and **measure** the
   effect by counting distinct instances.
6. **Explain** the JDK's own flyweights (`Integer.valueOf` cache, `Boolean.valueOf`, `Currency.getInstance`) and the
   rules for value-based classes (compare with `equals`, never synchronize on them, identity is not guaranteed).

## Prerequisites

m04 (Adapter, Decorator, Proxy — wrapping one object), m03 (immutable records, static factories with caching from
m02), m01 (composition over inheritance, OCP), m00 (sealed types, records, record patterns, exhaustive `switch`,
unnamed variables `_`, virtual threads).

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Composite (Bileşik) | `Component` interface; `Leaf`; `Composite` with a child list and `add`/`remove`; operations as polymorphic methods | `sealed interface` + `record` nodes; operations as recursive `switch` with record patterns (no `default`); immutable trees via `List.copyOf`; transparency vs. safety discussion | sealed (409), records (395), record patterns (440), pattern matching for `switch` (441), unnamed variables (456) |
| Bridge (Köprü) | `Abstraction` holding an `Implementor` reference; refined abstractions; concrete implementors | implementor as a functional interface (lambda / method reference); abstraction as a small class composed at the composition root; N + M instead of N × M | lambdas, method refs, records, interfaces |
| Facade (Cephe) | one class with high-level methods calling several subsystem classes | facade result as a `sealed` outcome (`Placed` / `Rejected`) instead of exceptions for expected business failures; injected subsystems so tests use fakes | sealed (409), records, pattern matching for `switch` (441) |
| Flyweight (Sinek Siklet) | `FlyweightFactory` with a map; intrinsic state in the flyweight, extrinsic state passed in | immutable `record` flyweights; `ConcurrentHashMap.computeIfAbsent` as a thread-safe factory; JDK caches (`Integer.valueOf`, `Boolean.valueOf`, `Currency.getInstance`); value-based classes (`@jdk.internal.ValueBased`, javac `[identity]` lint); JEP 534 compact object headers make every object smaller on JDK 27 but do not replace sharing | records, `ConcurrentHashMap`, virtual threads (444) for the thread-safety test |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m05.examples`. Every demo prints deterministic output (no random
without a fixed seed, no timestamps, no memory numbers — Flyweight demos print **instance counts**, not bytes).
All subsystems and "services" are in-memory fakes; nothing touches the file system, network or a real database.

Task **M05-2a** (#32) — Composite & Bridge:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `composite.orgchart` — `Employee` (interface: `name()`, `salary()`, `headcount()`, `print(indent)`), `Engineer`, `Designer` (record leaves), `Manager` (composite with `add`/`remove`, mutable child list, `ownSalary()`) | `OrgChartDemo` | company org chart | Classic GoF Composite, "safe" variant: `add`/`remove` only on `Manager`; client calls `salary()` on a department or a single person alike | department salary cost sums the whole subtree; headcount includes the manager; `remove` shrinks both; adding a manager under their own report is rejected (no cycles); `print` gives the exact indented chart; `reports()` returns an unmodifiable view |
| `composite.filesystem` — `sealed interface FsNode permits File, Directory`; `record File(String name, long bytes)`, `record Directory(String name, List<FsNode> children)` (+ `Directory.of(name, children...)`); `FsOps` (static `size`, `fileCount`, `depth`, `find`, `render`) | `FileSystemDemo` | file system tree | Modern Composite: data as sealed records, operations as recursive exhaustive `switch` with record patterns; adding an operation needs no change to the node types | total size and file count over nested directories; empty directory has size 0; `find` by predicate returns paths like `/src/main/App.java` in depth-first order; `render` gives the exact tree text; directory children are an immutable copy; duplicate child names and negative sizes rejected |
| `composite.expression` — `sealed interface Expr permits Num, Add, Mul, Neg`; records; `Expressions.evaluate`, `Expressions.render` | `ExpressionDemo` | arithmetic expressions | A Composite whose leaves are numbers and whose composites are operators; the same tree gives a value and a text | `evaluate` of nested expressions; `render` adds parentheses only where precedence needs them (`(1 + 2) * 3` vs. `1 + 2 * 3`); `Neg` of `Neg` renders `-(-x)`; deep tree (depth 1 000) evaluates without special cases |
| `bridge.shapes` — abstraction `Shape` (abstract class holding a `Renderer`), `Circle`, `Rectangle`; implementor `Renderer` (`circle(...)`, `rectangle(...)`), `SvgRenderer`, `AsciiRenderer` | `ShapesDemo` | drawing | Minimal canonical Bridge: 2 shapes × 2 renderers with 2 + 2 classes instead of 4 subclasses | exact SVG and ASCII output for each combination; a test-only `RecordingRenderer` receives the primitive calls; adding a renderer in the test needs no change to `Shape` |
| `bridge.remote` — implementor `Device` (`Tv`, `Radio`), abstraction `RemoteControl` (`togglePower`, `volumeUp`, `volumeDown`, `channelUp`), refined `AdvancedRemote` (`mute`, `unmute`, `saveFavourite(name)`, `goTo(name)`) | `RemoteDemo` | home devices | Classic GoF Bridge: the remote hierarchy and the device hierarchy grow independently | every remote works with every device; volume clamps to 0–100; `mute`/`unmute` restores the previous volume; channel wraps after the device's last channel; a powered-off device ignores volume changes |
| `bridge.alerts` — abstraction `AlertService` with `UrgentAlerts` (send now, `[URGENT]` prefix) and `DigestAlerts` (buffer, `pending()`, `flush()` sends one combined message); implementor `MessageChannel` (functional interface) with `EmailChannel`, `SmsChannel` (160-char limit), both handing the final text to an injected `Consumer<String>` transport | `AlertsDemo` | monitoring alerts | Realistic Bridge with a lambda implementor: *how often/what* to send is independent of *where* to send | urgent alert reaches the channel immediately; digest sends nothing until `flush`, then one message listing all alerts in order; SMS truncates to 160 chars with `…`; a lambda channel works without a new class |

Task **M05-2b** (#33) — Facade & Flyweight:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `facade.hometheater` — subsystems `Amplifier`, `Projector`, `Screen`, `Lights`, `StreamingPlayer` (each records its actions into an injected `ActionLog`); `HomeTheaterFacade` (`watchMovie(title)`, `endMovie()`) | `HomeTheaterDemo` | home cinema | Minimal canonical Facade: ten subsystem calls in the right order behind two methods | `watchMovie` produces the exact ordered action list; `endMovie` shuts down in reverse order; `endMovie` without a running movie does nothing; subsystems remain usable directly (facade does not hide them) |
| `facade.checkout` — subsystems `Inventory`, `PaymentGateway`, `ShippingService` (in-memory fakes whose steps return `Optional` ids: empty = expected failure); records `Cart` (+ `Cart.Line`), `Card`, `Address`; `CheckoutFacade.placeOrder(Cart, Card, Address)` returning `sealed interface CheckoutResult permits Placed, Rejected` (`Rejected.Reason` enum) | `CheckoutDemo` | online shop | Realistic Facade that also owns the *compensation* logic: reserve stock → charge → ship; undo earlier steps when a later one fails | happy path reserves, charges the exact total and returns a tracking number; out of stock → `Rejected(OUT_OF_STOCK)` and no charge; payment declined → reservation released; shipping failure → payment refunded and reservation released; stock never goes negative |
| `flyweight.glyphs` — `record Glyph(char symbol, String font, int size)` (intrinsic); `GlyphFactory` (`ConcurrentHashMap.computeIfAbsent`, `created()` counter); `TextDocument` stores `(Glyph, row, column)` positions (extrinsic) as `TextDocument.Placement` records | `TextEditorDemo` | text editor | Canonical Flyweight: thousands of characters on screen, one glyph object per (character, font, size) | typing the same text twice creates no new glyphs; `created()` equals the number of distinct (char, font, size) keys; same key → same instance (`isSameAs`); different font → different instance; 1 000 virtual threads asking for the same key create exactly one glyph |
| `flyweight.forest` — `record TreeType(String species, String colour, String texture)` (shared), `record Tree(int x, int y, TreeType type)`, `TreeTypeFactory`, `Forest` (takes a `Forest.TypeSource`: `factory::typeOf` shares, `TreeType::new` is the naive baseline; `plantGrid(columns, rows)`); `ForestDemo shared\|naive` builds one forest and waits for Enter for the manual `jcmd` measurement | `ForestDemo` | game world | Realistic Flyweight: 100 000 trees planted deterministically share 3 tree types; the demo prints tree count vs. distinct `TreeType` instances (identity-based count) | 100 000 trees, exactly 3 distinct `TreeType` instances (counted with an identity set); a naive forest (new `TreeType` per tree, same class, for comparison) has 100 000; rendering a region lists trees in (y, x) order |
| `flyweight.jdk` — `JdkFlyweights` (helpers that report identity facts) | `JdkFlyweightsDemo` | JDK caches & value-based classes | The JDK's own flyweights: `Integer.valueOf` for −128..127, `Boolean.valueOf`, `Character.valueOf` for `\u0000`..`\u007f`, `Currency.getInstance`; why `==` on value-based classes (`Integer`, `LocalDate`, `Optional`) is a bug | tests assert **only guaranteed** facts: boxing −128..127 is identical (JLS §5.1.7); `Boolean.valueOf(true) == Boolean.TRUE`; `Currency.getInstance("EUR")` is one instance; `equals` is always right. Outside the guaranteed range (e.g. 128) the demo prints the result with "not guaranteed" and **no test asserts it** |

**JDK behaviour verified on JDK 27+35 (2026-09-29, scratch files outside the repo):**

- `Integer.valueOf(127) == Integer.valueOf(127)` and `Integer.valueOf(-128) == …` are `true`; `128` and `-129` are
  `false` with default flags (the upper bound is tunable with `-XX:AutoBoxCacheMax`, so tests must not assert it).
  `Long`/`Short` 127 and `Character` 127 are cached, `Character` 128 is not; `Byte.valueOf` caches every value.
- `Currency.getInstance("EUR") == Currency.getInstance(Locale.GERMANY)` is `true`.
- `java.time.LocalDate` carries the `jdk.internal.ValueBased` annotation. `javac -Xlint:all -Werror` **rejects**
  `synchronized` on a `LocalDate` or `Integer` with `warning: [identity] attempt to synchronize on an instance of a
  value-based class` — so the course build (`-Werror`) makes this a compile error. The lesson shows this diagnostic
  as quoted compiler output; no example file contains the offending code.
- `Integer.valueOf(a) == Integer.valueOf(b)` compiles cleanly under `-Xlint:all -Werror` (no lint catches `==` on
  boxed values) — the lesson warns about this explicitly.
- `-XX:+PrintFlagsFinal` shows `UseCompactObjectHeaders = true {default}` (JEP 534). Any heap figure in the lesson is
  labelled "measured on JDK 27 with compact object headers"; tests never assert bytes.
- A sealed interface with record `Leaf`/`Dir` nodes and a recursive `switch` using record patterns and `_`
  (`case Leaf(var _, var s) -> s`) compiles under `-Xlint:all -Werror` with no `default` branch.
- `java.awt.Container` is a subclass of `java.awt.Component` (the JDK's classic Composite);
  `java.util.logging.Handler.setFormatter(Formatter)` exists (bridge-like Handler × Formatter).

**Real-world usage the lesson cites (no code, no network):** Composite — `java.awt.Component`/`Container`,
`javax.swing.JComponent`, `java.nio.file` directory trees walked with `Files.walk`; Bridge — JDBC (`java.sql`
interfaces as abstraction, vendor drivers as implementors), `java.util.logging.Handler` × `Formatter`; Facade —
`java.net.http.HttpClient` (one object hiding connection pooling, HTTP/2 negotiation and redirects),
`DriverManager.getConnection`; Flyweight —
`Integer.valueOf`, `String` literals and `intern()`, `Currency`, `EnumSet`/enum constants.

## Assignments

### ex01 — Restaurant menu Composite

- **Goal:** model a part–whole hierarchy with sealed records and write tree operations as recursive exhaustive
  `switch` expressions (totals and traversal).
- **Given (do not modify):** `sealed interface MenuComponent permits MenuItem, Menu`; record
  `MenuItem(String name, int priceCents, boolean vegetarian)` (name non-blank, price ≥ 0); record
  `Menu(String name, List<MenuComponent> children)` (children copied with `List.copyOf`; duplicate child names
  rejected; `Menu.of(name, children...)`); interface `MenuQueries` with `int itemCount(MenuComponent)`, `int totalCents(MenuComponent)`,
  `List<String> itemNames(MenuComponent)`, `List<MenuItem> vegetarian(MenuComponent)`,
  `Optional<String> pathTo(MenuComponent root, String itemName)`, `String render(MenuComponent)`.
- **Rules:** traversal is depth-first in menu order; `pathTo` returns names joined with ` > ` (e.g.
  `Dinner > Desserts > Tiramisu`); `render` prints one line per node, two spaces of indent per level, menus as their
  name, items as `- <name> <euros>.<cents>` with `(v)` for vegetarian; a single `MenuItem` is a valid tree; `null`
  arguments throw `NullPointerException`.
- **Student writes:** `MenuReport implements MenuQueries` (no `instanceof` chains — the brief asks for a `switch`
  over the sealed type; the tests check behaviour only).
- **Acceptance criteria (contract tests):** `singleItemIsItsOwnTree`, `emptyMenuHasNoItemsAndZeroTotal`,
  `countsItemsInNestedMenus`, `totalsPricesAcrossAllLevels`, `listsItemNamesDepthFirstInMenuOrder`,
  `filtersVegetarianItemsInOrder`, `findsPathToNestedItem`, `pathToUnknownItemIsEmpty`, `rendersIndentedMenu`,
  `rejectsNullArguments`.

### ex02 — Flyweight map tiles

- **Goal:** share intrinsic tile data among many cells and measure the saving by counting distinct instances before
  and after.
- **Given (do not modify):** `enum Terrain { GRASS, SAND, FOREST, WATER, MOUNTAIN }` carrying the terrain table
  (`symbol()`, `movementCost()`, `walkable()`, so starter, solution and `NaiveTileMap` share one source); record
  `TileType(Terrain terrain, char symbol, int movementCost, boolean walkable, List<String> sprite)` (the 8 × 8 sprite
  makes each instance "heavy") with `TileType.of(Terrain)` building a new instance; `Sprites.forTerrain(Terrain)`; record `Point(int x, int y)`; interface
  `TileTypeRegistry` (`TileType typeOf(Terrain)`, `int createdCount()`); interface `TileMap` (`int width()`,
  `int height()`, `TileType typeAt(int x, int y)`, `void paint(int x, int y, Terrain)`,
  `int movementCost(List<Point> path)`, `String render()`); `NaiveTileMap implements TileMap` (the "before":
  correct, but a new `TileType` per cell); `ObjectCounter.distinctInstances(TileMap)` (identity-based count over all
  cells).
- **Rules:** terrain table — GRASS `.` cost 1, SAND `:` cost 2, FOREST `T` cost 3 (all walkable), WATER `~` and
  MOUNTAIN `^` not walkable; a new map is all GRASS; `movementCost` sums the costs of the tiles on the path and throws
  `IllegalArgumentException` for a non-walkable tile; coordinates outside the map throw
  `IndexOutOfBoundsException`; `render` returns one line of symbols per row; the registry must be safe to call from
  many threads and create each `TileType` at most once.
- **Student writes:** `CachingTileTypeRegistry implements TileTypeRegistry` and
  `SharedTileMap(int width, int height, TileTypeRegistry) implements TileMap`.
- **Acceptance criteria (contract tests):** `registryReturnsTheSameInstanceForTheSameTerrain`,
  `registryCreatesEachTypeAtMostOnce`, `registryIsSafeUnderManyVirtualThreads`, `newMapIsAllGrass`,
  `paintChangesOnlyOneCell`, `sharedMapHoldsOneInstancePerTerrainUsed` (256 × 256 map with three terrains: the naive
  baseline has 65 536 distinct instances, the student's map exactly 3), `mapsShareTypesThroughTheRegistry`,
  `movementCostSumsThePath`, `pathThroughWaterIsRejected`, `rendersRowsOfSymbols`,
  `rejectsCoordinatesOutsideTheMap`.

## Quiz topics

Composite: uniform treatment of leaf and composite; transparency vs. safety (where `add` lives); adding a node type vs.
adding an operation (classic polymorphism vs. sealed + `switch`) — the expression problem in one question; why records
make immutable trees easy and what `List.copyOf` protects. Bridge: the N × M problem; Bridge vs. Adapter (designed up
front vs. retrofitted) and vs. Strategy (structure vs. behaviour); JDBC as a bridge. Facade: Facade vs. Adapter vs.
Mediator; why a facade should not become a "god object"; where compensation logic belongs. Flyweight: intrinsic vs.
extrinsic state; why flyweights must be immutable; thread-safe factories; the `Integer` cache range and why `==` on
boxed values is a bug; what "value-based class" means; what JEP 534 changes (smaller headers for *all* objects) and
what it does not (duplicate objects still cost memory).

## Out of scope

Visitor over the Composite tree (m08 — sealed types + pattern matching replace it there); parsing and variables in the
expression tree (m08 Interpreter); `Files.walk` over a real file system (the lesson mentions it; examples stay
in-memory); Swing/AWT code (cited only — no GUI in examples); real HTTP/JDBC facades; byte-level memory assertions in
tests; JOL or any other measurement library (would be a new dependency); Project Valhalla value classes (not in
JDK 27 — lesson sidebar at most).

## Decisions (owner, 2026-09-29)

All questions below were answered **yes**: the recommended defaults apply.

1. **Flyweight memory numbers:** demos and tests count *instances* only (identity sets), never bytes. The lesson adds
   one manual, non-graded measurement recipe (`jcmd <pid> GC.class_histogram` on `ForestDemo` naive vs. shared) and
   quotes its JDK 27 numbers labelled "compact object headers on (JEP 534)". **Recommended default:** yes, one quoted
   measurement in the lesson, no tests on bytes, no new dependency.
2. **Classic Composite variant:** the org chart uses the GoF "safe" form (`add`/`remove` only on `Manager`), and the
   lesson explains the "transparent" form (on the component, leaves throw `UnsupportedOperationException`) with a
   diagram but without a second example. **Recommended default:** safe form in code, transparent form discussed only.
3. **Pattern count:** Composite and Bridge get 3 examples each, Facade 2, Flyweight 3 (including the JDK caches);
   `composite.expression` is deliberately limited to evaluate + render so m08 (Interpreter) can extend the idea
   without repeating it. **Recommended default:** keep as listed.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m05-structural-composition verify` green
- [ ] Lesson EN + TR + PDFs (Flyweight numbers labelled JDK 27 / JEP 534); `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
