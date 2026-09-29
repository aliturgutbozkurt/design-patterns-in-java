# Assignment 01 — Travel Booking Builder

> Module: m03-creational-construction · Difficulty: ★★☆ · Estimated time: 1.5 h

## Goal

A flight booking has four required parts and several optional ones. Write a **Builder** that fills in sensible
defaults, checks every rule in `build()` and — unlike a builder that stops at the first problem — reports **all**
violated rules in one exception, so a user can fix a form in one go. The resulting `Booking` must be immutable and
independent of the builder.

## What you are given

- `exercises/ex01/Booking.java` — record `Booking(traveller, from, to, departure, returnDate, passengers, cabin,
  extras)`; `returnDate` is `null` for a one-way trip, read it through `returnTrip()` — **do not modify**
- `exercises/ex01/CabinClass.java` — `ECONOMY`, `BUSINESS`, `FIRST` — **do not modify**
- `exercises/ex01/BookingBuilder.java` — the builder interface — **do not modify**
- `exercises/ex01/DefaultBookingBuilder.java` — your code goes here (`TODO(ex01)` markers)

## Tasks

1. Setters store values and return `this`; a `null` argument throws `NullPointerException` immediately.
2. Defaults: one-way (no return date), 1 passenger, `ECONOMY`, no extras.
3. `build()` checks every rule and collects the problems:
   - traveller, from, to and departure are required;
   - `from` and `to` must differ (ignoring case);
   - the return date, if any, must be **after** the departure date;
   - passengers must be 1–9.
4. If there is at least one problem, throw one `IllegalStateException` whose message lists them all.
5. The booking's `extras` must be an **immutable copy**: adding extras to the builder later must not change a booking
   that was already built. The same builder may build several bookings.

## Acceptance criteria

- [ ] `buildsWithDefaults`
- [ ] `buildsAReturnTrip`
- [ ] `missingRequiredFieldsAreAllReported` — one message names traveller, from, to, departure *and* passengers
- [ ] `rejectsSameOriginAndDestination`
- [ ] `rejectsReturnBeforeDeparture` — returning the same day also counts as "not after"
- [ ] `rejectsPassengersOutsideOneToNine`
- [ ] `extrasAreImmutableAndIndependentOfTheBuilder`
- [ ] `builderCanBuildSeveralIndependentBookings`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m03-creational-construction test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — collecting problems</summary>

Keep a `List<String> problems`, add one sentence per violated rule, and at the end throw
`new IllegalStateException("invalid booking: " + String.join("; ", problems))` if the list is not empty.

</details>

<details><summary>Hint 2 — the defensive copy</summary>

`Set.copyOf(extras)` creates an immutable snapshot of the builder's set.

</details>

## Stretch goals (optional, not graded)

- Turn the builder into a **step builder** so that `build()` is only available after the four required parts.
  What do you gain, and what becomes harder (hint: reporting *all* missing parts)?
- Why is `returnDate` a nullable component with an `Optional`-returning method instead of an `Optional` component?
