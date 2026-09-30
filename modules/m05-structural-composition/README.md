# m05 — Structural Patterns II: Composition · Yapısal Kalıplar II: Bileşim

> Week 6 · 6. Hafta — [Spec](../../specs/SPEC-m05-structural-composition.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Restaurant menu Composite | [EN](assignments/01-menu-composite.en.md) | [TR](assignments/01-menu-composite.tr.md) |
| Assignment 02 · Ödev 02 — Flyweight map tiles | [EN](assignments/02-flyweight-tiles.en.md) | [TR](assignments/02-flyweight-tiles.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Composite | Org chart (classic, safe variant) · Organizasyon şeması (klasik) | `composite/OrgChartDemo.java` |
| Composite | File system (sealed records + `switch`) · Dosya sistemi | `composite/FileSystemDemo.java` |
| Composite | Arithmetic expressions · Aritmetik ifadeler | `composite/ExpressionDemo.java` |
| Bridge | Shapes × renderers · Şekiller × çiziciler | `bridge/ShapesDemo.java` |
| Bridge | Remote controls × devices · Kumandalar × cihazlar | `bridge/RemoteDemo.java` |
| Bridge | Alert policies × channels (lambda) · Uyarı politikaları × kanallar | `bridge/AlertsDemo.java` |
| Facade | Home theater · Ev sineması | `facade/HomeTheaterDemo.java` |
| Facade | Checkout with compensation · Telafili ödeme akışı | `facade/CheckoutDemo.java` |
| Flyweight | Text editor glyphs · Metin düzenleyici glifleri | `flyweight/TextEditorDemo.java` |
| Flyweight | Forest of 100 000 trees · 100 000 ağaçlı orman | `flyweight/ForestDemo.java` |
| Flyweight | JDK caches, value-based classes · JDK önbellekleri, değer tabanlı sınıflar | `flyweight/JdkFlyweightsDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m05-structural-composition verify           # examples + solutions
./mvnw -pl modules/m05-structural-composition test -Pexercises     # your assignments · ödevleriniz
```
