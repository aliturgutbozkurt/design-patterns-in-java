# m06 — Behavioral Patterns I: Algorithms · Davranışsal Kalıplar I: Algoritmalar

> Week 8 · 8. Hafta — [Spec](../../specs/SPEC-m06-behavioral-algorithms.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Text editor with undo/redo | [EN](assignments/01-text-editor-undo.en.md) | [TR](assignments/01-text-editor-undo.tr.md) |
| Assignment 02 · Ödev 02 — Tree iterator and custom Gatherer | [EN](assignments/02-tree-iterator-gatherer.en.md) | [TR](assignments/02-tree-iterator-gatherer.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Strategy | Shipping, classic classes · Kargo, klasik sınıflar | `strategy/shipping/classic/ShippingDemo.java` |
| Strategy | Shipping, lambdas and enum · Kargo, lambda'lar ve enum | `strategy/shipping/modern/ShippingDemo.java` |
| Strategy | Compression codecs · Sıkıştırma kodekleri | `strategy/compression/CompressionDemo.java` |
| Strategy | `Comparator` roster · `Comparator` ile öğrenci listesi | `strategy/sorting/ComparatorDemo.java` |
| Template Method | Product importer, classic · Ürün içe aktarıcı, klasik | `templatemethod/importer/classic/ImporterDemo.java` |
| Template Method | Importer as a higher-order function · Yüksek mertebeden fonksiyon | `templatemethod/importer/functional/ImporterDemo.java` |
| Template Method | `AbstractList`, `InputStream` | `templatemethod/jdk/JdkTemplateMethodDemo.java` |
| Command | Spreadsheet undo/redo, classic · Hesap tablosu, klasik | `command/spreadsheet/classic/SpreadsheetDemo.java` |
| Command | Edits as sealed records · Mühürlü record'lar olarak düzenlemeler | `command/spreadsheet/modern/SpreadsheetDemo.java` |
| Command | Smart-home remote, macro · Akıllı ev kumandası, makro | `command/remote/RemoteControlDemo.java` |
| Command | Job queue on virtual threads · Sanal iş parçacıklarında iş kuyruğu | `command/jobs/JobQueueDemo.java` |
| Iterator | `IntRange`, sequenced playlist · Sıralı çalma listesi | `iterator/basics/IteratorBasicsDemo.java` |
| Iterator | Org chart traversals · Organizasyon şeması dolaşımları | `iterator/tree/TreeIteratorDemo.java` |
| Iterator | Paged and range spliterators · Sayfalı ve aralık spliterator'ları | `iterator/spliterator/SpliteratorDemo.java` |
| Iterator | Stream gatherers, sessions · Akış toplayıcıları, oturumlar | `iterator/gatherers/GatherersDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m06-behavioral-algorithms verify           # examples + solutions
./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises     # your assignments · ödevleriniz
```
