# Java 27 features & tooling compatibility

> Source of truth for which Java features the course may use. Audited 2026-09-25 against the official
> JDK project pages ([JDK 25](https://openjdk.org/projects/jdk/25/), [JDK 26](https://openjdk.org/projects/jdk/26/),
> [JDK 27](https://openjdk.org/projects/jdk/27/), GA 2026-09-15) and verified by compiling/running each feature on
> JDK 27+35 (task F02). Tooling results come from the F01 spike.

## Tooling compatibility with JDK 27 (class file version 71)

Verified on JDK 27+35 (Oracle), macOS arm64, Maven 3.9.16 via wrapper.

| Tool | Version | Result | How verified |
|---|---|---|---|
| maven-compiler-plugin | 3.16.0 | ✅ `--release 27`, `-Xlint:all -Werror` enforced | build + deliberate raw-type warning fails compilation |
| maven-surefire-plugin | 3.6.0 | ✅ | JUnit tests run |
| JUnit (BOM) | 6.1.3 | ✅ | `GreetingTest` |
| AssertJ | 3.27.7 | ✅ | `GreetingTest` |
| ArchUnit | 1.5.1 | ✅ imports class file 71 and evaluates rules | `ArchUnitJdk27SpikeTest` (logs an SLF4J "no providers" notice — harmless) |
| JaCoCo | 0.8.15 | ✅ instruments + reports | `./mvnw verify -Pcoverage` → `target/site/jacoco/index.html` |
| maven-enforcer-plugin | 3.6.3 | ✅ `requireJavaVersion [27,)` fails fast on JDK 23 with bilingual message | `JAVA_HOME=<jdk23> ./mvnw verify` |
| Multi-file source launcher (JEP 458) | JDK 27 | ✅ `java …/GreetingDemo.java` runs without a build | manual |

Decisions: ArchUnit stays in scope for m11 + capstone; JaCoCo is available via `-Pcoverage` (SPEC §7 coverage target is enforceable).

## Language & library features — final (usable everywhere)

Legend: ✅ = compiled and ran on JDK 27 with no flags.

| Feature | JEP | Final since | Verified | Used in |
|---|---|---|---|---|
| Local-variable type inference (`var`) | [286](https://openjdk.org/jeps/286) | 10 | ✅ | all |
| Switch expressions | [361](https://openjdk.org/jeps/361) | 14 | ✅ | all |
| Text blocks | [378](https://openjdk.org/jeps/378) | 15 | ✅ | m08 (Interpreter input), tests |
| Records | [395](https://openjdk.org/jeps/395) | 16 | ✅ | m00, m03, m07 (Memento), m09, all |
| Pattern matching for `instanceof` | [394](https://openjdk.org/jeps/394) | 16 | ✅ | m00, m08 |
| Sealed classes | [409](https://openjdk.org/jeps/409) | 17 | ✅ | m00, m05 (Composite), m08, m09 |
| Sequenced collections | [431](https://openjdk.org/jeps/431) | 21 | ✅ | m06 (Iterator), m07 (Memento history) |
| Record patterns | [440](https://openjdk.org/jeps/440) | 21 | ✅ | m00, m08 (Visitor vs. patterns), m09 |
| Pattern matching for `switch` | [441](https://openjdk.org/jeps/441) | 21 | ✅ | m00, m08, m09 |
| Virtual threads | [444](https://openjdk.org/jeps/444) | 21 | ✅ | m03 (Object Pool trade-off), m10, capstone |
| Unnamed variables & patterns (`_`) | [456](https://openjdk.org/jeps/456) | 22 | ✅ | m00, m08, m09 |
| Launch multi-file source-code programs | [458](https://openjdk.org/jeps/458) | 22 | ✅ | every example ("run without a build") |
| Markdown documentation comments (`///`) | [467](https://openjdk.org/jeps/467) | 23 | ✅ | m00 (shown once); course code keeps `/** */` for consistency |
| Stream Gatherers | [485](https://openjdk.org/jeps/485) | 24 | ✅ | m06 (Iterator), m09 |
| Scoped values | [506](https://openjdk.org/jeps/506) | 25 | ✅ | m10 (vs. `ThreadLocal`) |
| Module import declarations | [511](https://openjdk.org/jeps/511) | 25 | ✅ | m00 (compact source files only) |
| Compact source files & instance `main` (+ `java.lang.IO`) | [512](https://openjdk.org/jeps/512) | 25 | ✅ | m00 (first program); course examples keep classic `public static void main` inside packages |
| Flexible constructor bodies | [513](https://openjdk.org/jeps/513) | 25 | ✅ | m03 (Builder/validation), m01 (LSP) |

## Preview / incubator features in JDK 27 — policy

| Feature | JEP (JDK 27) | Status | Decision |
|---|---|---|---|
| Structured Concurrency | [533](https://openjdk.org/jeps/533) | 7th preview | **Use in m10 only**, isolated in package `…m10.examples.structured` with the module compiled/tested with `--enable-preview` and `-Xlint:-preview` (so `-Werror` still guards everything else). Lesson shows a ⚠️ preview banner and the run command `java --enable-preview --source 27 File.java`. API verified: `StructuredTaskScope.open()` / `fork` / `join`. Capstone uses it only in an optional extension. |
| Lazy Constants | [531](https://openjdk.org/jeps/531) | 3rd preview | **Lesson sidebar only** (m02 Singleton / lazy holder, m09 memoization) — "coming soon" note, no graded code. |
| Primitive types in patterns, `instanceof`, `switch` | [532](https://openjdk.org/jeps/532) | 5th preview | **Avoid in code**; mention in m08/m09 sidebar. |
| PEM encodings | [538](https://openjdk.org/jeps/538) | 3rd preview | Out of scope. |
| Vector API | [537](https://openjdk.org/jeps/537) | 12th incubator | Out of scope. |

Rule of thumb for authors: if a feature is not in the **final** table, it needs an explicit decision in this file and
in the module spec before use (see `CLAUDE.md §5`).

## Runtime notes relevant to the course

- JDK 26 [JEP 500](https://openjdk.org/jeps/500) ("Prepare to Make Final Mean Final") warns when code mutates `final`
  fields reflectively. Some build tools print these warnings on JDK 27; they are harmless for the course. m03 (Prototype /
  `clone`) mentions why reflective final-field mutation is going away.
- JDK 27 [JEP 534](https://openjdk.org/jeps/534) makes compact object headers the default — relevant to the m05
  Flyweight memory measurements (numbers differ from older JDKs; lessons quote JDK 27 numbers).
