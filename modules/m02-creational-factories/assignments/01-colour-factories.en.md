# Assignment 01 — Colour Values with Static Factories

> Module: m02-creational-factories · Difficulty: ★★☆ · Estimated time: 1.5 h

## Goal

Write an immutable colour class that callers can only create through **static factory methods**: `rgb(…)`, `hex(…)`
and `named(…)`. Along the way you practise the three things a factory can do and a constructor cannot: have a
meaningful name, return a **cached** instance, and parse or validate before deciding what to return.

## What you are given

- `exercises/ex01/Color.java` — the interface to implement — **do not modify**
- `exercises/ex01/RgbColor.java` — your code goes here (`TODO(ex01)` markers)

## Tasks

1. Keep the constructor of `RgbColor` **private**; store the three components.
2. `rgb(red, green, blue)`: each component 0–255, otherwise `IllegalArgumentException`.
3. `hex(text)`: accept `#RRGGBB` and the short form `#RGB` (`#F80` = `#FF8800`), upper or lower case. Anything else
   throws `IllegalArgumentException`.
4. `named(name)`: the 8 basic colours `black`, `white`, `red`, `green`, `blue`, `yellow`, `cyan`, `magenta`,
   case-insensitive. Create each of them **once** and always return that instance. An unknown name throws
   `IllegalArgumentException` whose message lists the known names.
5. `rgb(…)` (and therefore `hex(…)`) returns the cached instance when the value is one of the named colours.
6. `toHex()` returns upper-case `#RRGGBB`; `equals`/`hashCode` compare the components.

## Acceptance criteria

- [ ] `rgbRejectsComponentsOutsideZeroTo255`
- [ ] `hexParsesLongForm` — `#FF8800` → 255, 136, 0
- [ ] `hexParsesShortForm` — `#F80` → 255, 136, 0
- [ ] `hexIsCaseInsensitive`
- [ ] `hexRejectsMalformedInput` — `FF8800`, `#GG0000`, `#12345`, empty
- [ ] `namedColoursAreCached` — `named("red")` is the *same object* as `named("RED")`
- [ ] `rgbReturnsCachedInstanceForNamedColour` — `rgb(255, 0, 0)` is the same object as `named("red")`
- [ ] `unknownNameListsKnownNames`
- [ ] `toHexIsUppercaseSixDigits` — `rgb(10, 11, 12)` → `#0A0B0C`
- [ ] `equalByValue`
- [ ] `hasNoPublicConstructor`

## Run the tests

```bash
./mvnw -pl modules/m02-creational-factories test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — building the cache</summary>

Create the named colours in a `static` initializer and keep them in a map from name to colour. A second map from the
packed value `(red << 16) | (green << 8) | blue` to colour lets `rgb(…)` find a cached instance quickly.

</details>

<details><summary>Hint 2 — parsing hex</summary>

A regular expression such as `#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})` validates the input; `Integer.parseInt(digits, 16)`
converts it. `"%02X".formatted(value)` prints two upper-case hex digits.

</details>

## Stretch goals (optional, not graded)

- Why can a `record` not satisfy `hasNoPublicConstructor`? What would you lose and gain by using one anyway?
- Add `RgbColor.parse(String)` that accepts either a name or a hex string. Is one "smart" factory better than two
  explicit ones?
