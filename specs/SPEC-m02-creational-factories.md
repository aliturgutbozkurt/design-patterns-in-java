# Spec: m02-creational-factories — Creational I: Factories

> Status: **APPROVED** (owner, 2026-09-29) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 3 · Task: #16

## Objective

Teach students to take object creation out of the code that *uses* objects. After this module a student can choose
between a constructor, a static factory method, a Factory Method, an Abstract Factory and a `ServiceLoader` plugin,
explain the trade-offs, and recognise when a Singleton is a design smell that dependency injection (m01 DIP) removes.

## Learning outcomes

After this module a student can:

1. **Implement** a thread-safe Singleton (enum, lazy holder) and **explain** why global state hurts testability; then
   **refactor** a Singleton dependency into constructor injection.
2. **Write** static factory methods with meaningful names (`of`, `from`, `parse`, `valueOf`), instance caching and
   subtype selection, and **compare** them with public constructors.
3. **Implement** Factory Method in its classic (subclass overrides a creation hook) and modern (`Supplier` / registry)
   forms.
4. **Implement** Abstract Factory for a family of related products and **explain** how it keeps a family consistent.
5. **Load** implementations at run time with `ServiceLoader` and a `META-INF/services` file.
6. **Decide** which creational technique fits a given problem — including "just call the constructor".

## Prerequisites

m01: SOLID, especially OCP (extension points) and DIP (composition root, injection). m00: records, sealed types,
lambdas.

## Topics / patterns

