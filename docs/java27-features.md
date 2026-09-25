# Java 27 features & tooling compatibility

> Language-feature audit (final vs. preview per JEP) is completed by task **F02**. This file currently records the
> **tooling spike from F01** (2026-09-25).

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

## Language features

_To be completed in F02._
