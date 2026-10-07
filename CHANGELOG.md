# Changelog

All notable changes to this course are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and versions follow [Semantic Versioning](https://semver.org/).

## [1.0.0] — 2026-10-07

First complete release of **Design Patterns in Java (Java 27)** — a bilingual (English + Turkish) university course for
the Fall 2026 semester, built with spec-driven development. Every module has an approved spec in `specs/`, runnable
examples with tests, a lesson (Markdown + PDF, EN + TR), and two assignments with starter code, shared contract tests
and reference solutions.

### Added

- **Foundation:** Maven multi-module build on JDK 27 with the Maven Wrapper (`-Xlint:all -Werror`), JUnit + AssertJ,
  the exercise profile and contract-test pattern (`scripts/check-starters.sh`), the Markdown → Typst PDF pipeline with
  Mermaid diagrams (`scripts/build-pdf.sh`), EN/TR parity and code ↔ source checks (`scripts/check-docs.sh`), CI on
  Linux, macOS and Windows, and the Java 27 feature audit (`docs/java27-features.md`).
- **Modules** (each: spec, examples, tests, EN/TR lesson + PDFs, two assignments):
  - `m00` Setup & Modern Java — records, sealed types, pattern matching, lambdas, streams, the source launcher.
  - `m01` OOP, SOLID & UML — SOLID before/after refactors, composition over inheritance, Mermaid UML, GoF map.
  - `m02` Creational I: Factories — Singleton (and why it is often an anti-pattern), Static Factory Method,
    Factory Method, Abstract Factory, `ServiceLoader`.
  - `m03` Creational II: Construction — Builder (classic, record, step), Prototype, Object Pool, DI as creation.
  - `m04` Structural I: Wrappers — Adapter, Decorator (incl. I/O streams), Proxy (virtual, protection, caching,
    dynamic).
  - `m05` Structural II: Composition — Composite (sealed trees), Bridge, Facade, Flyweight.
  - `m06` Behavioral I: Algorithms — Strategy, Template Method, Command (undo/redo), Iterator and Stream Gatherers.
  - `m07` Behavioral II: Communication — Observer (incl. `Flow`), Mediator, Chain of Responsibility, Memento.
  - `m08` Behavioral III: State & Structure — State, Visitor vs. sealed types + pattern matching, Interpreter.
  - `m09` Functional & Data-Oriented — data-oriented programming, `Result` types, patterns that became language
    features.
  - `m10` Concurrency — virtual threads, Producer–Consumer, Guarded Suspension, Balking, Immutable Object, Scoped
    Values, `CompletableFuture`, Structured Concurrency (**preview**, isolated in one package).
  - `m11` Architecture & Enterprise — manual DI, Repository, Ports & Adapters, domain events, anti-patterns,
    refactoring to patterns, test doubles, ArchUnit rules.
- **Capstone "PatternShop":** student brief, 100-point rubric and walkthrough guide (EN/TR + PDFs), a starter with the
  GIVEN API, 83 acceptance tests and 7 architecture rules, and a reference solution that passes all of them using
  13 counted patterns plus Immutable Object (`capstone/`, `specs/SPEC-capstone.md`).
- **Docs:** EN/TR README, syllabus, glossary, contributing guide, issue/PR templates, MIT (code) and CC BY 4.0 (text)
  licences, and this changelog.

### Quality at release

- `./mvnw verify` on JDK 27: 1 919 tests, 0 failures; coverage profile green.
- All 35 exercise suites (24 module + 11 capstone) compile and fail on their starters, as designed.
- `scripts/check-docs.sh`: 0 problems (EN/TR parity, links, every lesson code block matches its source file).
- 149 module demos run with `java <File>.java`; the capstone CLI demo matches the guide's transcript.
- Full review pass (R1) of every module and the capstone; findings fixed, including two concurrency/state bugs in
  m03 and m07 and the capstone checkout rollback.

[1.0.0]: https://github.com/aliturgutbozkurt/design-patterns-in-java/releases/tag/v1.0.0
