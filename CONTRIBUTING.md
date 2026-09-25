# Contributing

Thanks for helping! This course is built with **spec-driven development** — please read [`CLAUDE.md`](CLAUDE.md)
(conventions, commands, boundaries) and [`SPEC.md`](SPEC.md) first. Every change should trace back to a task in
[`tasks/todo.md`](tasks/todo.md) / a GitHub issue.

## Setup

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 27)   # macOS; elsewhere point JAVA_HOME at JDK 27
./mvnw -q verify                                    # must be green before you open a PR
scripts/check-starters.sh                           # starters compile and still fail their contracts
```

## Workflow

1. Pick an unblocked issue; branch `mNN/<slug>` or `infra/<slug>`.
2. Tests first, then code (small slices). Conventional Commits, e.g. `feat(m03): add step builder example`.
3. PR body: `Closes #N` + the spec section it implements. Tick the task in `tasks/todo.md` in the same PR.

## Contract tests: how exercises and solutions share one set of tests

Each assignment `exNN` in a module is made of:

| File | Location | Purpose |
|---|---|---|
| Given types (interfaces, records) | `src/main/.../<mNN>/exercises/exNN/` | The API the student codes against. Header comment: `GIVEN — do not modify`. |
| Starter | `src/main/.../<mNN>/exercises/exNN/` | Compiles, contains `// TODO(exNN): …`, throws `UnsupportedOperationException` (or returns a wrong stub). |
| Reference solution | `src/main/.../<mNN>/solutions/exNN/` | Implements the *same* given types. Never referenced from the exercises package. |
| `ExNNContract` | `src/test/.../<mNN>/exercises/exNN/` | **Abstract** class holding all tests; obtains the implementation through abstract factory methods. |
| `ExNNExerciseTest` | `src/test/.../<mNN>/exercises/exNN/` | `@Tag("exercise")`, binds the contract to the starter. Excluded from the default build. |
| `ExNNSolutionTest` | `src/test/.../<mNN>/solutions/exNN/` | Binds the contract to the solution. Runs in the default build / CI. |

```java
public abstract class Ex01Contract {                       // the assignment's spec, as tests
    protected abstract WordCounter newCounter();
    @Test void countsEachWordCaseInsensitively() { … }
}

@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {             // student:  ./mvnw -pl modules/<id> test -Pexercises
    @Override protected WordCounter newCounter() { return new exercises.ex01.SimpleWordCounter(); }
}

class Ex01SolutionTest extends Ex01Contract {             // CI:       ./mvnw verify
    @Override protected WordCounter newCounter() { return new solutions.ex01.SimpleWordCounter(); }
}
```

(This is itself the **Template Method** pattern: the contract fixes the test algorithm, subclasses supply the object.)

Rules:
- A starter must compile and must **fail** its contract; `scripts/check-starters.sh` enforces this in CI.
- Never weaken, skip or delete a contract test to make something pass.
- The contract tests what the assignment brief promises — nothing more, nothing less. Keep the brief (EN + TR) and the contract in sync.

The `modules/_sample` module contains a complete working example of this layout.

## Lessons & translations

Follow `CLAUDE.md §7`: EN and TR lessons have identical structure, use `docs/glossary.md`, and code blocks are copied from
compiled files. Regenerate PDFs in the same PR (`scripts/build-pdf.sh`).
