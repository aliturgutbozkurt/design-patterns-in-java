// Course-wide PDF styling (included by scripts/build-pdf.sh via pandoc header-includes).
#set page(
  numbering: "1 / 1",
  header: context {
    if counter(page).get().first() > 1 [
      #set text(size: 8pt, fill: luma(110))
      Design Patterns in Java · Java 27 #h(1fr) github.com/aliturgutbozkurt/design-patterns-in-java
    ]
  },
  footer: context [
    #set text(size: 8pt, fill: luma(110))
    Code: MIT · Text: CC BY 4.0 #h(1fr) #counter(page).display("1 / 1", both: true)
  ],
)
#show raw.where(block: true): it => block(
  fill: luma(245), inset: 8pt, radius: 3pt, width: 100%, stroke: 0.5pt + luma(220),
  text(size: 8.5pt, it),
)
#show raw.where(block: false): it => box(fill: luma(240), inset: (x: 2pt), outset: (y: 2pt), radius: 2pt, it)
#show link: set text(fill: rgb("#1f5fa8"))
// Long tables (capstone guide, rubric) may span pages instead of running over the footer.
#show figure.where(kind: table): set block(breakable: true)
// Inside table cells, inline code may wrap after "." or "(" and at camelCase humps, so long identifiers
// (e.g. test names in the rubric's 8-column template) stay inside narrow columns. Font as `codefont` in defaults.yaml.
// Very wide tables (8+ columns, e.g. the rubric's justification template) also use a smaller font so every column fits.
#show table: it => {
  show raw.where(block: false): r => {
    show regex("[.(]|[a-z][A-Z]"): m => m.text.slice(0, 1) + sym.zws + m.text.slice(1)
    highlight(fill: luma(240), extent: 1pt, text(font: "DejaVu Sans Mono", size: 1em, r.text))
  }
  let ncols = if type(it.columns) == int { it.columns } else { it.columns.len() }
  if ncols >= 8 { text(size: 8.5pt, it) } else { it }
}
