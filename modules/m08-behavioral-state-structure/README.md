# m08 — Behavioral Patterns III: State & Structure · Davranışsal Kalıplar III: Durum ve Yapı

> Week 10 · 10. Hafta — [Spec](../../specs/SPEC-m08-behavioral-state-structure.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Document-approval workflow (State) | [EN](assignments/01-document-workflow.en.md) | [TR](assignments/01-document-workflow.tr.md) |
| Assignment 02 · Ödev 02 — Mini expression language (Interpreter) | [EN](assignments/02-mini-language.en.md) | [TR](assignments/02-mini-language.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| State | Vending machine (classic GoF) · Otomat (klasik) | `state/VendingMachineDemo.java` |
| State | Order lifecycle as an `enum`, generated diagram · `enum` olarak sipariş yaşam döngüsü | `state/EnumOrderDemo.java` |
| State | Order as sealed records, pure transitions, replay · Sealed record'larla sipariş, yeniden oynatma | `state/SealedOrderDemo.java` |
| State | `Thread.State` on platform and virtual threads · Platform ve sanal iş parçacıkları | `state/ThreadStateDemo.java` |
| Visitor | Cart with double dispatch, overload trap · Çift yönlendirmeli sepet | `visitor/ClassicCartDemo.java` |
| Visitor | Cart as exhaustive `switch` · Eksiksiz `switch` ile sepet | `visitor/ModernCartDemo.java` |
| Visitor | Document renderers over a Composite · Composite üzerinde belge işleyicileri | `visitor/DocumentDemo.java` |
| Visitor | `Files.walkFileTree` disk usage · Disk kullanımı | `visitor/DiskUsageDemo.java` |
| Interpreter | Promotion rules (classic GoF) · Kampanya kuralları (klasik) | `interpreter/PromotionRulesDemo.java` |
| Interpreter | Calculator: lexer, parser, evaluator, printer, simplifier · Hesap makinesi | `interpreter/CalculatorDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m08-behavioral-state-structure verify           # examples + solutions
./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises     # your assignments · ödevleriniz
```
