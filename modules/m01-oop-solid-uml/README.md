# m01 — OOP, SOLID & UML · OOP, SOLID ve UML

> Week 2 · 2. Hafta — [Spec](../../specs/SPEC-m01-oop-solid-uml.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Sales report (SRP + OCP) | [EN](assignments/01-sales-report.en.md) | [TR](assignments/01-sales-report.tr.md) |
| Assignment 02 · Ödev 02 — Library loans (DIP + LSP) | [EN](assignments/02-library-loans.en.md) | [TR](assignments/02-library-loans.tr.md) |

## Examples · Örnekler

Every demo prints the **before** and the **after** version · Her demo **önce** ve **sonra** sürümünü yazdırır.

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/<path>`

| Principle · İlke | Scenario · Senaryo | Run · Çalıştır (`<path>`) |
|---|---|---|
| SRP | Invoice god class · Fatura god class'ı | `srp/SrpDemo.java` |
| SRP | Gradebook · Not defteri | `srp/GradeBookDemo.java` |
| OCP | Discount rules · İndirim kuralları | `ocp/OcpDemo.java` |
| OCP | Course sorting with `Comparator` · `Comparator` ile ders sıralama | `ocp/CourseSortDemo.java` |
| LSP | Rectangle / Square · Dikdörtgen / kare | `lsp/RectangleDemo.java` |
| LSP | Bank accounts · Banka hesapları | `lsp/AccountDemo.java` |
| ISP | Office devices · Ofis cihazları | `isp/IspDemo.java` |
| ISP | Product store · Ürün deposu | `isp/StoreDemo.java` |
| DIP | Notifications · Bildirimler | `dip/DipDemo.java` |
| DIP | `java.time.Clock` injection · `Clock` enjeksiyonu | `dip/ClockDemo.java` |
| Composition · Bileşim | Counting set (fragile base class) · Sayaçlı küme | `composition/CountingSetDemo.java` |
| Composition · Bileşim | Vehicles (class explosion) · Araçlar | `composition/VehicleDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m01-oop-solid-uml verify           # examples + solutions
./mvnw -pl modules/m01-oop-solid-uml test -Pexercises     # your assignments · ödevleriniz
```
