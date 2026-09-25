# Spec: Design Patterns in Java — Java 27 Course (Fall 2026)

> Status: **APPROVED** (2026-09-25) · Owner: @aliturgutbozkurt · Created: 2026-09-25
> Workflow: spec-driven development (agent-skills). This file is the root spec; each module gets `specs/SPEC-<id>.md`.

## 1. Objective

Build an open, bilingual (English + Turkish) course that teaches **object-oriented design patterns in modern Java (JDK 27)**
for the Fall 2026 semester (starting September 2026), module by module, ending with a capstone project.

**Users**
- *Student* (2nd–4th year CS/SE, or junior developer): knows Java basics (classes, interfaces, collections, exceptions).
  Wants to understand *when* and *why* to use a pattern, see many runnable examples, practice, and check their work.
- *Instructor*: wants ready lesson material (PDF for print/LMS, Markdown for GitHub), assignments with solutions, and a gradeable capstone.

**User stories**
- As a student, I can clone the repo, install JDK 27, and run any example with one command, without an IDE or build step.
- As a student, I can read each lesson in English or Turkish, online (Markdown) or offline (PDF).
- As a student, I can do an assignment in starter code and run tests that tell me whether my solution is correct.
- As a student, I can compare my work with a reference solution after attempting it.
- As an instructor, I can follow a 14-week plan and grade a capstone against a published rubric.

**Pedagogical stance**
- Every pattern is taught as *Problem → Intent → Structure → Classic Java → Modern Java 27 → Real-world use → Pitfalls/When not to use*.
- Show how modern Java (records, sealed types, pattern matching, lambdas, virtual threads) simplifies or replaces classic GoF forms.
- "Many examples": each pattern has **≥ 2 runnable examples** (a minimal canonical one + a realistic domain one), plus a
  modern-Java variant where it differs meaningfully.

## 2. Capability map (module ids are stable — never rename)

