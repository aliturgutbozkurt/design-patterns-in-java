# Assignment 02 — Mini Expression Language

> Module: m08-behavioral-state-structure · Difficulty: ★★★ · Estimated time: 3 h

## Goal

Write the operations of an **Interpreter** for a small typed language with numbers, booleans, comparisons,
`and`/`or`/`not` and `if … then … else …`. The sealed AST and a complete parser are given. You write three recursive
functions over the tree: an evaluator with run-time type checks and short-circuiting, a printer whose output parses
back to the same tree, and a query that lists the variables. This is the modern "Visitor without a visitor": one
exhaustive `switch` per operation.

## What you are given

- `exercises/ex02/Expr.java` — sealed: `Num(long)`, `Bool(boolean)`, `Var(String)`, `Unary(UnaryOp, Expr)`,
  `Binary(BinaryOp, Expr, Expr)`, `If(Expr condition, Expr then, Expr otherwise)` — **do not modify**
- `exercises/ex02/UnaryOp.java` — `NEG` (`-`), `NOT` (`not`), each with `symbol()` and `precedence()` —
  **do not modify**
- `exercises/ex02/BinaryOp.java` — `OR, AND, EQ, LT, LE, ADD, SUB, MUL, DIV` with `symbol()`, `precedence()` and
  `isComparison()` — **do not modify**
- `exercises/ex02/Value.java` — sealed: `NumValue(long)`, `BoolValue(boolean)`; `EvalException.java` —
  **do not modify**
- `exercises/ex02/Parser.java` — `Parser.parse(String)`, the complete grammar (see its Javadoc) — **do not modify**
- `exercises/ex02/Language.java` — `evaluate(Expr, Map<String, Value>)`, `print(Expr)`, `freeVariables(Expr)` —
  **do not modify**
- `exercises/ex02/MiniLanguage.java` — your code goes here (`TODO(ex02)` markers)

Precedence, loosest first: `if` < `or` < `and` < `not` < comparisons (`=`, `<`, `<=`) < `+ -` < `* /` < unary `-`.
Binary operators are left-associative. Comparisons do not chain (`a < b < c` does not parse).

## Tasks

1. **Evaluate.**
   - Arithmetic uses `Math.addExact`/`subtractExact`/`multiplyExact`, and `negateExact` for `-x`. Overflow throws
     `ArithmeticException`. `DIV` truncates toward zero, and division by zero throws
     `EvalException("division by zero")`.
   - A wrong operand type throws `EvalException("type error: <OP> expects <NUM|BOOL> but got <NUM|BOOL>")`, where
     `<OP>` is the enum name (`ADD`, `NOT`, …) and `IF` for a non-boolean condition. `EQ` takes two numbers or two
     booleans; for mixed types the left operand's type is the expected one.
   - `and`/`or` short-circuit, and `if` evaluates only the chosen branch: `false and 1 / 0 = 1` is `false`.
   - An unknown variable throws `EvalException("unknown variable: <name>")`.
2. **Print.** Use single spaces around binary operators, `-x`, `not x` and `if c then a else b`. Add parentheses
   **only** where precedence or associativity needs them. `Parser.parse(print(e))` must equal `e`.
3. **Free variables.** Return every variable name in the tree (the language has no binders).
4. `null` arguments throw `NullPointerException`.

## Acceptance criteria

- [ ] `evaluatesIntegerArithmetic`
- [ ] `respectsPrecedenceAndLeftAssociativity`
- [ ] `integerDivisionTruncatesTowardZero`
- [ ] `divisionByZeroIsAnEvalError`
- [ ] `overflowThrowsArithmeticException`
- [ ] `comparesNumbersAndBooleans`
- [ ] `mixedTypeEqualityIsATypeError`
- [ ] `arithmeticOnBooleansIsATypeError`
- [ ] `ifRequiresABooleanCondition`
- [ ] `andOrShortCircuit`
- [ ] `ifEvaluatesOnlyTheChosenBranch`
- [ ] `looksUpVariablesInTheEnvironment`
- [ ] `unknownVariableIsAnEvalError`
- [ ] `printsWithMinimalParentheses`
- [ ] `printsNestedIfAndUnaryOperators`
- [ ] `printedTextParsesBackToTheSameTree` (23 trees)
- [ ] `collectsFreeVariables`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — two small helpers carry the type checks</summary>

`long number(String op, Value v)` and `boolean bool(String op, Value v)` each switch over the sealed `Value`. They
return the payload or throw the type error. Every operator then becomes one line, and `bool(...) && bool(...)`
short-circuits for free because Java's own `&&` does.

</details>

<details><summary>Hint 2 — equality over a pair</summary>

Wrap both values in a private `record Operands(Value left, Value right)` and switch over it:
`case Operands(NumValue(var a), NumValue(var b)) -> a == b`. The two mixed cases can share one label:
`case Operands(NumValue _, BoolValue _), Operands(BoolValue _, NumValue _) -> …`. No `default` is needed.

</details>

<details><summary>Hint 3 — the printer rule</summary>

Give every node a precedence (`if` = 0, operators their own, literals and variables the highest). Wrap a child in
parentheses when its precedence is **lower** than the parent's. For the right operand of a binary operator, also wrap
it when the precedence is **equal** (left associativity). For the left operand of a comparison, also wrap it when
equal (comparisons do not chain). `if` as an operand is always wrapped. The calculator's `Printer` in
`examples/interpreter/calc` uses the same rule.

</details>

## Stretch goals (optional, not graded)

- Add a `Simplifier` (`x + 0`, `x * 1`, `not not b`, constant folding) as a fourth function, without touching `Expr`.
- Add a static type checker `typeOf(Expr, Map<String, Type>)` that rejects `1 + true` *before* evaluation. Which
  run-time checks become impossible to fail?
