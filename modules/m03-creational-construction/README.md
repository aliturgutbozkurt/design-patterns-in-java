# m03 — Creational Patterns II: Construction · Yaratımsal Kalıplar II: İnşa

> Week 4 · 4. Hafta — [Spec](../../specs/SPEC-m03-creational-construction.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Travel booking builder | [EN](assignments/01-booking-builder.en.md) | [TR](assignments/01-booking-builder.tr.md) |
| Assignment 02 · Ödev 02 — Shape editor with prototypes | [EN](assignments/02-shape-prototypes.en.md) | [TR](assignments/02-shape-prototypes.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Builder | Pizza (classic, JEP 513) · Pizza (klasik) | `builder/PizzaDemo.java` |
| Builder | HTTP request · HTTP isteği | `builder/HttpRequestDemo.java` |
| Builder | Record + builder + `with` copies · Record + builder | `builder/ServerConfigDemo.java` |
| Step builder | SQL-like query · SQL benzeri sorgu | `builder/QueryDemo.java` |
| Prototype | `clone()` vs copy constructor · `clone()` ve kopya kurucu | `prototype/DocumentPrototypeDemo.java` |
| Prototype | Unit registry · Birim kaydı | `prototype/UnitRegistryDemo.java` |
| Object Pool | Connection pool · Bağlantı havuzu | `pool/ConnectionPoolDemo.java` |
| Virtual threads · Sanal iş parçacıkları | Throttle, no pool · Havuzsuz sınırlama | `pool/ThrottleDemo.java` |
| DI as creation · Yaratım olarak DI | Composition root · Bileşim kökü | `di/CompositionRootDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m03-creational-construction verify           # examples + solutions
./mvnw -pl modules/m03-creational-construction test -Pexercises     # your assignments · ödevleriniz
```
