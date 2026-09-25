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
