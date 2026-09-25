# Syllabus — Design Patterns in Java (Java 27), Fall 2026

## Course information

| | |
|---|---|
| Level | Undergraduate (2nd–4th year) or junior developers |
| Prerequisites | Java basics: classes, interfaces, collections, exceptions, generics (reading level) |
| Format | 14 weeks · 3 h lecture + 2 h lab per week (suggested) |
| Language | English and Turkish materials with identical content |
| Tools | JDK 27, any Java IDE, Git; Maven via the included wrapper |

## Course learning outcomes

By the end of the course students can:

1. **Identify** the problem a design pattern solves and **choose** a pattern — or none — with justified trade-offs.
2. **Implement** the GoF creational, structural and behavioral patterns in idiomatic Java 27.
3. **Refactor** existing code towards SOLID design and patterns, keeping behaviour fixed with tests.
4. **Apply** functional, data-oriented and concurrency patterns using records, sealed types, pattern matching and virtual threads.
5. **Design** a small system with clean architecture, starting from a written specification, and **defend** the design.

## Weekly plan

| Week | Module | Topics | Lab / hand-in |
|---|---|---|---|
| 1 | m00 Setup & Modern Java | JDK 27, source launcher, records, sealed types, pattern matching, lambdas | m00 assignments |
| 2 | m01 OOP, SOLID & UML | SOLID, composition over inheritance, UML class and sequence diagrams | m01 assignments |
| 3 | m02 Creational I | Singleton, Static Factory Method, Factory Method, Abstract Factory, `ServiceLoader` | m02 assignments |
| 4 | m03 Creational II | Builder, Prototype, Object Pool, composition root | m03 assignments |
| 5 | m04 Structural I | Adapter, Decorator, Proxy (incl. dynamic proxies) | m04 assignments |
| 6 | m05 Structural II | Composite, Bridge, Facade, Flyweight | m05 assignments |
| 7 | — | **Midterm** (m00–m05) | — |
| 8 | m06 Behavioral I | Strategy, Template Method, Command, Iterator, Stream Gatherers | m06 assignments |
| 9 | m07 Behavioral II | Observer, Mediator, Chain of Responsibility, Memento | m07 assignments · **capstone starts** (spec) |
| 10 | m08 Behavioral III | State, Visitor vs. pattern matching, Interpreter | m08 assignments · capstone spec due |
| 11 | m09 Functional & Data-Oriented | Data-oriented programming, Result types, patterns as functions | m09 assignments |
| 12 | m10 Concurrency Patterns | Virtual threads, Producer–Consumer, Structured Concurrency (preview), Scoped Values | m10 assignments |
| 13 | m11 Architecture & Enterprise | Dependency Injection, Repository, Ports & Adapters, anti-patterns, ArchUnit | m11 assignments |
| 14 | Capstone | Presentations and design defence | capstone code + report due |

## Assessment (suggested)

| Component | Weight | Notes |
|---|---|---|
| Assignments | 30 % | Graded by the contract tests plus a short code-quality review |
| Midterm | 20 % | Weeks 1–6 |
| Capstone | 40 % | Spec 10 % · implementation & tests 20 % · report & defence 10 % — see the capstone rubric |
| Participation / quizzes | 10 % | End-of-lesson quizzes in every module |

## Policies

### AI assistants

AI coding assistants are part of professional practice in 2026 and may be used, with three rules:
1. **Disclose** what you used them for in your submission.
2. **Explain** every line you hand in — you may be asked to walk through your code.
3. In the capstone, the **spec and design decisions are yours**: the written spec, the pattern choices and their
   justification are graded, not only the code.

### Academic integrity

Work on assignments individually unless the brief says otherwise. Reading the reference solution before a serious
attempt defeats the purpose — the tests tell you when you are done.
