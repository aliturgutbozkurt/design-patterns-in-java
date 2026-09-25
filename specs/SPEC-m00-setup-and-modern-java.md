# Spec: m00-setup-and-modern-java — Setup & Modern Java

> Status: **APPROVED** (owner approval via `/build auto`, 2026-09-25) · Parent: [SPEC.md](../SPEC.md) §2 · Week: 1 · Task: #7
> Pilot module: proves the per-module template end-to-end (plan checkpoint "Pilot").

## Objective

Get every student to a working JDK 27 environment and give them the modern-Java vocabulary the rest of the course
relies on: records, sealed hierarchies, pattern matching, lambdas and streams. Each feature is introduced with an eye on
the patterns it will later simplify.

## Learning outcomes

After this module a student can:

1. **Install** JDK 27, verify it, and **run** Java code without a build tool (single-file, compact source file,
   multi-file source launcher).
2. **Model** immutable values with records, including validation in compact constructors and derived constructors.
3. **Model** a closed set of alternatives with a sealed interface and **process** it with an exhaustive `switch` using
   record patterns, guards and unnamed patterns.
4. **Use** lambdas, method references and standard functional interfaces, and **write** small stream pipelines
   (including a Stream Gatherer).
5. **Explain** which later patterns these features support (value objects, Composite, Visitor, Strategy).

## Prerequisites

Java basics: classes, interfaces, collections, exceptions. No prior knowledge of Java 16+ features.

## Topics

| Topic | Java features | Previews pattern(s) |
|---|---|---|
| Running Java without a build | JEP 458 multi-file launcher, JEP 512 compact source files + instance `main` + `IO` | — |
| Records as value objects | JEP 395 records, compact constructors, JEP 513 flexible constructor bodies | Value Object, Builder (m03), Memento (m07) |
| Sealed hierarchies + pattern matching | JEP 409, JEP 440, JEP 441, JEP 456 (`_`) | Composite (m05), Visitor/Interpreter (m08), DOP (m09) |
| Functions & streams | lambdas, method refs, `java.util.function`, streams, JEP 431 sequenced collections, JEP 485 gatherers | Strategy, Command (m06) |

## Examples

Package root: `io.github.aliturgutbozkurt.patterns.m00.examples`. Every demo prints deterministic output.

| Package / file | Demo | Domain | What it shows | Tested behaviour |
|---|---|---|---|---|
| `first-steps/Hello.java` (outside Maven source tree) | — | greeting | Compact source file, instance `main`, `IO.println` | launched via source launcher in a test; prints expected text |
| `first-steps/multifile/Main.java` + `Greeter.java` | — | greeting | Multi-file source launcher (JEP 458), no build | launched via source launcher in a test |
| `records.Money` | `MoneyDemo` | shopping | Record value object: validation, normalisation (scale 2), arithmetic returning new values, equality | rejects null/negative, adds same-currency amounts, refuses mixed currencies, equal by value |
| `records.Percentage` | `PercentageDemo` | discounts | Flexible constructor body: parse & validate a `"15%"` string *before* `this(...)` | parses valid input, rejects `"120%"` and garbage with clear messages |
| `sealed.Shape` (+ `Circle`, `Rectangle`, `Triangle`) | `ShapeDemo` | geometry | Sealed interface of records; exhaustive `switch` with record patterns, guard (`when`), `_` | area/perimeter per shape; degenerate shapes described specially |
| `sealed.Json` (+ `JsonNull`, `JsonBool`, `JsonNumber`, `JsonString`, `JsonArray`, `JsonObject`) | `JsonDemo` | configuration | Recursive sealed tree + nested record patterns → renderer (Composite/Interpreter preview) | renders nested values to compact JSON; escapes strings |
| `functional.OrderLine` + `OrderStats` | `OrderStatsDemo` | shop orders | Lambdas, method refs, `Comparator.comparing`, grouping/summing, `reversed()`/`getFirst()`, `Gatherers.windowFixed` | revenue per category, top product, rolling batches |
| `functional.TextPipeline` | `TextPipelineDemo` | text processing | `Function` composition (`andThen`), `Predicate` combinators, passing behaviour as data (Strategy preview) | composed pipeline output; predicate combinations |

## Assignments

### ex01 — Temperature value object

- **Goal:** practise records, compact-constructor validation and value semantics.
- **Given (do not modify):** `Unit` enum (`CELSIUS`, `FAHRENHEIT`, `KELVIN`), `Temperature` interface
  (`value()`, `unit()`, `to(Unit)`).
- **Student writes:** record `TemperatureReading(double value, Unit unit) implements Temperature`.
- **Acceptance criteria (contract tests):** `isARecord`, `convertsCelsiusToFahrenheit`, `convertsFahrenheitToKelvin`,
  `convertingToSameUnitReturnsEqualValue`, `rejectsTemperaturesBelowAbsoluteZero`, `rejectsNaN`, `rejectsNullUnit`,
  `equalWhenValueAndUnitEqual`.

### ex02 — Payment fees with a sealed hierarchy

- **Goal:** practise exhaustive `switch` with record patterns and guards over a sealed type.
- **Given (do not modify):** `sealed interface Payment permits CardPayment, BankTransfer, WalletPayment` (records),
  `FeeCalculator` interface.
- **Rules:** card 2.9 % + 0.30, plus 1 % if `international`; bank transfer 0.50 flat, free when amount ≥ 1000;
  wallet 1.5 % capped at 5.00. Fees are `BigDecimal`, scale 2, `HALF_EVEN`.
- **Student writes:** `PaymentFees implements FeeCalculator` using one `switch` with record patterns, no `default`.
- **Acceptance criteria (contract tests):** `domesticCardFee`, `internationalCardFee`, `bankTransferFlatFee`,
  `bankTransferFreeFromThousand`, `walletFeeCapped`, `walletFeeBelowCap`, `feesUseBankersRounding`, `rejectsNull`.

## Quiz topics

Record equality and immutability (shallow!); what a compact constructor can/can't do; `permits` and exhaustiveness;
why no `default` on sealed switches; unnamed pattern `_`; source launcher vs. build tool.

## Out of scope

Modules (JPMS), generics deep-dive, Stream internals, preview features.

## Success criteria

- [ ] All examples run with the source launcher and have tests; `./mvnw -pl modules/m00-setup-and-modern-java verify` green
- [ ] Lesson EN + TR + PDFs; `scripts/check-docs.sh` clean
- [ ] `scripts/check-starters.sh modules/m00-setup-and-modern-java`: both starters fail, both solutions pass