| Topic | Classic form | Modern Java 27 angle | Java features (see docs/java27-features.md) |
|---|---|---|---|
| Singleton (Tekil Nesne) | private constructor + `static getInstance()` | `enum` singleton; lazy holder idiom; DI instead of global access. **Sidebar:** Lazy Constants (JEP 531, preview — no graded code) | enums, class initialization guarantees, virtual threads (concurrency test) |
| Static Factory Method (Statik Fabrika Metodu) | `static X of(...)` with a private constructor | records with factories; caching like `Integer.valueOf`; returning sealed subtypes like `EnumSet.of` / `List.of` | records, sealed (409) |
| Factory Method (Fabrika Metodu) | abstract creator + `createProduct()` override | `Supplier<T>` / constructor references; enum-keyed registry | lambdas, method refs, `EnumMap` |
| Abstract Factory (Soyut Fabrika) | interface of creation methods per family | families as small factory implementations; selecting the family once in the composition root | interfaces, records, `switch` on enum |
| `ServiceLoader` | — (JDK plugin mechanism) | provider interface + `META-INF/services`; lazy `ServiceLoader.Provider` stream | `ServiceLoader.stream()` |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m02.examples`. Every demo prints deterministic output.
The Singleton examples are the only place in the course where mutable static state is allowed (CLAUDE.md §5); the
lesson discusses why.

Task **M02-2a** (#17) — Singleton & Static Factory Method:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `singleton.enumsingleton` — `enum AppSettings { INSTANCE }` | `EnumSingletonDemo` | application settings | The simplest correct Singleton: the JVM guarantees one instance, thread safety, serialization and reflection safety | same instance everywhere; serialize → deserialize returns the same instance; `Constructor.newInstance` is impossible |
| `singleton.holder` — `CurrencyTable` with a private static `Holder` class | `HolderSingletonDemo` | exchange rates | Lazy holder idiom: created on first `getInstance()`, thread-safe without `synchronized` | not created before first use (creation counter); 1 000 virtual threads calling `getInstance()` see one instance created once |
| `singleton.testability.before` — `SequenceGenerator` (Singleton), `OrderService` | `SingletonTestabilityDemo` | order numbers | Hidden global dependency: tests leak state into each other | test **documents** the leak: the second test's first order is not `ORD-1` |
| `singleton.testability.after` — `IdSource` (functional interface), `SequentialIds`, `OrderService` | `SingletonTestabilityDemo` | order numbers | "One instance" becomes a decision of the composition root, not a property of the class | fixed-id lambda gives predictable numbers; two services with their own `SequentialIds` do not interfere |
| `staticfactory.Money` — `of`, `zero`, `parse` | `StaticFactoryDemo` | shopping | Named factories vs. constructors; `parse("12.50 EUR")` | same as constructor result; `parse` rejects garbage with a clear message |
| `staticfactory.Temperature` — `ofCelsius`, `ofFahrenheit`, `ofKelvin` | `StaticFactoryDemo` | weather | Names distinguish factories whose parameter lists are identical (`double`) — impossible with overloaded constructors | conversions; all three agree on 0 °C |
| `staticfactory.Percentage.of(int)` | `StaticFactoryDemo` | discounts | Instance caching: `of(0..100)` returns cached instances like `Integer.valueOf` | `isSameAs` for cached values; out-of-range rejected |
| `staticfactory.Shipment.forWeight(grams)` → `sealed interface Shipment permits Letter, Parcel, Freight` | `StaticFactoryDemo` | postal service | A factory may return a subtype chosen from the input, hidden from the caller (like `EnumSet.of`) | boundaries 500 g / 30 kg pick the right subtype; price per subtype |

Task **M02-2b** (#18) — Factory Method, Abstract Factory, `ServiceLoader`:

| Package | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `factorymethod.export` — `Report` (record), `Formatter` + `MarkdownFormatter`, `HtmlFormatter`, `CsvFormatter` (shared); `factorymethod.export.classic` — `abstract DocumentExporter` with `createFormatter()`; `MarkdownExporter`, `HtmlExporter`, `CsvExporter` | `ExportDemo` | report export | Classic GoF: the creator's template method calls an overridable creation hook | each format's exact output; HTML escapes `<`/`&`; CSV quotes cells containing commas |
| `factorymethod.export.modern` — `enum ExportFormat` holding `Supplier<Formatter>`; `Exporter` | `ExportDemo` | report export | The same variation with constructor references instead of subclasses | same output as the classic version for every format |
| `factorymethod.logistics` — `Cargo` (record), `Logistics` (abstract, `createTransport()`), `RoadLogistics`, `SeaLogistics`; `Transport` → `Truck`, `Ship` | `LogisticsDemo` | delivery planning | The creator's business logic (`planDelivery`) is written once against `Transport` | plan text, cost and days per mode; adding `AirLogistics` in the test needs no change to `Logistics` |
| `abstractfactory.ui` — `Widget` → `Button`, `Checkbox`, `TextField`; `WidgetFactory` (`button`, `checkbox`, `textField`); `MacWidgets`, `WindowsWidgets` (products as private nested records); `LoginDialog` client; `WidgetFactories.forOs` | `WidgetDemo` | cross-platform UI | A family of products that must match; the client never names a concrete widget | every widget from one factory has the same platform; switching factory switches the whole dialog; rendered text |
| `abstractfactory.cloud` — `CloudFactory` (`storage()`, `queue()`) → `BlobStorage`, `MessageQueue`; `AcmeCloud`, `NimbusCloud` (fictional, sharing package-private in-memory simulations); `ReportArchiver` client | `CloudDemo` | file archiving | Provider families: storage and queue from the same provider share a URI scheme and region | archiver stores and enqueues through either family; URIs are `acme://…` / `nimbus://…`; mixing families is impossible through the factory |
| `serviceloader` — `ExporterProvider` (SPI) creating a `FieldExporter`, `PluginRegistry`; providers `JsonExporterProvider`, `YamlExporterProvider`; `META-INF/services/…ExporterProvider` | `PluginDemo` | export plugins | Discovering implementations at run time without the client naming them | registry finds both providers in a deterministic (sorted) order; unknown format → `Optional.empty()`; provider types listed without instantiating them |

**ServiceLoader and the source launcher (verified 2026-09-29):** the multi-file source launcher (JEP 458) finds
`META-INF/services/…` in the *source root*. The provider-configuration file therefore lives at
`src/main/java/META-INF/services/`, and the module POM adds that one path as a Maven resource so the tests (which run
from `target/classes`) see the same file.

## Assignments

### ex01 — Colour values with static factories

- **Goal:** practise static factory methods: meaningful names, validation, parsing and instance caching.
- **Given (do not modify):** interface `Color` (`red()`, `green()`, `blue()`, `toHex()`).
- **Student writes:** `final class RgbColor implements Color` with a **private** constructor and static factories
  `rgb(int, int, int)`, `hex(String)` (`#RRGGBB` or `#RGB`, case-insensitive) and `named(String)` for the 8 basic
  colours (`black`, `white`, `red`, `green`, `blue`, `yellow`, `cyan`, `magenta`). Named colours are cached, and
  `rgb(...)` returns the cached instance when the value is a named colour.
