<!-- Report template for the capstone brief §9 and rubric C9 (capstone/rubric.en.md). Write it in English OR Turkish,
     6–10 pages, terms as in docs/glossary.md; every claim about the code points to a file path. Replace every <…>. -->
# PatternShop — Report — <your name(s)>

> Language: <English | Türkçe> · Commit: <tag capstone-final> · Summary in the other language: section 7

## 1. Design overview
What the system does, the main decisions in a few paragraphs, and the hexagon as a Mermaid diagram (packages
`domain`, `application`, `adapter.in.*`, `adapter.out.*`, `config`, the ports between them).

## 2. Pattern-justification table
The complete table from the rubric §5 (may be the updated table of your `SPEC.md` §5), plus at least one pattern
you deliberately did not use and why.

| # | Pattern (category) | Force / problem in PatternShop | Participants (types and `@PatternRole` roles) | Alternative considered and why not | Modern Java form | Test(s) that show it | Course module |
|---|---|---|---|---|---|---|---|
| 1 | | | | | | | |

**Patterns considered and rejected**

| Pattern | Where it was tempting | Why it was not used |
|---|---|---|
| | | |

## 3. Modern Java decisions
At least two decisions explained against the classic alternative (e.g. a sealed hierarchy with an exhaustive `switch`
instead of a Visitor; records instead of mutable value classes).

## 4. Concurrency design
What runs in parallel during fulfilment, how the parallelism is bounded, how results and failures are collected, and
why the shared state is thread-safe.

## 5. Reflection
At least two things you would change with what you know now, and what they would cost.

## 6. AI-usage statement
Which assistants you used, for what, and in which parts of the code and text.

## 7. One-page summary in the other language
<Türkçe özet | English summary>
