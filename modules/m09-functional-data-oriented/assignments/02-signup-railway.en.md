# Assignment 02 — Railway-Style Sign-Up Validation with a Sealed `Result`

> Module: m09-functional-data-oriented · Difficulty: ★★★ · Estimated time: 3 h

## Goal

Validate a PatternShop sign-up form without a single thrown exception. Each field parser returns a
`Result<X, SignupError>`. **Stage 1** runs every parser and **collects all** field errors, so the customer sees them
all at once. **Stage 2** runs only when stage 1 succeeded: it asks the external `UserRegistry` in a **fail-fast**
chain of `flatMap`s (the "railway"), so the service is never called with invalid input.

## What you are given

- `exercises/ex02/Result.java` — `sealed interface Result<T, E> permits Ok, Err` with `map`, `mapError`, `flatMap`,
  `fold`, `orElse`, `orElseGet`, `toOptional`, `attempt`. It is a copy of `examples.result.core.Result` with the same
  API, so this assignment depends only on its own package — **do not modify**
- `exercises/ex02/RawSignup.java` — record `RawSignup(username, email, age, country, referralCode)`, all strings
  exactly as typed; any of them may be `null` — **do not modify**
- `exercises/ex02/Country.java` — `TR`, `DE`, `NL`, `US` — **do not modify**
- `exercises/ex02/Referral.java` — sealed: `NoReferral()`, `ReferredBy(String code)` — **do not modify**
- `exercises/ex02/Signup.java` — record `Signup(String username, String email, int age, Country country, Referral
  referral)` — **do not modify**
- `exercises/ex02/SignupError.java` — sealed: `Missing(field)`, `TooShort(field, minLength)`, `InvalidFormat(field)`,
  `OutOfRange(field, min, max)`, `UnknownCountry(value)`, `UsernameTaken(username)`, `InvalidReferral(code)` —
  **do not modify**
- `exercises/ex02/UserRegistry.java` — `boolean isTaken(String username)`, `boolean isValidReferral(String code)` —
  **do not modify**
- `exercises/ex02/SignupPipeline.java` — `Result<Signup, List<SignupError>> validate(RawSignup raw)` —
  **do not modify**
- `exercises/ex02/DefaultSignupPipeline.java` — your code goes here (`TODO(ex02)` markers); add private field parsers
  of your choice

## Tasks

1. **Stage 1 — collect all**, in field order username → email → age → country → referral. Every field is trimmed
   first. A `null` or blank username, email, age or country → `Missing(field)`.
   - username: lower-cased with `Locale.ROOT`; shorter than 3 → `TooShort("username", 3)`; longer than 20 or any
     character outside `[a-z0-9_]` → `InvalidFormat("username")`.
   - email: must look like `local@domain.tld` (no spaces, exactly one `@`, a dot after it) → otherwise
     `InvalidFormat("email")`; lower-cased with `Locale.ROOT`.
   - age: a base-10 integer (`InvalidFormat("age")` otherwise) in 13..120 inclusive
     (`OutOfRange("age", 13, 120)` otherwise).
   - country: a `Country` name, ignoring case → otherwise `UnknownCountry(<trimmed value>)`.
   - referral: `null`/blank → `NoReferral`, otherwise `ReferredBy(<code upper-cased with Locale.ROOT>)`.

   If any field fails, return `Err` with **all** stage-1 errors in field order — and do **not** call the registry.
2. **Stage 2 — fail fast**, only after stage 1 succeeded: `isTaken(username)` → `Err([UsernameTaken(username)])`,
   and then the referral is not checked; otherwise, only for `ReferredBy`, `isValidReferral(code)` false →
   `Err([InvalidReferral(code)])`. Success → `Ok(Signup)`.
3. Every error list is unmodifiable. The pipeline never throws for bad input; a `null` `RawSignup` →
   `NullPointerException`.

## Acceptance criteria

- [ ] `validSignupProducesNormalisedValue`
- [ ] `missingFieldsAreReported`
- [ ] `allFieldErrorsAreCollectedInFieldOrder`
- [ ] `usernameTooShortAndInvalidCharacters`
- [ ] `emailIsTrimmedAndLowerCased`
- [ ] `ageMustBeANumberInRange`
- [ ] `ageBoundariesAreInclusive`
- [ ] `countryIsCaseInsensitive`
- [ ] `unknownCountryIsReported`
- [ ] `blankReferralMeansNoReferral`
- [ ] `registryNotCalledWhenFieldsInvalid`
- [ ] `takenUsernameFailsFast`
- [ ] `invalidReferralIsReported`
- [ ] `errorListIsUnmodifiable`
- [ ] `neverThrowsForBadInput`
- [ ] `rejectsNullInput`

## Run the tests

```bash
./mvnw -pl modules/m09-functional-data-oriented test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — one parser per field</summary>

Write `private static Result<String, SignupError> username(String text)`, `email(...)`, `Result<Integer, …> age(...)`
and so on. `Result.attempt(() -> Integer.parseInt(text, 10), _ -> new InvalidFormat("age"))` turns the
`NumberFormatException` into an `Err`; a following `flatMap` checks the range.

</details>

<details><summary>Hint 2 — collecting the errors</summary>

`fold` turns each field result into a stream of zero or one error:
`r.fold(_ -> Stream.<SignupError>empty(), Stream::of)`. `Stream.of(u, e, a, c, r).flatMap(...).toList()` then gives
all errors in field order, already unmodifiable.

</details>

<details><summary>Hint 3 — combining five Oks, then the railway</summary>

When the error list is empty every result is an `Ok`, so nested `flatMap`s
(`u.flatMap(x -> e.flatMap(y -> …map(… -> new Signup(…))))`) build the `Signup`. `mapError(List::of)` lifts
`SignupError` to `List<SignupError>`, and two more `flatMap`s ask the registry: the second one never runs if the first
returned `Err`.

</details>

## Stretch goals (optional, not graded)

- Generalise stage 1 into a tiny `Validated` helper that combines two results and concatenates their errors. Why does
  `Result.flatMap` alone not do this?
- Add a `password` field with three independent rules and report every broken rule, not only the first.
