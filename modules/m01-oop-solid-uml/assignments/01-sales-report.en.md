# Assignment 01 — Sales Report: SRP + OCP Refactoring

> Module: m01-oop-solid-uml · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

`LegacyReport` is a god class: in one method it adds up sales per region, sorts the regions and formats the result,
and it picks the format with a `String` flag. Split it into classes that each have **one reason to change** (SRP) and
make it possible to add a new report format **without editing** the service (OCP). The legacy output is your safety
net: your version must produce exactly the same text.

## What you are given

- `exercises/ex01/Sale.java`, `SalesSummary.java` — records — **do not modify**
- `exercises/ex01/SalesSummarizer.java`, `ReportFormat.java`, `ReportGenerator.java` — interfaces — **do not modify**
- `exercises/ex01/LegacyReport.java` — the god class; its output is the reference — **do not modify**
- `exercises/ex01/RegionSummarizer.java`, `TextReportFormat.java`, `CsvReportFormat.java`, `ReportService.java` —
  your code goes here (`TODO(ex01)` markers)

## Tasks

1. Read `LegacyReport` and list its responsibilities (summing, layout, format selection).
2. `RegionSummarizer`: total the amounts per region and overall. Regions are sorted alphabetically; all amounts have
   scale 2; no sales give an empty map and a grand total of `0.00`.
3. `TextReportFormat` and `CsvReportFormat`: only the layout — each must match `LegacyReport`'s `"text"` and `"csv"`
   output character for character.
4. `ReportService(SalesSummarizer, ReportFormat)`: summarize, then render. No `if`/`switch` on the format inside the
   service — the format is whatever object you are given.
5. Reject `null` constructor arguments and a `null` sales list with `NullPointerException`.

## Acceptance criteria

- [ ] `summarizesTotalsByRegionInAlphabeticalOrder`
- [ ] `grandTotalIsSumOfRegions` — sample data → 5630.75
- [ ] `emptySalesGiveZeroTotal`
- [ ] `textFormatMatchesLegacyOutput`
- [ ] `csvFormatMatchesLegacyOutput`
- [ ] `newFormatPlugsInWithoutChangingService` — the test passes a lambda `ReportFormat`
- [ ] `amountsUseScaleTwo`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m01-oop-solid-uml test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — summing per key</summary>

`Map.merge(region, amount, BigDecimal::add)` adds to an existing total or starts a new one. A `TreeMap` keeps its keys
sorted.

</details>

<details><summary>Hint 2 — the text layout</summary>

Copy the format string from `LegacyReport`: `"%-12s%10s"` pads the label to 12 characters (left-aligned) and the amount
to 10 (right-aligned).

</details>

## Stretch goals (optional, not graded)

- Add a `MarkdownReportFormat` that renders a Markdown table. Which classes did you have to touch?
- Draw the before and after designs as Mermaid class diagrams.
