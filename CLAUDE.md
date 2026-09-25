# CLAUDE.md — Design Patterns in Java (Java 27 · Sept 2026 curriculum)

Guidance for Claude Code (and human contributors) working in this repository.
Read this file first, then load only the spec/plan sections relevant to the task at hand.

## 1. What this repo is

A bilingual (English + Turkish) university-level course on **design patterns in modern Java (JDK 27)**,
built module by module with **spec-driven development** (Addy Osmani `agent-skills` plugin).
Every module ships: lesson text (Markdown + PDF, EN + TR), runnable examples, tests,
assignments with starter code, and reference solutions. The course ends with a capstone project.

**Sources of truth — in this order:**

| File | Role |
|---|---|
| `SPEC.md` | What we build and why, capability map (module ids), success criteria, boundaries |
| `specs/SPEC-<module-id>.md` | Per-module spec (learning outcomes, examples, assignments). Written before the module is implemented |
| `tasks/plan.md` | Implementation plan, phases, checkpoints, risks |
| `tasks/todo.md` | Task list; each task maps 1:1 to a GitHub issue (`#N`) |
| `docs/glossary.md` | EN ↔ TR terminology. Use it; never invent a new Turkish term silently |

If code and spec disagree, **update the spec first**, then the code. Never implement anything that is not in a spec or task.

## 2. Workflow (SDD with agent-skills)

```
/spec  →  /plan  →  /build  →  /test  →  /review  →  /ship
```

1. Pick the next unblocked task in `tasks/todo.md` (dependency order, not preference).
2. If the module has no `specs/SPEC-<id>.md` yet, its spec task comes first and needs human approval.
3. Implement in thin vertical slices (`incremental-implementation`), tests first (`test-driven-development`).
4. Verify with the task's **Verify** command before marking it done.
5. One task → one branch → one PR that says `Closes #N`. Tick the box in `tasks/todo.md` in the same PR.

## 3. Commands

Maven must run on JDK 27. The machine's default Maven JDK may be older — always export it:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 27)     # macOS; on Linux/Windows point JAVA_HOME at JDK 27

./mvnw -q verify                                      # build + all example & solution tests (CI gate)
./mvnw -q -pl modules/m03-creational-construction verify          # one module
./mvnw -q -pl modules/m03-creational-construction test -Pexercises # run the student's exercise tests
scripts/check-starters.sh [modules/<id>]              # starters compile AND still fail their contract tests

# Run any example WITHOUT building (multi-file source launcher, JEP 458):
java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/builder/PizzaDemo.java

scripts/build-pdf.sh modules/m03-creational-construction   # lesson.{en,tr}.md → lesson.{en,tr}.pdf
scripts/build-pdf.sh --all
scripts/check-docs.sh                                  # EN/TR parity + link check
```

(`mvnw`, the profiles and the scripts are created by the Foundation tasks; if missing, that task is not done yet.)

## 4. Repository layout

```
SPEC.md  CLAUDE.md  README.md  README.tr.md  pom.xml (parent/aggregator)  mvnw  .mvn/
specs/                   per-module specs (SPEC-<module-id>.md)
tasks/                   plan.md, todo.md
docs/                    glossary.md, syllabus.{en,tr}.md, java27-features.md, templates/, pdf/ (typst template)
scripts/                 build-pdf.sh, check-docs.sh, new-module.sh
modules/<module-id>/
  README.md              bilingual module index (links to everything below)
  lesson/                lesson.en.md  lesson.tr.md  lesson.en.pdf  lesson.tr.pdf  img/
  assignments/           NN-<slug>.en.md  NN-<slug>.tr.md
  pom.xml
  src/main/java/io/github/aliturgutbozkurt/patterns/<mNN>/
    examples/<pattern>/  runnable demos (each has a `main`)
    exercises/exNN/      student starter code with `// TODO` markers
    solutions/exNN/      reference solutions
  src/test/java/.../<mNN>/
    examples/…           tests for examples
    exercises/exNN/      *ExerciseTest  (@Tag("exercise"), excluded from default build)
    solutions/exNN/      *SolutionTest  (runs in CI)
    …/exNN/*Contract.java   shared abstract contract test (see §6)
capstone/                spec.{en,tr}.md, rubric.{en,tr}.md, starter/, reference/
```

Module ids are **stable** (defined in `SPEC.md` capability map). Never rename them.

## 5. Java code conventions

- **JDK 27, `maven.compiler.release=27`.** Prefer final (non-preview) features. Preview features are allowed
  only where `docs/java27-features.md` marks them as preview AND the module spec says so; such code lives in a
  module/profile with `--enable-preview` and the lesson flags it clearly.
- Write **modern idiomatic Java**, and show the classic GoF form next to it when the modern form hides the pattern:
  `record`s for values, `sealed` hierarchies + `switch` pattern matching (exhaustive, no `default` on sealed types),
  record patterns, unnamed variables `_`, lambdas/method refs for single-method strategies, `var` only when the type is obvious,
  `List.of`/`Map.of`, `Optional` only as a return type, virtual threads for blocking concurrency.
- `src/main` code has **zero external dependencies** so every demo runs with plain `java File.java`. Test-only deps are fine.
- Demos print what they are demonstrating (`System.out.println`) — output is part of the lesson; keep it deterministic
  (no timestamps/random without a seed) so the lesson can quote it.
- One public top-level type per file; small classes; names in **English** (code is English, prose is bilingual).
- Javadoc on every public type in `examples` and `solutions`: one line of intent + `@see` the lesson section.
- No `System.exit`, no swallowed exceptions, no mutable static state (except the Singleton lesson, which discusses why).
- Compiler: `-Xlint:all -Werror`. Do not add `@SuppressWarnings` without a comment explaining why.

```java
/** Strategy: pricing rule chosen at runtime. @see lesson.en.md#strategy */
public sealed interface PricingRule permits RegularPrice, PercentOff, BuyXGetY {
    Money apply(Money base);
}