| # | Module id | Content | Depends on |
|---|---|---|---|
| 0 | `m00-setup-and-modern-java` | JDK 27 setup, Maven wrapper, source launcher, IDE; records, sealed, pattern matching, lambdas, streams recap; how to use this course | — |
| 1 | `m01-oop-solid-uml` | OOP pillars, composition over inheritance, SOLID, coupling/cohesion, UML class & sequence diagrams (Mermaid), GoF catalog overview | m00 |
| 2 | `m02-creational-factories` | Singleton (enum, holder, and why it's often an anti-pattern), Static Factory Method, Factory Method, Abstract Factory, `ServiceLoader` | m01 |
| 3 | `m03-creational-construction` | Builder (classic, record + builder, step builder), Prototype (copy constructors vs `clone`), Object Pool, DI as creation | m02 |
| 4 | `m04-structural-wrappers` | Adapter, Decorator (incl. I/O streams), Proxy (virtual, protection, caching, `java.lang.reflect.Proxy`) | m03 |
| 5 | `m05-structural-composition` | Composite (sealed trees), Bridge, Facade, Flyweight (incl. value-based classes) | m04 |
| 6 | `m06-behavioral-algorithms` | Strategy (lambdas), Template Method (vs. higher-order functions), Command (undo/redo, queues), Iterator (`Iterable`, `Spliterator`, Stream Gatherers) | m05 |
| 7 | `m07-behavioral-communication` | Observer (listeners, `Flow` API), Mediator, Chain of Responsibility, Memento | m06 |
| 8 | `m08-behavioral-state-structure` | State (enum/sealed state machines), Visitor vs. sealed types + pattern matching, Interpreter (sealed AST with records) | m07 |
| 9 | `m09-functional-data-oriented` | Data-oriented programming, immutability, `Optional`/Result as sealed types, function composition, "patterns that became language features" | m08 |
| 10 | `m10-concurrency-patterns` | Virtual threads (thread-per-task), Producer–Consumer, Structured Concurrency, Scoped Values, Immutable object, Guarded suspension, `CompletableFuture` pipelines | m09 |
| 11 | `m11-architecture-enterprise` | Dependency Injection (manual), Repository, Ports & Adapters (hexagonal), Domain events, anti-patterns, refactoring to patterns, test doubles, ArchUnit rules | m10 |
| C | `capstone` | "PatternShop" order-processing system: spec written by the student, ≥ 10 patterns, tests, architecture rules, bilingual report | m00–m11 |

Build order: `m00 → m01 → … → m11 → capstone` (linear; mirrors the teaching order).
Cross-cutting (Foundation, before m00): build system, test harness, docs/PDF pipeline, CI, repo community files.

### 14-week semester mapping (suggested)
W1 m00 · W2 m01 · W3 m02 · W4 m03 · W5 m04 · W6 m05 · W7 **midterm** · W8 m06 · W9 m07 · W10 m08 · W11 m09 · W12 m10 · W13 m11 · W14 capstone presentations (capstone runs W9–W14).

## 3. Tech stack

| Concern | Choice | Notes |
|---|---|---|
| Language | **Java SE 27** (`--release 27`, class file 71) | Prefer final features; preview only per `docs/java27-features.md` + module spec |
| Build | Maven 3.9.x via **Maven Wrapper** (`./mvnw`) | Multi-module: parent `pom.xml` aggregates `modules/*` and `capstone/*` |
| Tests | JUnit Jupiter (latest stable) + AssertJ | Versions pinned in parent `pom.xml` during task F01 |
| Architecture tests | ArchUnit (m11, capstone only) | Must support class file 71 — verify in F01, else drop to "Ask first" |
| Docs | Markdown (GitHub-flavoured) + Mermaid diagrams | EN + TR per lesson |
| PDF | **pandoc 3.x → Typst** engine, Mermaid pre-rendered with `mermaid-cli` | Typst handles Turkish glyphs + fonts with no LaTeX install; runs locally and in CI |
| CI | GitHub Actions (`actions/setup-java` JDK 27) | build, tests, starter compile, PDF build, docs check |

## 4. Commands

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 27)          # macOS (Linux/Windows: set JAVA_HOME to JDK 27)
./mvnw -q verify                                           # full build: examples + solutions tests
./mvnw -q -pl modules/<module-id> verify                   # one module
./mvnw -q -pl modules/<module-id> test -Pexercises         # student: run exercise tests
scripts/check-starters.sh [modules/<module-id>]             # CI: starters compile and still fail their contracts
java modules/<module-id>/src/main/java/.../<Demo>.java     # run any example, no build needed (JEP 458)
scripts/build-pdf.sh modules/<module-id> | --all            # Markdown → PDF (EN + TR)
scripts/check-docs.sh                                      # EN/TR parity + broken links
scripts/new-module.sh <module-id>                          # scaffold a module from the template
```

## 5. Project structure

```
SPEC.md CLAUDE.md README.md README.tr.md LICENSE pom.xml mvnw .mvn/
specs/SPEC-<module-id>.md          per-module specs
tasks/plan.md tasks/todo.md        plan & task list (tasks ↔ GitHub issues)
docs/                              glossary.md, syllabus.{en,tr}.md, java27-features.md, templates/, pdf/
scripts/                           build-pdf.sh, check-docs.sh, new-module.sh
modules/<module-id>/
  README.md                        bilingual index
  lesson/lesson.{en,tr}.{md,pdf}   lesson text + generated PDFs, img/
  assignments/NN-<slug>.{en,tr}.md
  src/main/java/io/github/aliturgutbozkurt/patterns/<mNN>/{examples,exercises,solutions}/…
  src/test/java/io/github/aliturgutbozkurt/patterns/<mNN>/{examples,exercises,solutions}/…