- **Acceptance criteria (contract tests):** `rgbRejectsComponentsOutsideZeroTo255`, `hexParsesLongForm`,
  `hexParsesShortForm`, `hexIsCaseInsensitive`, `hexRejectsMalformedInput`, `namedColoursAreCached`,
  `rgbReturnsCachedInstanceForNamedColour`, `unknownNameListsKnownNames`, `toHexIsUppercaseSixDigits`,
  `equalByValue`, `hasNoPublicConstructor`.

### ex02 — Game levels with an Abstract Factory

- **Goal:** keep a family of products consistent and let the client work with any family.
- **Given (do not modify):** `enum Biome { FOREST, DESERT }`; interfaces `Enemy` (`name()`, `hitPoints()`,
  `biome()`), `Obstacle` (`name()`, `damage()`, `biome()`), `Reward` (`name()`, `points()`, `biome()`),
  `LevelFactory` (`enemy()`, `obstacle()`, `reward()`); record `Level(List<Enemy>, List<Obstacle>, Reward)`.
- **Rules:** forest = Wolf (30 HP), Fallen log (5 damage), Mushroom (10 points); desert = Scorpion (20 HP),
  Quicksand (12 damage), Water flask (15 points). `LevelGenerator.generate(difficulty)` creates `difficulty` enemies,
  `difficulty` obstacles and one reward, all from its factory; difficulty must be 1–10.
- **Student writes:** `ForestLevelFactory`, `DesertLevelFactory`, `LevelGenerator(LevelFactory)`, and the static
  factory `LevelFactories.forBiome(Biome)` plus `LevelFactories.forName(String)` (case-insensitive).
- **Acceptance criteria (contract tests):** `forestFamilyIsConsistent`, `desertFamilyIsConsistent`,
  `productStatsMatchTheTable`, `generatorUsesOnlyItsFactory`, `difficultyScalesEnemiesAndObstacles`,
  `generatorWorksWithAnyFactory` (a test-only factory), `forBiomeReturnsTheMatchingFamily`,
  `forNameIsCaseInsensitive`, `unknownNameRejected`, `rejectsDifficultyOutsideOneToTen`.
- **Contract note:** the contract drives the generator through `generate(LevelFactory, int)` returning the GIVEN
  `Level`, so the same tests fit the starter's and the solution's `LevelGenerator` classes.

## Quiz topics

Why an `enum` Singleton is serialization- and reflection-safe; why the holder idiom is lazy and thread-safe
(class initialization); what makes Singleton an anti-pattern (hidden dependency, global mutable state, test
interference) and how DI fixes it; advantages of static factories over constructors (names, caching, subtypes) and
their costs (not discoverable, no subclassing with a private constructor); Factory Method vs. a `Supplier`; Abstract
Factory vs. Factory Method; how `ServiceLoader` finds providers; when a plain constructor is the right answer.

## Out of scope

Builder, Prototype, Object Pool and DI as creation (m03); DI frameworks (m11); JPMS `provides`/`uses` (mentioned in
the lesson only); reflection-based factories (`Class.forName(...).newInstance()`); Lazy Constants in graded code
(preview, JEP 531).

## Decisions (owner, 2026-09-29)

1. `META-INF/services` lives under `src/main/java` (so `java PluginDemo.java` works without a build) and the m02
   module POM adds that path as a resource — build configuration only, no new plugin or dependency.
2. Singleton, Static Factory Method, Factory Method and Abstract Factory get ≥ 2 examples each; `ServiceLoader` (a JDK
   mechanism, not a GoF pattern) gets one.

## Success criteria

- [ ] All examples run with `java <File>.java` and have tests; `./mvnw -q -pl modules/m02-creational-factories verify` green
- [ ] Lesson EN + TR + PDFs (with the Lazy Constants sidebar marked preview); `check-docs.sh` clean
- [ ] Assignments: both starters fail, both solutions pass the shared contract tests (`check-starters.sh`)