public record PercentOff(int percent) implements PricingRule {
    public PercentOff {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException("percent: " + percent);
    }
    @Override public Money apply(Money base) { return base.times(100 - percent).dividedBy(100); }
}
```

## 6. Tests, exercises and solutions

- JUnit Jupiter + AssertJ. Test names describe behaviour: `appliesDiscountBeforeTax()`.
- Every example has at least one test; every exercise has a **contract test**:
  ```
  abstract class Ex01Contract { abstract Cart newCart(); @Test void … }   // the spec of the exercise
  @Tag("exercise") class Ex01ExerciseTest extends Ex01Contract { Cart newCart() { return new exercises.ex01.Cart(); } }
  class Ex01SolutionTest extends Ex01Contract { Cart newCart() { return new solutions.ex01.Cart(); } }
  ```
  So the student's code and the reference solution are held to the *same* tests.
- CI must prove: solutions pass, starters compile, starters must NOT pass their tests (`scripts/check-starters.sh`).
- Types the student must not change live in the `exercises.exNN` package marked `GIVEN — do not modify`;
  the solution implements those same types, so one contract test fits both. Details: `CONTRIBUTING.md`.
- Never delete, `@Disabled` or weaken a test to get green. Fix the code or ask.

## 7. Lessons (Markdown → PDF), bilingual rules

- Use `docs/templates/lesson.md`. Sections per pattern: **Problem → Intent → Structure (Mermaid class diagram) →
  Classic Java → Modern Java 27 → Real-world usage in the JDK/frameworks → Pitfalls & when NOT to use → Related patterns → Quiz**.
- `lesson.en.md` and `lesson.tr.md` have **identical structure** (same headings order, same code blocks, same diagrams).
  Code and identifiers stay English in both; comments inside code blocks may be translated.
- Turkish: pattern names stay in English with the Turkish term in parentheses on first use
  (e.g. "Strategy (Strateji)") — follow `docs/glossary.md`. Proper Turkish characters (ç ğ ı İ ö ş ü) always.
- Code in lessons must be **copied from real compiled files** (reference file path under each block), never hand-written snippets that don't compile.
- PDFs are generated, never edited by hand. Regenerate PDFs in the same PR that changes the Markdown.

## 8. Git & GitHub

- Branch: `mNN/<short-slug>` or `infra/<slug>`; Conventional Commits (`feat(m03): add step builder example`).
- PR body: link the task (`Closes #N`) and the spec section it implements.
- Labels: `type:*`, `module:*`; milestones = modules. Keep `tasks/todo.md` checkboxes in sync with issues.

## 9. Boundaries

**Always**
- Run `./mvnw -q verify` (JDK 27) before every commit; regenerate PDFs when lesson Markdown changes.
- Keep EN and TR in structural parity; keep code in lessons in sync with source files.
- Update the spec/plan when scope or a decision changes.

**Ask first**
- Adding any dependency or Maven plugin; changing `maven.compiler.release` or enabling preview features.
- Changing the capability map (module ids, order, scope), the capstone scope, or the license.
- Changing CI workflows, GitHub labels/milestones, or force-pushing.

**Never**
- Commit secrets, generated `target/` output, or IDE files.
- Publish reference solutions *inside* exercise starter packages, or starter code that already passes its tests.
- Delete/skip failing tests, or copy text/code from copyrighted books verbatim.
- Mark a task done without its Verify step passing.

## 10. Definition of Done (per module)

- [ ] `specs/SPEC-<id>.md` approved
- [ ] Examples compile, run with `java <File>.java`, and are tested; `./mvnw verify` green on JDK 27
- [ ] `lesson.en.md` + `lesson.tr.md` complete, in parity, PDFs regenerated
- [ ] ≥ 2 assignments (EN + TR), starters compile & fail, solutions pass the shared contract tests
- [ ] Module `README.md` links everything; issue closed; `tasks/todo.md` ticked
