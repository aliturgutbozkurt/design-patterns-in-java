# Assignment 01 — Temperature Value Object

> Module: m00-setup-and-modern-java · Difficulty: ★☆☆ · Estimated time: 1 h

## Goal

Write a small **value object** as a Java `record`: a temperature that validates itself when it is created, converts
between units, and is equal to another temperature exactly when value and unit are equal. You practise records,
compact constructors and switch expressions.

## What you are given

- `exercises/ex01/Unit.java` — `CELSIUS`, `FAHRENHEIT`, `KELVIN` with their symbols — **do not modify**
- `exercises/ex01/Temperature.java` — the interface your record implements — **do not modify**
- `exercises/ex01/TemperatureReading.java` — your code goes here (`TODO(ex01)` markers)

(Paths are relative to `modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/`.)

## Tasks

1. In the compact constructor of `TemperatureReading`, reject invalid state:
   a `null` unit (`NullPointerException`), `NaN`, and anything colder than absolute zero (`IllegalArgumentException`).
   Absolute zero is 0 K = −273.15 °C = −459.67 °F.
2. Implement `to(Unit target)`. Convert through Kelvin:
   - K = °C + 273.15
   - K = (°F + 459.67) × 5 ⁄ 9
3. Make sure converting to the unit a temperature already has returns an **equal** value.

## Acceptance criteria

- [ ] `isARecord` — `TemperatureReading` is a record
- [ ] `convertsCelsiusToFahrenheit` — 100 °C = 212 °F and −40 °C = −40 °F
- [ ] `convertsFahrenheitToKelvin` — 32 °F = 273.15 K
- [ ] `convertingToSameUnitReturnsEqualValue`
- [ ] `rejectsTemperaturesBelowAbsoluteZero` — but −273.15 °C itself is allowed
- [ ] `rejectsNaN`
- [ ] `rejectsNullUnit`
- [ ] `equalWhenValueAndUnitEqual` — and 20 °C is **not** equal to 68 °F

## Run the tests

```bash
./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — where does validation go?</summary>

A compact constructor has no parameter list: `public TemperatureReading { … }`. The components `value` and `unit`
are in scope, and the fields are assigned automatically after your code runs.

</details>

<details><summary>Hint 2 — same unit</summary>

`21.5 + 273.15 - 273.15` is not exactly `21.5` in floating point. If `target == unit`, what can you return instead?

</details>

## Stretch goals (optional, not graded)

- Add `static TemperatureReading parse(String text)` accepting `"21.5 °C"`.
- Why is 20 °C not `equals` to 68 °F even though they are the same temperature? Write down when that is the right
  behaviour for a value object, and when it is not.
