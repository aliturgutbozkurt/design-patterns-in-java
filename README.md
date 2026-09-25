# Design Patterns in Java (Java 27)

[![CI](https://github.com/aliturgutbozkurt/design-patterns-in-java/actions/workflows/ci.yml/badge.svg)](https://github.com/aliturgutbozkurt/design-patterns-in-java/actions/workflows/ci.yml)
[![Code: MIT](https://img.shields.io/badge/code-MIT-blue.svg)](LICENSE)
[![Text: CC BY 4.0](https://img.shields.io/badge/text-CC%20BY%204.0-lightgrey.svg)](LICENSE-CONTENT.txt)

🇹🇷 [Türkçe README](README.tr.md)

An open, bilingual (English + Turkish) course on **design patterns in modern Java (JDK 27)** for the Fall 2026
semester: 12 modules and a capstone project. Every module has lesson notes (Markdown + PDF), many runnable examples,
assignments with tests, and reference solutions.

> 🚧 The course is being built module by module with spec-driven development — see
> [progress](https://github.com/aliturgutbozkurt/design-patterns-in-java/milestones).

## What you will learn

- The classic GoF patterns — **when** to use each one, and when not to
- How modern Java (records, sealed types, pattern matching, lambdas, virtual threads) simplifies or replaces them
- SOLID design, functional and data-oriented design, concurrency patterns and clean architecture
- How to turn requirements into a spec, and a spec into tested code (the capstone)

## Course map

| Week | Module | Topics |
|---|---|---|
| 1 | m00 Setup & Modern Java | JDK 27, running code without a build, records, sealed types, pattern matching |
| 2 | m01 OOP, SOLID & UML | SOLID, composition over inheritance, UML with Mermaid |
| 3 | m02 Creational I | Singleton, Static Factory, Factory Method, Abstract Factory |
| 4 | m03 Creational II | Builder, Prototype, Object Pool |
| 5 | m04 Structural I | Adapter, Decorator, Proxy |
| 6 | m05 Structural II | Composite, Bridge, Facade, Flyweight |
| 7 | — | Midterm |
| 8 | m06 Behavioral I | Strategy, Template Method, Command, Iterator |
| 9 | m07 Behavioral II | Observer, Mediator, Chain of Responsibility, Memento |
| 10 | m08 Behavioral III | State, Visitor, Interpreter |
| 11 | m09 Functional & Data-Oriented | Records + sealed types, Result types, patterns as functions |
| 12 | m10 Concurrency Patterns | Virtual threads, Producer–Consumer, Structured Concurrency, Scoped Values |
| 13 | m11 Architecture | Dependency Injection, Repository, Hexagonal architecture, anti-patterns |
| 9–14 | Capstone | PatternShop — an order-processing system using ≥ 10 patterns |

Full plan: [syllabus](docs/syllabus.en.md).

## Getting started

### 1. Install JDK 27

| OS | Command |
|---|---|
| macOS | `brew install --cask temurin` (installs the latest Temurin, 27) |
| Windows | Download the JDK 27 `.msi` installer from [adoptium.net](https://adoptium.net/temurin/releases/?version=27) and tick "Set JAVA_HOME" |
| Linux / any | [SDKMAN!](https://sdkman.io): `sdk list java`, then `sdk install java <the 27.x-tem identifier>` — or use the archive from [adoptium.net](https://adoptium.net/temurin/releases/?version=27) |

Check: `java -version` prints `27`. If several JDKs are installed, point `JAVA_HOME` at 27
(macOS: `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`).

### 2. Clone

```bash
git clone https://github.com/aliturgutbozkurt/design-patterns-in-java.git
cd design-patterns-in-java
```

You do **not** need to install Maven — the repository ships the Maven Wrapper (`./mvnw`, or `mvnw.cmd` on Windows).

### 3. Run an example — no build needed

Every example has a `main` method and runs straight from source:

```bash
java modules/<module>/src/main/java/io/github/aliturgutbozkurt/patterns/<mNN>/examples/<pattern>/<Demo>.java
```

Or open the project in IntelliJ IDEA / VS Code / Eclipse and run the `main` method.

### 4. Build and test

```bash
./mvnw verify                            # everything
./mvnw -pl modules/<module> verify       # one module
```

### 5. Do an assignment

1. Read the brief in `modules/<module>/assignments/` (English `.en.md`, Turkish `.tr.md`).
2. Write your code where the `TODO(exNN)` markers are, in `src/main/java/.../exercises/exNN/`.
3. Run your tests until they are green:
   ```bash
   ./mvnw -pl modules/<module> test -Pexercises
   ```
4. Only then compare with the reference solution in `.../solutions/exNN/`.

## For instructors

- Lessons as PDF (English and Turkish) are in each module's `lesson/` folder and attached to every
  [release](https://github.com/aliturgutbozkurt/design-patterns-in-java/releases).
- Suggested weekly plan and assessment weights: [syllabus](docs/syllabus.en.md).
- Assignments come with contract tests; the same tests grade student code and the reference solution.

## Repository guide

| Path | What |
|---|---|
| `modules/<module>/` | lesson (MD + PDF), examples, assignments, solutions, tests |
| `capstone/` | capstone brief, rubric, starter code, reference solution |
| `docs/` | syllabus, glossary (EN ↔ TR), Java 27 feature notes, templates |
| `SPEC.md`, `tasks/` | the course specification, plan and task list (spec-driven development) |

## Contributing

Corrections and suggestions are welcome — please read [CONTRIBUTING.md](CONTRIBUTING.md) and open an
[issue](https://github.com/aliturgutbozkurt/design-patterns-in-java/issues/new/choose).

## License

Code: [MIT](LICENSE) · Lesson texts, assignment briefs, diagrams and PDFs: [CC BY 4.0](LICENSE-CONTENT.txt).
