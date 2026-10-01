# m09 — Functional & Data-Oriented Patterns · Fonksiyonel ve Veri Odaklı Kalıplar

> Week 11 · 11. Hafta — [Spec](../../specs/SPEC-m09-functional-data-oriented.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — From Visitor to data-oriented payroll (DOP, immutability) | [EN](assignments/01-data-oriented-payroll.en.md) | [TR](assignments/01-data-oriented-payroll.tr.md) |
| Assignment 02 · Ödev 02 — Railway-style sign-up validation (`Result`) | [EN](assignments/02-signup-railway.en.md) | [TR](assignments/02-signup-railway.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/<path>`

| Topic · Konu | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Data-oriented programming | Order lifecycle: mutable class vs. sealed records · Sipariş yaşam döngüsü | `dop/order/OrderLifecycleDemo.java` |
| Data-oriented programming | Parse, don't validate: CSV import · Doğrulama değil, ayrıştırma | `dop/boundary/ParseDontValidateDemo.java` |
| Immutability | Leaky vs. defensive-copy cart, withers · Savunmacı kopya, wither'lar | `immutability/cart/DefensiveCopyDemo.java` |
| Immutability | `Money`, `DateRange`, mutable-key bug · Değer nesneleri | `immutability/values/ValueObjectsDemo.java` |
| `Optional` | Optional as a return type only · Yalnızca dönüş tipi olarak Optional | `optional/directory/OptionalDoneRightDemo.java` |
| `Result` | Sealed `Result`: map, flatMap, fold, sequence · Sealed Result | `result/core/ResultBasicsDemo.java` |
| `Result` | Checkout three ways: exceptions, Optional, Result · Üç hata modeli | `result/checkout/CheckoutStylesDemo.java` |
| `Result` | Same shape in `Optional`/`Stream`/`CompletableFuture` · JDK'da aynı biçim | `result/jdk/JdkResultShapesDemo.java` |
| Function composition | Price rules as composed functions · Fiyat kuralları | `composition/pricing/PricePipelineDemo.java` |
| Function composition | Slug pipeline and the Turkish I · Slug hattı ve Türkçe I | `composition/text/SlugifierDemo.java` |
| Currying | Curry, partial application, shipping tariff · Kısmi uygulama | `composition/currying/CurryingDemo.java` |
| Laziness & memoisation | Memoised supplier/function, `computeIfAbsent` trap · Bellekleme | `lazy/memo/MemoizationDemo.java` |
| Laziness | Lazy streams, deferred log messages, scan vs. fold · Tembel akışlar | `lazy/streams/LazyStreamsDemo.java` |
| Patterns → language features | 8-row before/after catalogue · Önce/sonra kataloğu | `features/catalogue/CatalogueDemo.java` |
| Patterns → language features | Invoice run, GoF style (21 types) · GoF üslubu | `features/shop/classic/InvoiceRunDemo.java` |
| Patterns → language features | Invoice run, functional + DOP style (6 types) · Fonksiyonel + DOP | `features/shop/modern/InvoiceRunDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m09-functional-data-oriented verify           # examples + solutions
./mvnw -pl modules/m09-functional-data-oriented test -Pexercises     # your assignments · ödevleriniz
```
