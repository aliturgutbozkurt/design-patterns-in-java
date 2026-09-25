# m00 — Setup & Modern Java · Kurulum ve Modern Java

> Week 1 · 1. Hafta — [Spec](../../specs/SPEC-m00-setup-and-modern-java.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Temperature | [EN](assignments/01-temperature.en.md) | [TR](assignments/01-temperature.tr.md) |
| Assignment 02 · Ödev 02 — Payment fees | [EN](assignments/02-payment-fees.en.md) | [TR](assignments/02-payment-fees.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m00-setup-and-modern-java/<path>`

| Topic · Konu | Run · Çalıştır (`<path>`) |
|---|---|
| Compact source file · Kompakt kaynak dosya | `first-steps/Hello.java` |
| Multi-file program · Çok dosyalı program | `first-steps/multifile/Main.java` |
| Record value object · Record değer nesnesi | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/records/MoneyDemo.java` |
| Flexible constructor body · Esnek kurucu gövdesi | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/records/PercentageDemo.java` |
| Sealed types + pattern matching · Sealed tipler + desen eşleme | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/sealedtypes/ShapeDemo.java` |
| Recursive sealed tree (JSON) · Özyinelemeli sealed ağaç | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/sealedtypes/JsonDemo.java` |
| Streams, collectors, gatherers | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/functional/OrderStatsDemo.java` |
| Functions as data · Veri olarak fonksiyonlar | `src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/functional/TextPipelineDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m00-setup-and-modern-java verify           # examples + solutions
./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises     # your assignments · ödevleriniz
```