capstone/                          spec.{en,tr}.md, rubric.{en,tr}.md, starter/, reference/
.github/workflows/ci.yml, ISSUE_TEMPLATE/, PULL_REQUEST_TEMPLATE.md
```

## 6. Code style

See `CLAUDE.md §5`. Summary: modern idiomatic Java 27; `src/main` has no external dependencies (so the source launcher works);
deterministic demo output; English identifiers; `-Xlint:all -Werror`; Javadoc on public types in examples/solutions.

## 7. Testing strategy

| Level | What | Where | Runs in |
|---|---|---|---|
| Example tests | Each example's observable behaviour | `src/test/.../examples` | default build / CI |
| Contract tests | Abstract spec of an assignment | `src/test/.../exNN/*Contract` | — (abstract) |
| Solution tests | Contract bound to reference solution | `*SolutionTest` | default build / CI |
| Exercise tests | Contract bound to starter code (`@Tag("exercise")`) | `*ExerciseTest` | `-Pexercises` (student) |
| Starter check | Starters compile; every `*ExerciseTest` fails on starters | `scripts/check-starters.sh` | CI |
| Architecture | ArchUnit rules (layering, no cycles) | m11, capstone | default build / CI |
| Docs | EN/TR heading parity, links, PDF build | `scripts/check-docs.sh`, `build-pdf.sh` | CI |

Coverage target: ≥ 80 % line coverage for `examples` + `solutions` packages (JaCoCo, **only if** it supports class file 71 — verify in F01).

## 8. Boundaries

- **Always:** build & test on JDK 27 before commit; keep EN/TR in parity; regenerate PDFs with Markdown changes; lesson code comes from compiled files; update spec first when scope changes.
- **Ask first:** new dependencies/plugins; preview features; changing module ids/order/scope; capstone scope; license; CI/labels/milestones changes.
- **Never:** commit secrets or `target/`; put solutions inside starter packages; skip/weaken tests; copy copyrighted text verbatim; mark done without verification.

## 9. Success criteria

1. `./mvnw verify` passes on a clean clone with JDK 27 (locally and in CI on ubuntu + windows + macos).
2. 12 modules (m00–m11) + capstone exist, each meeting the Definition of Done in `CLAUDE.md §10`.
3. Every pattern listed in §2 has ≥ 2 runnable examples with tests; every example runs via `java <File>.java` with no build.
4. Every module has `lesson.en.md`, `lesson.tr.md`, `lesson.en.pdf`, `lesson.tr.pdf`; `check-docs.sh` reports 0 parity/link errors; Turkish characters render correctly in PDFs.
5. Every module has ≥ 2 assignments (EN + TR) with starter code, shared contract tests, and a reference solution; CI proves solutions pass and starters compile.
6. Capstone has student spec + rubric (EN + TR), starter with acceptance tests, a reference solution using ≥ 10 patterns, and ArchUnit rules.
7. Public GitHub repo with README (EN + TR), license, CI badge, and a `v1.0.0` release attaching all PDFs.

## 10. Assumptions (confirmed by owner 2026-09-25)

1. Audience is undergraduate / junior developers who already know basic Java — not absolute beginners.
2. Engineering docs (SPEC, plan, CLAUDE.md, code, identifiers) are English; learner-facing prose is EN + TR.
3. "2026 Eylül müfredatı" = a 14-week Fall 2026 semester; the module list above is the syllabus (no specific university's official syllabus).
4. Maven (not Gradle) is the build tool; Maven Wrapper so students don't need a global Maven.
5. PDFs are committed to the repo (so students can download them directly) and also attached to releases.
6. License: **MIT** for code, **CC BY 4.0** for lesson text — confirmed.
7. Capstone domain: "PatternShop" order processing (CLI + tests, no UI/DB) — confirmed.

## 11. Decisions log & open questions

| # | Question | Decision (2026-09-25) |
|---|---|---|
| 1 | License | MIT (code) + CC BY 4.0 (lesson text) |
| 2 | Capstone domain | PatternShop order processing |
| 3 | Java 26/27 feature status | Open → resolved by task F02 from openjdk.org JEP pages |
| 4 | Midterm question bank | Out of scope for v1 |
| 5 | Slides / videos | Out of scope for v1 |

## 12. Out of scope (v1)

Slides/videos, midterm question bank, auto-grading platform integration (e.g. GitHub Classroom — possible v1.1), Gradle build, Kotlin examples,
web UI or database in the capstone.
